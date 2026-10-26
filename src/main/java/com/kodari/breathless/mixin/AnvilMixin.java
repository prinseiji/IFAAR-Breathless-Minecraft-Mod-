package com.kodari.breathless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.kodari.breathless.ModItems;

/**
 * Eli's Sword cannot be enchanted at the anvil either: with it on the left, an enchanted book or any enchanted item on the right gives no result.
 * Renaming and repairing with diamonds still work. Optional: if this fails to apply, the sword's enchantments are still stripped by EliSummon.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMixin {
   @Inject(method = "createResult", at = @At("RETURN"))
   private void breathless$noEnchants(CallbackInfo ci) {
      ItemCombinerMenu menu = (ItemCombinerMenu)(Object)this;
      ItemStack left = menu.getSlot(0).getItem();
      ItemStack right = menu.getSlot(1).getItem();
      if (left.is(ModItems.ELI_SWORD) && !right.isEmpty() && (right.is(Items.ENCHANTED_BOOK) || right.isEnchanted() || right.has(DataComponents.STORED_ENCHANTMENTS))) {
         menu.getSlot(menu.getResultSlot()).set(ItemStack.EMPTY);
      }
   }
}
