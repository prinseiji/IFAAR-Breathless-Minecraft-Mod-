package com.kodari.breathless.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import com.kodari.breathless.Masks;
import com.kodari.breathless.Tuning;
import com.kodari.breathless.client.ClientState;

/** The real fog: white with a dark cyan tint below Y=0, thicker as your exposure climbs. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
   @Inject(method = "setupFog", at = @At("RETURN"))
   private void breathless$sporeFog(Camera camera, int renderDistance, DeltaTracker tracker, float partialTick, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null || level.dimension() != Level.OVERWORLD) {
         return;
      }
      double y = mc.player.getY();
      if (y >= Tuning.HAZARD_Y) {
         return;
      }
      FogData fog = cir.getReturnValue();
      float blend = (float)Math.min(1.0D, -y / 6.0D);
      // 26 blocks of sight at first, closing in to 11 at full exposure. A working mask helps you see a bit further.
      float end = Mth.lerp(Math.min(1.0F, ClientState.exposure / 100.0F), 26.0F, 11.0F);
      if (Masks.hasWorkingMask(mc.player)) {
         end += 8.0F;
      }
      fog.environmentalStart = Mth.lerp(blend, fog.environmentalStart, 0.0F);
      fog.environmentalEnd = Mth.lerp(blend, fog.environmentalEnd, end);
      fog.renderDistanceStart = Mth.lerp(blend, fog.renderDistanceStart, 0.0F);
      fog.renderDistanceEnd = Mth.lerp(blend, fog.renderDistanceEnd, end);
      fog.color.x = Mth.lerp(blend, fog.color.x, 0.62F);
      fog.color.y = Mth.lerp(blend, fog.color.y, 0.82F);
      fog.color.z = Mth.lerp(blend, fog.color.z, 0.84F);
   }
}
