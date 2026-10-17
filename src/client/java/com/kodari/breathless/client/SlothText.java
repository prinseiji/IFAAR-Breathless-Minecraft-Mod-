package com.kodari.breathless.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;

import com.kodari.breathless.Masks;
import com.kodari.breathless.Tuning;

/**
 * 5-L0 (SLOTH) cannot speak out loud. It can only reach you once it is in your head, so these lines only ever appear
 * while the exposure meter is above 0. The letters are stacked on top of each other like a tower, quiver in place,
 * and fade in and out in sculk blue. During a F13ND purge the letters glitch from blue to red as the spores burn away.
 *
 * Drawn with Minecraft's own font, scaled up by whole numbers.
 */
public final class SlothText {
   private SlothText() {
   }

   private static final int CYAN = 0x3CCBFF;
   private static final int RED = 0xFF2A2A;

   // ---- the lines (periods are left off on screen) ----
   private static final String[] FIRST_CONTACT = {"LEAVE"};
   private static final String[] STAGE_25 = {"BREATHE"};
   private static final String[] STAGE_50 = {"STAY STILL"};
   private static final String[] STAGE_75 = {"ALMOST"};
   private static final String[] CRITICAL = {"JOIN US", "DON'T RESIST"};
   private static final String[] SPRINT = {"LOUD", "STOP", "WALK", "NOISE", "QUIET"};
   private static final String[] MASKED = {"TOO LATE", "WE ARE STILL HERE", "CLEVER"};
   private static final String[] PURGE = {"IT BURNS", "THE FIEND", "STOP", "CHEATER", "MINE", "YOU"};

   private record Tower(String word, float xf, float yf, int start, int fadeIn, int hold, int fadeOut, boolean purge, int seed) {
      int total() {
         return fadeIn + hold + fadeOut;
      }
   }

   private static final List<Tower> ACTIVE = new ArrayList<>();
   private static final Random RNG = new Random();
   private static int clock;
   private static int lastSpoke = -1000000;
   private static String lastLine = "";
   private static float lastExposure;
   private static boolean lastMasked;
   private static boolean lastUnderground;
   private static int purgeSecondAt = -1;
   private static String purgeSecondLine = "";

   // ---------------------------------------------------------------- logic

   static void tick(Minecraft mc) {
      if (mc.player == null || mc.level == null) {
         ACTIVE.clear();
         lastExposure = 0.0F;
         lastMasked = false;
         lastUnderground = false;
         purgeSecondAt = -1;
         return;
      }
      clock++;
      ACTIVE.removeIf(t -> clock - t.start() > t.total());

      int ft = ClientState.fiendTick;
      boolean purging = ft >= 0 && ft < Tuning.POPUP_CLEAR_TICK;
      if (!purging) {
         // Once the bar is empty SLOTH is cut off.
         ACTIVE.removeIf(Tower::purge);
         purgeSecondAt = -1;
      }
      if (purging) {
         tickPurge(ft);
      }

      float e = ClientState.exposure;
      boolean maskedNow = Masks.isMask(mc.player.getItemBySlot(EquipmentSlot.HEAD));
      boolean undergroundNow = Masks.isUnderground(mc.player);
      boolean inHead = e >= 0.5F && ft < 0;

      if (inHead) {
         // First contact: the moment it gets into your head.
         if (lastExposure < 0.5F && Tuning.SLOTH_CHANCE_FIRST_CONTACT > RNG.nextFloat()) {
            speak(FIRST_CONTACT, false);
         }
         // Climbing through the stages.
         if (crossed(25.0F, e)) {
            roll(STAGE_25, Tuning.SLOTH_CHANCE_STAGE, false);
         }
         if (crossed(50.0F, e)) {
            roll(STAGE_50, Tuning.SLOTH_CHANCE_STAGE, false);
         }
         if (crossed(75.0F, e)) {
            roll(STAGE_75, Tuning.SLOTH_CHANCE_STAGE, false);
         }
         if (crossed(99.9F, e)) {
            roll(CRITICAL, Tuning.SLOTH_CHANCE_CRITICAL, true);
         }
         // Walking back into the spores with it still in you.
         if (undergroundNow && !lastUnderground) {
            roll(FIRST_CONTACT, Tuning.SLOTH_CHANCE_FIRST_CONTACT * 0.7F, false);
         }
         // Putting a mask on is rude.
         if (maskedNow && !lastMasked) {
            roll(MASKED, Tuning.SLOTH_CHANCE_MASK_ON, false);
         }
         // Once a second: running is loud, and hiding behind a mask is still hiding.
         if (clock % 20 == 0) {
            if (mc.player.isSprinting()) {
               roll(SPRINT, Tuning.SLOTH_CHANCE_SPRINT, false);
            } else if (maskedNow && Masks.hasWorkingMask(mc.player)) {
               roll(MASKED, Tuning.SLOTH_CHANCE_MASKED_IDLE, false);
            }
         }
      }
      lastExposure = e;
      lastMasked = maskedNow;
      lastUnderground = undergroundNow;
   }

