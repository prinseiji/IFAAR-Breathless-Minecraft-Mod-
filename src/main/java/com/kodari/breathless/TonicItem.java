package com.kodari.breathless;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A drinkable spore tonic (honey and wildflowers). Drinking works like an Instant Health potion for the spores:
 * with a working mask on it clears some of the meter right away; without one, a full meter remembers the cure for a while.
 * The empty glass bottle comes back through "usingConvertsTo".
 */
public class TonicItem extends Item {
   private final float purge;

   public TonicItem(Item.Properties properties, float purge) {
      super(properties);
      this.purge = purge;
   }

   @Override
   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      ItemStack result = super.finishUsingItem(stack, level, entity);
      if (!level.isClientSide() && entity instanceof ServerPlayer player) {
         Hazard.applyTonic(player, purge);
         Achievements.award(player, "ya_like_jazz");
      }
      return result;
   }
}
