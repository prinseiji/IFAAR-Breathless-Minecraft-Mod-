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
   private static final int BAR_W = 117;
   private static final int BAR_H = 37;
   /** The hollow rail inside the picture that the health fills (pixels inside the texture). */
   private static final int RAIL_X = 25;
   private static final int RAIL_W = 65;
   private static final int RAIL_Y = 21;
   private static final int RAIL_H = 5;

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
         if (newPhase == 2 && phase == 1) {
            popFlash = 16;
         }
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

   /** Vanilla background music stays off while the boss theme plays (and for a few seconds after, so it does not cut in during the fade). */
   private static int hush;

   static void silenceVanilla(Minecraft mc) {
      if (phase != 0) {
         hush = 100;
      }
      if (hush > 0) {
         hush--;
         mc.getMusicManager().stopPlaying();
      }
   }

   static void tick(Minecraft mc) {
      silenceVanilla(mc);
      ticks++;
      if (popFlash > 0) {
         popFlash--;
      }
      fxTicks++;
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
   /** The totem pop flash (dark cyan), ticks left. */
   private static int popFlash;
   private static int fxTicks;
   /** Boss bar fade: 0 hidden .. 1 fully shown. It rises while phase two is on and falls after. */
   private static float barAlpha;

   static void parry() {
      flash = 8;
   }

   static void render(GuiGraphicsExtractor g, DeltaTracker tracker) {
      if (flash > 0) {
         g.fill(0, 0, g.guiWidth(), g.guiHeight(), ((Math.min(255, flash * 30)) << 24) | 0xFFFFFF);
      }
      if (popFlash > 0) {
         // Quick: up in 3 ticks, then fading out.
         float t = popFlash > 13 ? (16 - popFlash) / 3.0F : popFlash / 13.0F;
         g.fill(0, 0, g.guiWidth(), g.guiHeight(), (Math.round(t * 150.0F) << 24) | 0x0F7F7A);
      }
      if (barAlpha > 0.0F) {
         edgeVeins(g);
      }
      if (barAlpha <= 0.0F) {
         return;
      }
      int al = Math.round(barAlpha * 255.0F);
      int x = (g.guiWidth() - BAR_W) / 2;
      int y = 1;
      g.fill(x + RAIL_X, y + RAIL_Y, x + RAIL_X + RAIL_W, y + RAIL_Y + RAIL_H, (Math.round(barAlpha * 200.0F) << 24) | 0x6A6A6A);
      int fill = Math.round(RAIL_W * Math.max(0.0F, Math.min(1.0F, health)));
      if (fill > 0) {
         int fx = x + RAIL_X;
         int fy = y + RAIL_Y;
         g.fill(fx, fy, fx + fill, fy + RAIL_H, al << 24);
         g.fill(fx, fy, fx + fill, fy + 1, (al << 24) | 0x2B2B2B);
      }
      g.blit(RenderPipelines.GUI_TEXTURED, BAR, x, y, 0.0F, 0.0F, BAR_W, BAR_H, 233, 73, 233, 73, (al << 24) | 0xFFFFFF);
   }

   /** Phase two: a slow dark cyan pulse with veins creeping in from the edges of the screen. */
   private static void edgeVeins(GuiGraphicsExtractor g) {
      int w = g.guiWidth();
      int h = g.guiHeight();
      float pulse = 0.5F + 0.5F * (float)Math.sin(fxTicks * 0.16F);
      float a = barAlpha * (0.35F + 0.65F * pulse);
      // soft glow along the edges
      for (int i = 0; i < 14; i++) {
         int al = Math.round(Math.min(1.0F, a * (1.0F - i / 14.0F) * 1.25F) * 190.0F);
         int c = (al << 24) | 0x0F7F7A;
         g.fill(0, i * 4, w, i * 4 + 4, c);
         g.fill(0, h - i * 4 - 4, w, h - i * 4, c);
         g.fill(i * 4, 0, i * 4 + 4, h, c);
         g.fill(w - i * 4 - 4, 0, w - i * 4, h, c);
      }
      // veins: fixed random walks that start on an edge and creep inward, longer when the pulse is high
      java.util.Random rnd = new java.util.Random(7L);
      int vein = (Math.round(a * 230.0F) << 24) | 0x1FB5AC;
      for (int n = 0; n < 32; n++) {
         int side = n % 4;
         double px = side == 0 ? rnd.nextInt(w) : (side == 1 ? w : (side == 2 ? rnd.nextInt(w) : 0));
         double py = side == 0 ? 0 : (side == 1 ? rnd.nextInt(h) : (side == 2 ? h : rnd.nextInt(h)));
         double ang = Math.atan2(h / 2.0 - py, w / 2.0 - px) + (rnd.nextDouble() - 0.5) * 0.8;
         int len = 5 + rnd.nextInt(8) + Math.round(pulse * 3.0F);
         for (int s = 0; s < len; s++) {
            ang += (rnd.nextDouble() - 0.5) * 0.5;
            px += Math.cos(ang) * 3.0;
            py += Math.sin(ang) * 3.0;
            int thick = s < len / 2 ? 4 : 3;
            g.fill((int)px, (int)py, (int)px + thick, (int)py + thick, vein);
            if (s % 5 == 4) {
               // a short branch
               double bx = px;
               double by = py;
               double ba = ang + (rnd.nextBoolean() ? 1.0 : -1.0);
               for (int k = 0; k < 3; k++) {
                  bx += Math.cos(ba) * 3.0;
                  by += Math.sin(ba) * 3.0;
                  g.fill((int)bx, (int)by, (int)bx + 2, (int)by + 2, vein);
               }
            }
         }
      }
   }
}
