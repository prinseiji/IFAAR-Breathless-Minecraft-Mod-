package com.kodari.breathless;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * The lore documents: four readable folders / letters. Reading one for the first time unlocks its recipe
 * (through a hidden advancement) and the recipes stay locked until then (RecipeLockMixin).
 * They drop uncommonly from Stagnated and can be found in ancient cities. Vincent's note is different:
 * only a Stagnated that dies while burning drops it.
 */
public final class Lore {
   private Lore() {
   }

   /** id (texts and item name), the advancement that records the reading, the recipe path it unlocks ("" for none). */
   public record Doc(String id, String advancement, String recipe) {
   }

   public static final Doc[] DOCS = {
      new Doc("mandel", "lore_mandel", "gas_mask_filter_reinforced"),
      new Doc("mcnally", "lore_mcnally", "gas_mask_nv_empty"),
      new Doc("howard", "lore_howard", "tonic_strong"),
      new Doc("vincent", "for_those_who_find_these", ""),
   };
   public static final int VINCENT = 3;

   /** Chance that a Stagnated killed by a player drops a document they have not read yet. */
   public static final float DROP_CHANCE = 0.15F;
   /** Chance for Vincent's note when a Stagnated dies while on fire. */
   public static final float VINCENT_CHANCE = 0.20F;
   /** Chance for a document to be in an ancient city chest. */
   public static final float CHEST_CHANCE = 0.15F;

   private static final Random RANDOM = new Random();

   private static AdvancementHolder holder(ServerPlayer player, Doc doc) {
      return player.level().getServer().getAdvancements().get(Breathless.id(doc.advancement()));
   }

   public static boolean hasRead(ServerPlayer player, int index) {
      AdvancementHolder holder = holder(player, DOCS[index]);
      return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
   }

   /** Opens the document on the player's screen and marks it read (which unlocks its recipe the first time). */
   public static void read(ServerPlayer player, int index) {
      Achievements.award(player, DOCS[index].advancement());
      ServerPlayNetworking.send(player, new OpenLorePayload(index));
   }

   /** True while a recipe of this mod is locked behind a document this player has not read. Creative players are never locked. */
   public static boolean isLocked(ServerPlayer player, Identifier recipe) {
      if (player.isCreative() || !Breathless.MOD_ID.equals(recipe.getNamespace())) {
         return false;
      }
      for (int i = 0; i < DOCS.length; i++) {
         if (DOCS[i].recipe().equals(recipe.getPath())) {
            return !hasRead(player, i);
         }
      }
      return false;
   }

   /** For testing (/breathless lorereset): forget every document, locking the recipes again. */
   public static void reset(ServerPlayer player) {
      for (Doc doc : DOCS) {
         AdvancementHolder holder = holder(player, doc);
         if (holder != null) {
            player.getAdvancements().revoke(holder, "done");
         }
      }
   }

   public static void init() {
      ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
         if (!(entity instanceof Stagnated) || !(entity.level() instanceof ServerLevel level)) {
            return;
         }
         Player hurtBy = entity.getLastHurtByPlayer();
         if (!(hurtBy instanceof ServerPlayer player)) {
            return;
         }
         // Vincent immolated himself: only a Stagnated that burns to death carries his note.
         if (entity.isOnFire() && !hasRead(player, VINCENT) && RANDOM.nextFloat() < VINCENT_CHANCE) {
            entity.spawnAtLocation(level, new ItemStack(ModItems.LORE[VINCENT]));
         }
         if (RANDOM.nextFloat() < DROP_CHANCE) {
            List<Integer> unread = new ArrayList<>();
            for (int i = 0; i < VINCENT; i++) {
               if (!hasRead(player, i)) {
                  unread.add(i);
               }
            }
            if (!unread.isEmpty()) {
               entity.spawnAtLocation(level, new ItemStack(ModItems.LORE[unread.get(RANDOM.nextInt(unread.size()))]));
            }
         }
      });

      // Ancient city chests: one document now and then (never Vincent's).
      LootTableEvents.MODIFY.register((key, table, tableSource, registries) -> {
         if (tableSource.isBuiltin() && (key.equals(BuiltInLootTables.ANCIENT_CITY) || key.equals(BuiltInLootTables.ANCIENT_CITY_ICE_BOX))) {
            LootPool.Builder pool = LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(CHEST_CHANCE));
            for (int i = 0; i < VINCENT; i++) {
               pool.add(LootItem.lootTableItem(ModItems.LORE[i]));
            }
            table.withPool(pool);
         }
      });
   }
}
