package com.kodari.breathless.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import com.kodari.breathless.Masks;
import com.kodari.breathless.Tuning;

/** What the client knows about the hazard. The server owns the real exposure value. */
public final class ClientState {
   private ClientState() {
   }

   /** Last exposure value received from the server (0..100). */
   public static float exposure;
   /** Counts up while critical (drives the black fade and the creeping veins), counts down fast once cured. */
   public static int critTicks;

   static void tick(Minecraft mc) {
      if (mc.player == null || mc.level == null) {
         exposure = 0.0F;
         critTicks = 0;
         return;
      }
      int max = Tuning.FADE_SECONDS * 20;
      if (exposure >= 99.99F) {
         critTicks = Math.min(critTicks + 1, max);
      } else {
         critTicks = Math.max(0, critTicks - 6);
      }

      // Ambient drifting spores around the player while below Y=0.
      if (Masks.isUnderground(mc.player) && mc.level.dimension() == Level.OVERWORLD) {
         for (int i = 0; i < 4; i++) {
            double x = mc.player.getX() + (mc.level.getRandom().nextDouble() - 0.5D) * 18.0D;
            double y = mc.player.getY() + (mc.level.getRandom().nextDouble() - 0.3D) * 7.0D;
            double z = mc.player.getZ() + (mc.level.getRandom().nextDouble() - 0.5D) * 18.0D;
            mc.level.addParticle(ParticleTypes.WARPED_SPORE, x, y, z, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
