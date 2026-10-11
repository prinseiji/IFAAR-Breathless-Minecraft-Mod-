package com.kodari.breathless;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Right-click a filter while wearing a Gas Mask (No Filter) to slot it in.
 * The mask takes over the filter's remaining life.
 */
public class FilterItem extends Item {
   public FilterItem(Item.Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack filter = player.getItemInHand(hand);
      ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
      if (!head.is(ModItems.GAS_MASK_EMPTY)) {
         return InteractionResult.PASS;
      }
      if (!level.isClientSide()) {
         ItemStack mask = new ItemStack(ModItems.GAS_MASK);
         mask.setDamageValue(Math.min(filter.getDamageValue(), mask.getMaxDamage() - 2));
         player.setItemSlot(EquipmentSlot.HEAD, mask);
         if (!player.isCreative()) {
            filter.shrink(1);
         }
         level.playSound(null, player.blockPosition(), SoundEvents.GLASS_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F);
      }
      return InteractionResult.SUCCESS;
   }
}
