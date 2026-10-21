package com.kodari.breathless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.kodari.breathless.Lore;

/**
 * Recipes that belong to a lore document cannot be taken out of the crafting result slot until the document has been read.
 * Optional: if this ever fails to apply, the recipes are simply not locked.
 */
@Mixin(Slot.class)
public abstract class RecipeLockMixin {
   @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
   private void breathless$lock(Player player, CallbackInfoReturnable<Boolean> cir) {
      Slot self = (Slot)(Object)this;
      if (self.container instanceof ResultContainer results && player instanceof ServerPlayer serverPlayer) {
         RecipeHolder<?> recipe = results.getRecipeUsed();
         if (recipe != null && Lore.isLocked(serverPlayer, recipe.id().identifier())) {
            cir.setReturnValue(false);
         }
      }
   }
}
