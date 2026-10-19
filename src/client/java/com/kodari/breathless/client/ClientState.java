package com.kodari.breathless.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.DustParticleOptions;
import com.kodari.breathless.ModParticles;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.kodari.breathless.MaskFilters;
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
   /** Free-running tick counter for the haze pulses and floating spores. */
   public static int clock;

   // ---- F13ND pop-up sequence ----
   /** Ticks since the injection, or -1 when no sequence is running. */
   public static int fiendTick = -1;
   public static int fiendSeed;
   /** The bar value shown while the pop-ups are still covering it (the value from before the purge). */
   public static float fiendFrozen;

   // ---- visor cracks ----
   public static final int CRACK_ANIM_TICKS = 10;
   public static int crackAnim;
   /** The crack stage that just appeared (1 or 2) while crackAnim runs. */
   public static int crackAnimStage;
   private static int lastStage;
   /** Ticks left to keep showing the filter meter after a change (when not below Y=0). */
   public static int filterShow;
   private static int lastFirst;
   private static int lastSecond;
   private static boolean lastHeadWasMask;

   private static final Random RNG = new Random();
   // Small cave-style floating motes, a bright blue so they read against the dark fog; and big soft mist puffs.
   private static final DustParticleOptions SPORE = new DustParticleOptions(0x3CCBFF, 0.3F);
   private static final DustParticleOptions MIST = new DustParticleOptions(0x1F8FC4, 3.4F);
   // The bright surface of the gas, right at Y=0.
   private static final DustParticleOptions SURFACE = new DustParticleOptions(0x29DFEB, 5.0F);

   static void startFiend(int seed) {
      fiendFrozen = exposure;
      fiendSeed = seed;
      fiendTick = 0;
   }

   /** Progress (0..1) of the build-up: the bar drains and the pop-ups pile on during it. */
   public static float buildupProgress() {
      if (fiendTick < Tuning.BUILDUP_START_TICK) {
         return 0.0F;
      }
      return Math.min(1.0F, (fiendTick - Tuning.BUILDUP_START_TICK) / (float)Tuning.BUILDUP_TICKS);
   }

   /** The bar value to draw: it slowly drains from the old value to empty while the pop-ups pile on, then the real value takes over. */
   public static float shownExposure() {
      if (fiendTick >= 0 && fiendTick < Tuning.POPUP_CLEAR_TICK) {
         float p = buildupProgress();
         return fiendFrozen * (1.0F - p * p * (3.0F - 2.0F * p));
      }
      return exposure;
   }

   /** Crack stage (0, 1, 2) of the mask on this head slot item. */
   public static int stageOf(ItemStack head) {
      if (!Masks.isMask(head)) {
         return 0;
      }
      MaskFilters filters = Masks.getFilters(head);
      if (filters.count() <= 0) {
         return 0;
      }
      float weakest = filters.weakest();
      if (weakest < Tuning.CRACK_STAGE_2) {
         return 2;
      }
      return weakest < Tuning.CRACK_STAGE_1 ? 1 : 0;
   }

   static void tick(Minecraft mc) {
      if (mc.player == null || mc.level == null || !mc.player.isAlive()) {
         exposure = 0.0F;
         critTicks = 0;
         fiendTick = -1;
         crackAnim = 0;
         lastStage = 0;
         lastHeadWasMask = false;
         return;
      }
      clock++;
      int max = Tuning.FADE_SECONDS * 20;
      if (exposure >= 99.99F) {
         critTicks = Math.min(critTicks + 1, max);
      } else {
         critTicks = Math.max(0, critTicks - 6);
      }

      if (fiendTick >= 0 && ++fiendTick >= Tuning.POPUP_END_TICK) {
         fiendTick = -1;
      }

      // The visor cracks the moment the weakest filter crosses a threshold.
      ItemStack head = mc.player.getItemBySlot(EquipmentSlot.HEAD);
      boolean isMask = Masks.isMask(head);
      int stage = stageOf(head);
      if (isMask && lastHeadWasMask && stage > lastStage) {
         crackAnim = CRACK_ANIM_TICKS;
         crackAnimStage = stage;
      }
      // Show the filter meter for a while when a mask goes on or a filter goes in or out.
      MaskFilters held = isMask ? Masks.getFilters(head) : MaskFilters.EMPTY;
      boolean maskJustOn = isMask && !lastHeadWasMask;
      boolean changed = Math.abs(held.first() - lastFirst) > 40 || Math.abs(held.second() - lastSecond) > 40
         || (held.first() > 0) != (lastFirst > 0) || (held.second() > 0) != (lastSecond > 0);
      if (isMask && (maskJustOn || changed)) {
         filterShow = 160;
      } else if (filterShow > 0) {
         filterShow--;
      }
      lastFirst = held.first();
      lastSecond = held.second();
      lastStage = stage;
      lastHeadWasMask = isMask;
      if (crackAnim > 0) {
         crackAnim--;
      }

      spawnParticles(mc);
      SlothText.tick(mc);
   }

   private static void spawnParticles(Minecraft mc) {
      if (mc.level.dimension() != Level.OVERWORLD) {
         return;
      }
      double py = mc.player.getY();
      Vec3 eye = mc.player.getEyePosition();
      boolean masked = Masks.hasWorkingMask(mc.player);

      if (Masks.isUnderground(mc.player)) {
         // Tiny motes hanging in the air, like the dust in a cave.
         for (int i = 0; i < 8; i++) {
            double x = mc.player.getX() + (RNG.nextDouble() - 0.5D) * 22.0D;
            double y = py + (RNG.nextDouble() - 0.35D) * 8.0D;
            double z = mc.player.getZ() + (RNG.nextDouble() - 0.5D) * 22.0D;
            mc.level.addParticle(SPORE, x, y, z, (RNG.nextDouble() - 0.5D) * 0.02D, (RNG.nextDouble() - 0.5D) * 0.02D, (RNG.nextDouble() - 0.5D) * 0.02D);
         }
         // Small teal eyes hanging in the air all around (they glow, and fade in and out).
         if (clock % 2 == 0) {
            for (int i = 0; i < 2; i++) {
               double x = mc.player.getX() + (RNG.nextDouble() - 0.5D) * 20.0D;
               double y = py + (RNG.nextDouble() - 0.35D) * 7.0D;
               double z = mc.player.getZ() + (RNG.nextDouble() - 0.5D) * 20.0D;
               mc.level.addParticle(ModParticles.SPORE_EYE, x, y, z, (RNG.nextDouble() - 0.5D) * 0.012D, (RNG.nextDouble() - 0.5D) * 0.008D, (RNG.nextDouble() - 0.5D) * 0.012D);
            }
         }
         // With no working mask the spores come for you. These are "fly toward a point" particles
         // (like the ones around an enchanting table), so they really do stream into your face.
         // They sink 1.2 blocks by the end of their flight, so the target is raised by that much to land on your head,
         // and it is shifted ahead by your own movement so they keep up when you walk or run.
         if (!masked) {
            Vec3 move = mc.player.getDeltaMovement();
            int life = Tuning.SPORE_STREAM_TICKS;
            // They converge on a point just in front of your face (so they never block your view), a little lower than the eyes,
            // shifted ahead by your own movement so they keep up when you walk or run.
            double yaw = Math.toRadians(mc.player.getYRot());
            double pitch = Math.toRadians(mc.player.getXRot());
            double fx = -Math.sin(yaw) * Math.cos(pitch);
            double fy = -Math.sin(pitch);
            double fz = Math.cos(yaw) * Math.cos(pitch);
            double tx = eye.x + fx * 0.8D + move.x * life * 0.8D;
            double ty = eye.y + fy * 0.8D - 0.1D;
            double tz = eye.z + fz * 0.8D + move.z * life * 0.8D;
            for (int i = 0; i < 6; i++) {
               double angle = RNG.nextDouble() * Math.PI * 2.0D;
               double dist = 2.5D + RNG.nextDouble() * 4.5D;
               double dx = Math.cos(angle) * dist;
               double dy = (RNG.nextDouble() - 0.5D) * 2.5D;
               double dz = Math.sin(angle) * dist;
               // The "velocity" of a surge eye is where it starts, relative to the target point.
               mc.particleEngine.createParticle(ModParticles.SPORE_EYE_SURGE, tx, ty, tz, dx, dy, dz);
            }
         }
      }

      // The brine pool: a flat, bright, crisp surface exactly at Y=0 with sparse gas hanging below it.
      // These particles are created directly so they can be held perfectly still (no drifting), which keeps the top edge sharp.
      if (py < Tuning.HAZARD_Y + 24.0D && py > Tuning.HAZARD_Y - 40.0D) {
         double px = mc.player.getX();
         double pz = mc.player.getZ();
         // A light shimmer on the surface (the sheet itself is drawn by LevelSheet).
         for (int i = 0; i < 12; i++) {
            double x = px + (RNG.nextDouble() - 0.5D) * 44.0D;
            double z = pz + (RNG.nextDouble() - 0.5D) * 44.0D;
            double y = Tuning.HAZARD_Y - 0.04D - RNG.nextDouble() * 0.05D;
            spawnStill(mc, SURFACE, x, y, z, 80);
         }
         // The gas body under it: thinner, darker, hanging in the dark.
         for (int i = 0; i < 22; i++) {
            double x = px + (RNG.nextDouble() - 0.5D) * 44.0D;
            double z = pz + (RNG.nextDouble() - 0.5D) * 44.0D;
            double r = RNG.nextDouble();
            double y = Tuning.HAZARD_Y - 0.5D - r * r * 6.0D;
            spawnStill(mc, MIST, x, y, z, 90);
         }
      }
   }

   /** Spawns a particle that does not move and lives exactly this many ticks. */
   private static void spawnStill(Minecraft mc, DustParticleOptions options, double x, double y, double z, int lifetime) {
      Particle particle = mc.particleEngine.createParticle(options, x, y, z, 0.0D, 0.0D, 0.0D);
      if (particle != null) {
         particle.setParticleSpeed(0.0D, 0.0D, 0.0D);
         particle.setLifetime(lifetime);
      }
   }
}
