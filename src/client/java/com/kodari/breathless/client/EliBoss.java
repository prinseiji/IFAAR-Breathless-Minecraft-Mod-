package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.ModSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
   private static final int BAR_H = 48;
   /** The hollow rail inside the picture that the health fills (pixels inside the texture). */
   private static final int RAIL_X = 32;
   private static final int RAIL_W = 78;
   private static final int RAIL_Y = 27;
   private static final int RAIL_H = 6;

   private static int phase;
   private static float health = 1.0F;
   private static int silent;
   /** A music track that can be faded out. */
   private static final class Track extends AbstractTickableSoundInstance {
      private boolean fading;

      Track(SoundEvent event) {
         super(event, SoundSource.MUSIC, RandomSource.create());
         this.attenuation = SoundInstance.Attenuation.NONE;
         this.relative = true;
         this.volume = 1.0F;
      }

      void fade() {
         this.fading = true;
      }

      @Override
      public void tick() {
         if (this.fading) {
            this.volume -= 0.04F;
            if (this.volume <= 0.0F) {
               this.stop();
            }
         }
      }
   }

   private static Track playing;
   private static int playingSince;
   private static boolean intro;
   private static int ticks;

   static void ping(int newPhase, float newHealth) {
      Minecraft mc = Minecraft.getInstance();
      silent = 0;
      health = newHealth;
      if (newPhase == 0) {
         fadeOut();
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
      playing = new Track(phase == 2 ? (intro ? ModSounds.ELI_TENEBRE_START : ModSounds.ELI_TENEBRE) : ModSounds.ELI_PHASE_ONE);
      mc.getSoundManager().play(playing);
      playingSince = ticks;
   }

   /** The fight is over: the music fades out over about a second instead of cutting off. */
   private static void fadeOut() {
      if (playing != null) {
         playing.fade();
      }
      playing = null;
      phase = 0;
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
      barAlpha = Math.max(0.0F, Math.min(1.0F, barAlpha + (phase == 2 ? 0.05F : -0.04F)));
      if (flash > 0) {
         flash--;
      }
      if (phase == 0) {
         return;
      }
      if (++silent > 60) {
         fadeOut();
         return;
      }
      // When a track ends the next one starts: the phase one song repeats, the phase two theme repeats from the start after the first 22-second-in run.
      if (playing != null && ticks - playingSince > 10 && !mc.getSoundManager().isActive(playing)) {
         intro = false;
         start(mc);
      }
   }

   private static int flash;
   /** Boss bar fade: 0 hidden .. 1 fully shown. It rises while phase two is on and falls after. */
   private static float barAlpha;

   static void parry() {
      flash = 8;
   }

   static void render(GuiGraphicsExtractor g, DeltaTracker tracker) {
      if (flash > 0) {
         g.fill(0, 0, g.guiWidth(), g.guiHeight(), ((Math.min(255, flash * 30)) << 24) | 0xFFFFFF);
      }
      if (barAlpha <= 0.0F) {
         return;
      }
      int al = Math.round(barAlpha * 255.0F);
      int x = (g.guiWidth() - BAR_W) / 2;
      int y = 1;
      g.fill(x + RAIL_X, y + RAIL_Y, x + RAIL_X + RAIL_W, y + RAIL_Y + RAIL_H, (Math.round(barAlpha * 102.0F) << 24));
      int fill = Math.round(RAIL_W * Math.max(0.0F, Math.min(1.0F, health)));
      if (fill > 0) {
         int fx = x + RAIL_X;
         int fy = y + RAIL_Y;
         g.fill(fx, fy, fx + fill, fy + RAIL_H, al << 24);
         g.fill(fx, fy, fx + fill, fy + 1, (al << 24) | 0x2B2B2B);
      }
      g.blit(RenderPipelines.GUI_TEXTURED, BAR, x, y, 0.0F, 0.0F, BAR_W, BAR_H, BAR_W, BAR_H, (al << 24) | 0xFFFFFF);
   }
}
