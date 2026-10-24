package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.ModSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;

/**
 * Elijah's fight on the client: phase one music, the phase two theme (it starts at its 22 second mark, then repeats from the start),
 * and the boss bar that shows in phase two only. Everything stops when the server stops sending updates for 3 seconds.
 */
final class EliBoss {
   private EliBoss() {
   }

   private static final Identifier BAR = Breathless.id("textures/gui/eli_bar.png");
   private static final int BAR_W = 144;
   private static final int BAR_H = 52;
   /** The hollow rail inside the picture that the health fills (pixels inside the texture). */
   private static final int RAIL_X = 22;
   private static final int RAIL_W = 98;
   private static final int RAIL_Y = 30;
   private static final int RAIL_H = 5;

   private static int phase;
   private static float health = 1.0F;
   private static int silent;
   private static SimpleSoundInstance playing;
   private static int playingSince;
   private static boolean intro;
   private static int ticks;

   static void ping(int newPhase, float newHealth) {
      Minecraft mc = Minecraft.getInstance();
      silent = 0;
      health = newHealth;
      if (newPhase == 0) {
         stop(mc);
         return;
      }
      if (newPhase != phase) {
         stop(mc);
         phase = newPhase;
         intro = newPhase == 2;
         start(mc);
      }
   }

   private static void start(Minecraft mc) {
      if (phase == 0) {
         return;
      }
      playing = SimpleSoundInstance.forMusic(phase == 2 ? (intro ? ModSounds.ELI_TENEBRE_START : ModSounds.ELI_TENEBRE) : ModSounds.ELI_PHASE_ONE);
      mc.getSoundManager().play(playing);
      playingSince = ticks;
   }

   private static void stop(Minecraft mc) {
      if (playing != null) {
         mc.getSoundManager().stop(playing);
      }
      playing = null;
      phase = 0;
   }

   static void tick(Minecraft mc) {
      ticks++;
      if (flash > 0) {
         flash--;
      }
      if (phase == 0) {
         return;
      }
      if (++silent > 60) {
         stop(mc);
         return;
      }
      // When a track ends the next one starts: the phase one song repeats, the phase two theme repeats from the start after the first 22-second-in run.
      if (playing != null && ticks - playingSince > 10 && !mc.getSoundManager().isActive(playing)) {
         intro = false;
         start(mc);
      }
   }

   private static int flash;

   static void parry() {
      flash = 8;
   }

   static void render(GuiGraphicsExtractor g, DeltaTracker tracker) {
      if (flash > 0) {
         g.fill(0, 0, g.guiWidth(), g.guiHeight(), ((Math.min(255, flash * 30)) << 24) | 0xFFFFFF);
      }
      if (phase != 2) {
         return;
      }
      int x = (g.guiWidth() - BAR_W) / 2;
      int y = 1;
      g.fill(x + RAIL_X, y + RAIL_Y, x + RAIL_X + RAIL_W, y + RAIL_Y + RAIL_H, 0x66000000);
      int fill = Math.round(RAIL_W * Math.max(0.0F, Math.min(1.0F, health)));
      if (fill > 0) {
         int fx = x + RAIL_X;
         int fy = y + RAIL_Y;
         g.fill(fx, fy, fx + fill, fy + RAIL_H, 0xFF000000);
         g.fill(fx, fy, fx + fill, fy + 1, 0xFF2B2B2B);
      }
      g.blit(RenderPipelines.GUI_TEXTURED, BAR, x, y, 0.0F, 0.0F, BAR_W, BAR_H, BAR_W, BAR_H);
   }
}
