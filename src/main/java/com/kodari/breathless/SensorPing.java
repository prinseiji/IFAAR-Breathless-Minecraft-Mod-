package com.kodari.breathless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** The sign that a sculk creature heard a sensor: a trail of blue souls from the sensor to it, and a pop where the signal lands. */
public final class SensorPing {
   private SensorPing() {
   }

   public static void ping(ServerLevel level, BlockPos sensor, Entity listener) {
      Vec3 from = Vec3.atCenterOf(sensor).add(0.0, 0.4, 0.0);
      Vec3 to = listener.position().add(0.0, listener.getBbHeight() * 0.6, 0.0);
      int steps = Math.max(6, (int)(from.distanceTo(to) * 3.0));
      for (int i = 0; i <= steps; i++) {
         Vec3 p = from.lerp(to, i / (double)steps);
         level.sendParticles(ParticleTypes.SCULK_SOUL, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
      }
      level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, to.x, to.y, to.z, 10, 0.4, 0.4, 0.4, 0.02);
      level.playSound(null, to.x, to.y, to.z, SoundEvents.SCULK_CLICKING, SoundSource.HOSTILE, 1.0F, 1.0F);
   }
}
