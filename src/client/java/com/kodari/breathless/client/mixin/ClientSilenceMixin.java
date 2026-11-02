package com.kodari.breathless.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import com.kodari.breathless.Silence;

/**
 * Your own sounds (footsteps, placing and breaking, eating...) are played by your client when it is the one that made them.
 * Under Silence they are dropped here. Optional: if the method ever changes, the mod still loads and only this part stops working.
 */
@Mixin(ClientLevel.class)
public abstract class ClientSilenceMixin {
   @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
   private void breathless$hushAt(Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed, CallbackInfo ci) {
      if (Silence.hushes(except)) {
         ci.cancel();
      }
   }

   @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
   private void breathless$hushFrom(Entity except, Entity from, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed, CallbackInfo ci) {
      if (Silence.hushes(from) || Silence.hushes(except)) {
         ci.cancel();
      }
   }
}
