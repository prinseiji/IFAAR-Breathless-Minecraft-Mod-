package com.kodari.breathless;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * The Rotting Heart and Eli's Sword. A heart that lies in fire, lava or a lit campfire is destroyed: it shrieks, the fire goes out, the ground
 * around it turns to sculk and Elijah climbs out of it. Eli's Sword makes its victims bleed (Hemorrhage) and cannot be enchanted.
 */
public final class EliSummon {
   private EliSummon() {
   }

   private static final Set<ItemEntity> HEARTS = new HashSet<>();
   private static final List<Object[]> LATER = new ArrayList<>();

   public static void init() {
      ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
         if (entity instanceof ItemEntity item && item.getItem().is(ModItems.ROTTING_HEART)) {
            HEARTS.add(item);
         }
      });
      ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
         if (entity instanceof ItemEntity item) {
            HEARTS.remove(item);
         }
      });
      ServerTickEvents.END_SERVER_TICK.register(server -> {
         Iterator<ItemEntity> it = HEARTS.iterator();
         List<ItemEntity> burning = new ArrayList<>();
         while (it.hasNext()) {
            ItemEntity item = it.next();
            if (item.isRemoved()) {
               it.remove();
            } else if (item.tickCount % 4 == 0 && inFire(item)) {
               burning.add(item);
            }
         }
         for (ItemEntity item : burning) {
            if (item.isRemoved() || !(item.level() instanceof ServerLevel level)) {
               continue;
            }
            item.getItem().shrink(1);
            if (item.getItem().isEmpty()) {
               item.discard();
               HEARTS.remove(item);
            }
            summon(level, item.blockPosition());
         }
         for (Iterator<Object[]> l = LATER.iterator(); l.hasNext();) {
            Object[] task = l.next();
            if (--((int[])task[0])[0] <= 0) {
               ((Runnable)task[1]).run();
               l.remove();
            }
         }
      });
      // Eli's Sword: bleeding on a clean hit.
      ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
         if (!blocked && source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker && attacker.getMainHandItem().is(ModItems.ELI_SWORD)) {
            entity.addEffect(new MobEffectInstance(ModEffects.HEMORRHAGE, Tuning.HEMORRHAGE_TICKS, 0));
            if (attacker instanceof net.minecraft.server.level.ServerPlayer bleeder) {
               Achievements.award(bleeder, "bled_dry");
            }
         }
      });
      // Parry: blocking with Eli's Sword right as the hit lands (first PARRY_TICKS of the block) cancels it, knocks the attacker back, flashes white and freezes time for half a second.
      ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
         // Only Eli's Sword parries count toward his rage (never against the stun slam).
         if (entity instanceof net.minecraft.server.level.ServerPlayer blocker && source.getEntity() instanceof Stagnated eli && eli.isSweaty() && !eli.isStunSmash() && blocker.isBlocking()) {
            int held = blocker.getTicksUsingItem();
            boolean swordBlock = blocker.getUseItem().is(ModItems.ELI_SWORD) && held <= Tuning.PARRY_TICKS;
            if (swordBlock) {
               eli.onParried(eli.getMainHandItem().is(net.minecraft.world.item.Items.MACE));
            }
         }
         if (entity instanceof net.minecraft.server.level.ServerPlayer player && player.isBlocking() && player.getUseItem().is(ModItems.ELI_SWORD)
            && player.getTicksUsingItem() <= Tuning.PARRY_TICKS && source.getEntity() != null && !(source.getEntity() instanceof Stagnated st && st.isStunSmash()) && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)
            && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_SHIELD)) {
            if (source.getEntity() instanceof Stagnated st && st.isSweaty()) {
               Achievements.award(player, "stop_hitting_yourself");
            }
            float after = amount * (1.0F - Math.min(20.0F, (float)player.getArmorValue()) / 25.0F);
            if (after >= player.getHealth() + player.getAbsorptionAmount()) {
               Achievements.award(player, "plus_parry");
            }
            parry(player, source.getEntity());
            return false;
         }
         return true;
      });
      // Whatever got onto the sword anyway (an older anvil enchant, a command) is taken off again.
      net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
         if (server.getTickCount() % 20 != 0) {
            return;
         }
         for (net.minecraft.server.level.ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
               net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
               if (stack.is(ModItems.ELI_SWORD) && stack.isEnchanted()) {
                  stack.remove(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
               }
            }
         }
      });
      EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> target.is(ModItems.ELI_SWORD) ? TriState.FALSE : TriState.DEFAULT);
      // Ancient city chests now and then hold a heart.
      LootTableEvents.MODIFY.register((key, table, tableSource, registries) -> {
         if (tableSource.isBuiltin() && (key.equals(BuiltInLootTables.ANCIENT_CITY) || key.equals(BuiltInLootTables.ANCIENT_CITY_ICE_BOX))) {
            table.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(Tuning.HEART_CHEST_CHANCE))
               .add(LootItem.lootTableItem(ModItems.ROTTING_HEART)));
         }
      });
   }

   private static void parry(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.entity.Entity attacker) {
      ServerLevel level = (ServerLevel)player.level();
      level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.PARRY, SoundSource.PLAYERS, 1.6F, 1.0F);
      level.sendParticles(ParticleTypes.CRIT, player.getX(), player.getEyeY(), player.getZ(), 20, 0.4, 0.3, 0.4, 0.3);
      net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new ParryPayload());
      if (attacker instanceof LivingEntity victim) {
         double dx = victim.getX() - player.getX();
         double dz = victim.getZ() - player.getZ();
         double dl = Math.max(0.01, Math.sqrt(dx * dx + dz * dz));
         victim.setDeltaMovement(victim.getDeltaMovement().add(dx / dl * Tuning.PARRY_KNOCKBACK, 0.3, dz / dl * Tuning.PARRY_KNOCKBACK));
         victim.hurtMarked = true;
      }
      player.invulnerableTime = Math.max(player.invulnerableTime, 10);
      net.minecraft.server.ServerTickRateManager rate = level.getServer().tickRateManager();
      if (!rate.isFrozen()) {
         rate.setFrozen(true);
         later(Tuning.PARRY_FREEZE_TICKS, () -> rate.setFrozen(false));
      }
   }

   /** Natural ground only: the sculk never eats a player's builds. */
   public static boolean natural(BlockState state) {
      return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY)
         || state.is(Blocks.TUFF) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.MUD) || state.is(Blocks.GRASS_BLOCK);
   }

   /** A splotch of this many sculk blocks on the natural ground around a spot, nearest first. */
   public static void splotch(ServerLevel level, BlockPos center, int count) {
      java.util.List<BlockPos> spots = new ArrayList<>();
      int r = 5;
      for (int dx = -r; dx <= r; dx++) {
         for (int dz = -r; dz <= r; dz++) {
            for (int dy = 2; dy >= -3; dy--) {
               BlockPos p = center.offset(dx, dy, dz);
               BlockState here = level.getBlockState(p);
               if ((here.isAir() || here.canBeReplaced()) && natural(level.getBlockState(p.below()))) {
                  spots.add(p.below());
                  break;
               }
            }
         }
      }
      spots.sort(java.util.Comparator.comparingDouble(p -> p.distSqr(center) + level.getRandom().nextDouble() * 3.0));
      for (int i = 0; i < Math.min(count, spots.size()); i++) {
         level.setBlock(spots.get(i), Blocks.SCULK.defaultBlockState(), 3);
      }
   }

   private static boolean inFire(ItemEntity item) {
      ServerLevel level = (ServerLevel)item.level();
      BlockPos pos = item.blockPosition();
      BlockState state = level.getBlockState(pos);
      return state.is(BlockTags.FIRE) || (state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT))
         || level.getFluidState(pos).is(FluidTags.LAVA) || item.isOnFire();
   }

   private static void later(int ticks, Runnable run) {
      LATER.add(new Object[] {new int[] {ticks}, run});
   }

   /** The first air spot above solid ground in this column, looking a few blocks up and down from y. Null when there is none. */
   private static BlockPos groundAt(ServerLevel level, int x, int y, int z) {
      for (int dy = 3; dy >= -3; dy--) {
         BlockPos p = new BlockPos(x, y + dy, z);
         BlockState here = level.getBlockState(p);
         BlockState below = level.getBlockState(p.below());
         if ((here.isAir() || here.canBeReplaced()) && here.getFluidState().isEmpty() && !below.isAir() && !below.canBeReplaced() && below.getFluidState().isEmpty()) {
            return p;
         }
      }
      return null;
   }

   private static boolean blocked(ServerLevel level, BlockPos p) {
      return !level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(p)).isEmpty();
   }

   /**
    * A low mound of sculk heaped on top of the ground around a spot, with sensors on it and a catalyst beside it. Nothing is replaced, and nothing is put where something stands.
    * Returns the y that Elijah stands at on top of it, or -1 when there is no ground in the middle (then the caller keeps the old behaviour).
    */
   private static int mound(ServerLevel level, BlockPos pos) {
      BlockPos mid = groundAt(level, pos.getX(), pos.getY(), pos.getZ());
      if (mid == null) {
         return -1;
      }
      int r = Tuning.MOUND_RADIUS;
      int top = mid.getY();
      for (int dx = -r; dx <= r; dx++) {
         for (int dz = -r; dz <= r; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > r) {
               continue;
            }
            BlockPos g = groundAt(level, pos.getX() + dx, mid.getY(), pos.getZ() + dz);
            if (g == null || Math.abs(g.getY() - mid.getY()) > 2) {
               continue;
            }
            int h = (int)Math.round(Tuning.MOUND_HEIGHT * (1.0 - d / (r + 1.0)));
            for (int i = 0; i < h; i++) {
               BlockPos b = g.above(i);
               BlockState st = level.getBlockState(b);
               if (!(st.isAir() || st.canBeReplaced()) || !st.getFluidState().isEmpty() || blocked(level, b)) {
                  break;
               }
               level.setBlock(b, Blocks.SCULK.defaultBlockState(), 3);
               if (dx == 0 && dz == 0) {
                  top = b.getY() + 1;
               }
            }
         }
      }
      // Two sensors on the slope and a catalyst on the ground beside it.
      int[][] sensors = {{2, 1}, {-2, -1}};
      for (int[] s : sensors) {
         BlockPos g = groundAt(level, pos.getX() + s[0], mid.getY(), pos.getZ() + s[1]);
         if (g == null) {
            continue;
         }
         BlockPos on = g;
         while (level.getBlockState(on).is(Blocks.SCULK)) {
            on = on.above();
         }
         if (level.getBlockState(on).isAir() && !blocked(level, on)) {
            level.setBlock(on, Blocks.SCULK_SENSOR.defaultBlockState(), 3);
         }
      }
      BlockPos cat = groundAt(level, pos.getX() + r + 1, mid.getY(), pos.getZ() - 1);
      if (cat != null && level.getBlockState(cat).isAir() && !blocked(level, cat)) {
         level.setBlock(cat, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
      }
      return top;
   }

   /** The heart bursts at this spot: shriek, put out the fire, sculk splotch, and Elijah arrives. */
   public static void summon(ServerLevel level, BlockPos pos) {
      double x = pos.getX() + 0.5;
      double y = pos.getY() + 0.3;
      double z = pos.getZ() + 0.5;
      level.playSound(null, x, y, z, ModSounds.HEART_DESTROYED, SoundSource.HOSTILE, 2.0F, 1.0F);
      level.playSound(null, x, y, z, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.0F, 0.7F);
      for (int i = 0; i < 3; i++) {
         final int delay = i * 6;
         level.sendParticles(new ShriekParticleOption(delay), x, y + 0.2, z, 1, 0.0, 0.0, 0.0, 0.0);
      }
      level.sendParticles(ParticleTypes.SCULK_SOUL, x, y + 0.5, z, 50, 0.5, 0.7, 0.5, 0.05);
      level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, x, y + 0.2, z, 30, 0.8, 0.3, 0.8, 0.05);
      // Fire out.
      for (BlockPos near : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
         BlockState st = level.getBlockState(near);
         if (st.is(BlockTags.FIRE)) {
            level.setBlock(near, Blocks.AIR.defaultBlockState(), 3);
         } else if (st.is(BlockTags.CAMPFIRES) && st.hasProperty(CampfireBlock.LIT) && st.getValue(CampfireBlock.LIT)) {
            level.setBlock(near, st.setValue(CampfireBlock.LIT, false), 3);
         }
      }
      // Sculk splotch.
      int r = Tuning.HEART_SPLOTCH_RADIUS;
      int surface = pos.getY();
      for (int dx = -r; dx <= r; dx++) {
         for (int dz = -r; dz <= r; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > r - level.getRandom().nextDouble() * 1.6) {
               continue;
            }
            for (int dy = 2; dy >= -3; dy--) {
               BlockPos p = pos.offset(dx, dy, dz);
               BlockState here = level.getBlockState(p);
               BlockState below = level.getBlockState(p.below());
               if ((here.isAir() || here.canBeReplaced()) && natural(below)) {
                  level.setBlock(p.below(), Blocks.SCULK.defaultBlockState(), 3);
                  if (dx == 0 && dz == 0) {
                     surface = p.getY();
                  }
                  break;
               }
            }
         }
      }
      // The mound he climbs out of (on top of the ground, nothing replaced).
      int moundTop = mound(level, pos);
      if (moundTop >= 0) {
         surface = moundTop;
      }
      // Elijah.
      Stagnated eli = ModEntities.STAGNATED.create(level, EntitySpawnReason.EVENT);
      if (eli == null) {
         return;
      }
      eli.snapTo(x, surface, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
      eli.makeSweaty();
      eli.setSummoned();
      if (Tuning.SUMMON_OPENER_SLAM) {
         eli.setOpenerSlam();
      }
      level.addFreshEntity(eli);
      Achievements.awardNear(eli, 32.0, "here_burns_the_wicked");
      eli.startEmerging();
      eli.pingBoss(level, 1);
      level.playSound(null, x, y, z, ModSounds.ETHAN_SUMMON, SoundSource.HOSTILE, 3.0F, 1.0F);
      Component name = Component.literal("xX_ELIJ4H_Xx");
      level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("multiplayer.player.joined", name).withStyle(ChatFormatting.YELLOW), false);
      later(25, () -> level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("chat.type.text", name, "You called?"), false));
   }
}