   private static boolean crossed(float level, float now) {
      return lastExposure < level && now >= level;
   }

   private static void roll(String[] lines, float chance, boolean ignoreCooldown) {
      if (RNG.nextFloat() < chance) {
         speak(lines, ignoreCooldown);
      }
   }

   private static void speak(String[] lines, boolean ignoreCooldown) {
      if (!ignoreCooldown && clock - lastSpoke < Tuning.SLOTH_COOLDOWN_TICKS) {
         return;
      }
      String line = pick(lines, lastLine);
      lastLine = line;
      lastSpoke = clock;
      spawn(line, false, RNG.nextInt());
   }

   /** A random line that is not the one just spoken, if there is a choice. */
   private static String pick(String[] lines, String avoid) {
      if (lines.length == 1) {
         return lines[0];
      }
      for (int tries = 0; tries < 8; tries++) {
         String candidate = lines[RNG.nextInt(lines.length)];
         if (!candidate.equals(avoid)) {
            return candidate;
         }
      }
      return lines[0];
   }

   private static void tickPurge(int ft) {
      int first = Tuning.BUILDUP_START_TICK + 10;
      if (ft == first) {
         Random r = new Random(ClientState.fiendSeed + 99L);
         String one = PURGE[r.nextInt(PURGE.length)];
         boolean second = r.nextFloat() < Tuning.SLOTH_CHANCE_PURGE_SECOND;
         String two = one;
         for (int tries = 0; tries < 8 && two.equals(one); tries++) {
            two = PURGE[r.nextInt(PURGE.length)];
         }
         spawn(one, true, ClientState.fiendSeed);
         purgeSecondAt = second ? ft + 45 : -1;
         purgeSecondLine = two;
         lastSpoke = clock;
      }
      if (purgeSecondAt >= 0 && ft == purgeSecondAt) {
         spawn(purgeSecondLine, true, ClientState.fiendSeed * 7 + 3);
         purgeSecondAt = -1;
      }
   }

   /** Puts a line on screen. Multi-word lines are spread out: one tower per word, appearing one after another. */
   private static void spawn(String line, boolean purge, int seed) {
      String[] words = line.split(" ");
      Random r = new Random(seed);
      int n = words.length;
      for (int i = 0; i < n; i++) {
         float xf;
         if (purge) {
            // Keep to the right side; the pop-ups are on the left and the loading box is in the middle.
            xf = 0.80F + 0.16F * ((i + 0.2F + r.nextFloat() * 0.6F) / n);
         } else if (n == 1) {
            xf = r.nextBoolean() ? 0.07F + r.nextFloat() * 0.16F : 0.77F + r.nextFloat() * 0.16F;
         } else {
            xf = 0.08F + 0.84F * ((i + 0.15F + r.nextFloat() * 0.7F) / n);
         }
         float yf = r.nextFloat();
         int delay = i * 14;
         int fadeIn = purge ? 18 : Tuning.SLOTH_FADE_IN;
         int hold = purge ? 40 : Tuning.SLOTH_HOLD;
         int fadeOut = purge ? 25 : Tuning.SLOTH_FADE_OUT;
         ACTIVE.add(new Tower(words[i], xf, yf, clock + delay, fadeIn, hold, fadeOut, purge, r.nextInt()));
      }
   }

