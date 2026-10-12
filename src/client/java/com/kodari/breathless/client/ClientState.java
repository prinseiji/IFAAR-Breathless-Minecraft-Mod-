package com.kodari.breathless.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
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
   private static boolean lastHeadWasMask;

   private static final Random RNG = new Random();
   private static final DustParticleOptions SPORE = new DustParticleOptions(0x2FB4E6, 1.1F);
   private static final DustParticleOptions MIST = new DustParticleOptions(0x2A8FD0, 2.4F);

   static void startFiend(int seed) {
      fiendFrozen = exposure;
      fiendSeed = seed;
      fiendTick = 0;
   }

   /** The bar value to draw: frozen at the old value until the pop-ups lift. */
   public static float shownExposure() {
      if (fiendTick >= 0 && fiendTick < Tuning.POPUP_CLEAR_TICK) {
         return fiendFrozen;
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
      if (mc.player == null || mc.level == null) {
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
      lastStage = stage;
      lastHeadWasMask = isMask;
      if (crackAnim > 0) {
         crackAnim--;
      }

      spawnParticles(mc);
   }

   private static void spawnParticles(Minecraft mc) {
      if (mc.level.dimension() != Level.OVERWORLD) {
         return;
      }
      double py = mc.player.getY();
      Vec3 eye = mc.player.getEyePosition();
      boolean masked = Masks.hasWorkingMask(mc.player);

      // Spores in the air below Y=0. Without a mask they stream toward your face.
      if (Masks.isUnderground(mc.player)) {
         for (int i = 0; i < 9; i++) {
            double x = mc.player.getX() + (RNG.nextDouble() - 0.5D) * 16.0D;
            double y = py + (RNG.nextDouble() - 0.35D) * 6.0D;
            double z = mc.player.getZ() + (RNG.nextDouble() - 0.5D) * 16.0D;
            double vx = (RNG.nextDouble() - 0.5D) * 0.01D;
            double vy = (RNG.nextDouble() - 0.5D) * 0.01D;
            double vz = (RNG.nextDouble() - 0.5D) * 0.01D;
            if (!masked) {
               Vec3 toward = eye.subtract(x, y, z);
               double len = Math.max(0.5D, toward.length());
               double pull = 0.07D;
               vx += toward.x / len * pull;
               vy += toward.y / len * pull;
               vz += toward.z / len * pull;
            }
            mc.level.addParticle(SPORE, x, y, z, vx, vy, vz);
         }
      }

      // The mist sheet: a thick blue layer lying on the Y=0 line, where the gas pools.
      if (py < Tuning.HAZARD_Y + 14.0D && py > Tuning.HAZARD_Y - 40.0D) {
         for (int i = 0; i < 10; i++) {
            double x = mc.player.getX() + (RNG.nextDouble() - 0.5D) * 26.0D;
            double z = mc.player.getZ() + (RNG.nextDouble() - 0.5D) * 26.0D;
            double y = Tuning.HAZARD_Y - 0.9D + RNG.nextDouble() * 1.1D;
            mc.level.addParticle(MIST, x, y, z, (RNG.nextDouble() - 0.5D) * 0.012D, 0.0D, (RNG.nextDouble() - 0.5D) * 0.012D);
         }
      }
   }
}
