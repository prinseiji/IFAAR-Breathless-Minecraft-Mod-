package com.kodari.breathless;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Small helpers shared by the player hazard and the mob rules. */
public final class Masks {
   private Masks() {
   }

   /** True if the entity wears a Gas Mask whose filter still has life. */
   public static boolean hasWorkingMask(LivingEntity entity) {
      ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
      return head.is(ModItems.GAS_MASK) && head.getDamageValue() < head.getMaxDamage() - 1;
   }

   /** Overworld only: the nether and end have no spore zone. */
   public static boolean isHazardLevel(Level level) {
      return level.dimension() == Level.OVERWORLD;
   }

   public static boolean isUnderground(Entity entity) {
      return isHazardLevel(entity.level()) && entity.getY() < Tuning.HAZARD_Y;
   }

   /** Registry id of the entity's type, e.g. "minecraft:zombie". Empty string if unknown. */
   public static String typeId(Entity entity) {
      Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
      return key == null ? "" : key.toString();
   }

   /** Wears the player's filter down. When it runs dry the mask turns into an empty mask. */
   public static void drainFilter(ServerPlayer player, int amount) {
      ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
      if (!head.is(ModItems.GAS_MASK)) {
         return;
      }
      int damage = head.getDamageValue() + amount;
      if (damage >= head.getMaxDamage() - 1) {
         player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.GAS_MASK_EMPTY));
         player.level().playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.8F, 1.2F);
         player.sendOverlayMessage(Component.literal("Your filter is spent!"));
      } else {
         head.setDamageValue(damage);
      }
   }
}