   // ---------------------------------------------------------------- drawing

   static void render(GuiGraphicsExtractor g, DeltaTracker tracker) {
      if (ACTIVE.isEmpty()) {
         return;
      }
      int w = g.guiWidth();
      int h = g.guiHeight();
      for (Tower t : ACTIVE) {
         int age = clock - t.start();
         if (age < 0) {
            continue;
         }
         float alpha;
         if (age < t.fadeIn()) {
            alpha = smooth(age / (float)t.fadeIn());
         } else if (age < t.fadeIn() + t.hold()) {
            alpha = 1.0F;
         } else {
            alpha = 1.0F - smooth((age - t.fadeIn() - t.hold()) / (float)t.fadeOut());
         }
         if (alpha <= 0.01F) {
            continue;
         }
         int k = pixelSize(h, t.word().length());
         int towerH = t.word().length() * 9 * k - 2 * k;
         int cx = Math.round(t.xf() * w);
         int top = Math.round(8 + t.yf() * Math.max(0, h - towerH - 16));
         float purgeProgress = t.purge() ? ClientState.buildupProgress() : 0.0F;
         drawTower(g, t, cx, top, k, alpha, purgeProgress);
      }
   }

   /** Biggest square size (1..3 screen pixels per font pixel) that keeps the tower under about 55% of the screen height. */
   private static int pixelSize(int screenH, int letters) {
      for (int k = 3; k > 1; k--) {
         if (letters * 9 * k - 2 * k <= screenH * 0.55F) {
            return k;
         }
      }
      return 1;
   }

   private static void drawTower(GuiGraphicsExtractor g, Tower t, int cx, int top, int k, float alpha, float purgeProgress) {
      String word = t.word();
      Font font = Minecraft.getInstance().font;
      for (int i = 0; i < word.length(); i++) {
         // Frantic quivering: every letter jumps a little every single tick.
         Random q = new Random(t.seed() * 131L + clock * 7L + i * 31L);
         int amp = Math.max(1, k);
         int jx = q.nextInt(2 * amp + 1) - amp;
         int jy = q.nextInt(amp + 1) - amp / 2;
         int color = CYAN;
         if (t.purge()) {
            // Blue glitching to red as the purge goes on, with sudden flickers back and forth.
            float mix = purgeProgress;
            if (q.nextFloat() < 0.14F) {
               mix = 1.0F - mix;
            }
            color = lerpColor(CYAN, RED, mix);
         }
         // Minecraft's own font, scaled up by whole numbers so the letters stay crisp and blocky.
         String ch = String.valueOf(word.charAt(i));
         float gw = font.width(ch) * (float)k;
         float x0 = cx - gw / 2.0F + jx;
         float y0 = top + i * 9 * k + jy;
         // A soft glow (four faint copies a pixel away), then the bright core.
         int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
         for (int[] o : offsets) {
            drawLetter(g, font, ch, x0 + o[0], y0 + o[1], k, argb((int)(46.0F * alpha), color));
         }
         drawLetter(g, font, ch, x0, y0, k, argb((int)(240.0F * alpha), color));
      }
   }

   private static void drawLetter(GuiGraphicsExtractor g, Font font, String ch, float x, float y, int k, int color) {
      // The game treats a nearly-invisible text colour as fully solid, so skip anything that faint.
      if ((color >>> 24) < 8) {
         return;
      }
      g.pose().pushMatrix();
      g.pose().translate(x, y);
      g.pose().scale((float)k, (float)k);
      g.text(font, ch, 0, 0, color, false);
      g.pose().popMatrix();
   }

   private static int lerpColor(int a, int b, float t) {
      float u = Math.max(0.0F, Math.min(1.0F, t));
      int r = (int)(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * u);
      int gr = (int)(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * u);
      int bl = (int)((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * u);
      return (r << 16) | (gr << 8) | bl;
   }

   private static int argb(int alpha, int rgb) {
      return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0xFFFFFF);
   }

   private static float smooth(float t) {
      float c = Math.max(0.0F, Math.min(1.0F, t));
      return c * c * (3.0F - 2.0F * c);
   }
}
