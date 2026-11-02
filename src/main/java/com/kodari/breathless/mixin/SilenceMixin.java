package com.kodari.breathless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import com.kodari.breathless.Silence;

/**
 * Game events are what sculk sensors, shriekers and Wardens listen to. An entity under Silence sends none, and the sounds it makes are not played for other players.
 * Optional: if the method ever changes, the mod still loads and only this part stops working.
 */
@Mixin(ServerLevel.class)
public abstract class SilenceMixin {
   @Inject(method = "gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V", at = @At("HEAD"), cancellable = true)
   private void breathless$hush(Holder<GameEvent> event, Vec3 pos, GameEvent.Context context, CallbackInfo ci) {
      if (context.sourceEntity() instanceof LivingEntity who && Silence.silenced(who)) {
         ci.cancel();
      }
   }

   /** Sounds the silenced player makes (footsteps, placing, eating...) are not sent to anyone else. */
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
