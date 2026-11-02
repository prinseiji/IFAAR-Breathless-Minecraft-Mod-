package com.kodari.breathless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import com.kodari.breathless.Silence;

/**
 * Game events are what sculk sensors, shriekers and Wardens listen to. An entity under Silence sends none.
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
}
