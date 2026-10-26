package com.kodari.breathless.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.kodari.breathless.MaskFilters;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Masks;
import com.kodari.breathless.Tuning;

/** All the screen drawing: spore haze, vision pulses, mask visor, exposure gauge, F13ND pop-ups, and the critical-state effects. */
public final class SporeHud {
   private SporeHud() {
   }

   private static final Identifier MASK_OVERLAY = Breathless.id("textures/misc/gas_mask_overlay.png");
   private static final Identifier MASK_CRACKS_1 = Breathless.id("textures/misc/gas_mask_cracks_1.png");
   private static final Identifier MASK_CRACKS_2 = Breathless.id("textures/misc/gas_mask_cracks_2.png");
   private static final Identifier GAUGE = Breathless.id("textures/gui/gauge.png");
   private static final Identifier[] POPUPS = new Identifier[6];
   private static final Identifier POPUP_FINAL = Breathless.id("textures/gui/popup_final.png");

   static {
      for (int i = 0; i < POPUPS.length; i++) {
         POPUPS[i] = Breathless.id("textures/gui/popup_" + i + ".png");
      }
   }

   /** Pixel widths of the pop-up pictures (all are 83 tall). Same order as POPUPS. */
   private static final int[] POPUP_W = {331, 195, 185, 394, 150, 202};
   private static final int POPUP_FINAL_W = 184;
   private static final int POPUP_H = 83;
   private static final float POPUP_SCALE = 0.5F;

   // The loading pop-up: 4 frames in a row (loading. / .. / ... / ....), each 206 x 84
   // in the real picture, drawn at half size (103 x 42) like the cover pop-ups.
   private static final Identifier POPUP_LOADING = Breathless.id("textures/gui/popup_loading.png");
   private static final int LOAD_W = 103;
   private static final int LOAD_H = 42;
   private static final int LOAD_FRAMES = 4;
   private static final int LOAD_SHEET_W = 412;
   private static final int LOAD_SHEET_H = 42;
   private static final int LOAD_TICKS_PER_FRAME = 7;

   // Gauge picture is 94x404; the dark tube inside it starts here.
   private static final int GAUGE_W = 94;
   private static final int GAUGE_H = 404;
   private static final int TUBE_X = 30;
   private static final int TUBE_Y = 78;
   private static final int TUBE_W = 33;
   private static final int TUBE_H = 294;
   private static final float GAUGE_SCALE = 0.30F;

   private static int argb(int alpha, int rgb) {
      return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0xFFFFFF);
   }

   /** Fade for the gauge and the filter meters: they ease in and out instead of popping when you cross Y=0 or the mask comes on or off. */
   private static float meterFade;
   private static float filterFade;
   private static long lastMeterNanos;
   private static long lastFadeNanos;
   private static final float FADE_SECONDS = 0.7F;

   private static float stepFade(float fade, boolean show, float dt) {
      return Math.max(0.0F, Math.min(1.0F, fade + (show ? dt : -dt) / FADE_SECONDS));
   }

   /** Multiplies the alpha of an ARGB colour. */
   private static int fa(int color, float f) {
      int a = Math.round((color >>> 24) * f);
      return (a << 24) | (color & 0xFFFFFF);
   }

   private static float smooth(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   private static float bump(float x, float center, float width) {
      return Math.max(0.0F, 1.0F - Math.abs(x - center) / width);
   }

   // ---- spore haze, vision pulses, floating spores, mask visor ----

   static void renderFogAndMask(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      int w = g.guiWidth();
      int h = g.guiHeight();
      float exposure = ClientState.shownExposure();

      // Light blue haze on top of the real fog (FogRendererMixin). It is also the fallback if that mixin cannot apply.
      double y = player.getY();
      if (y < Tuning.HAZARD_Y && mc.level.dimension() == Level.OVERWORLD) {
         float depth = (float)Math.min(1.0D, -y / 8.0D);
         float deep = (float)Math.min(1.0D, -y / 64.0D);
         int alpha = (int)(depth * (18.0F + 16.0F * deep));
         g.fill(0, 0, w, h, argb(alpha, 0x2A8CC0));
      }

      // Punishment: now and then the vision goes milky, closing in from the edges. Faster and stronger as exposure climbs.
      if (exposure >= Tuning.BLUR_START) {
         float strength = Math.min(1.0F, (exposure - Tuning.BLUR_START) / (100.0F - Tuning.BLUR_START));
         int period = (int)(Tuning.BLUR_PERIOD_TICKS * (1.8F - 0.8F * strength));
         float phase = (ClientState.clock % period) / (float)period;
         float pulse = phase < 0.4F ? (float)Math.sin(Math.PI * phase / 0.4F) : 0.0F;
         if (pulse > 0.0F) {
            float amount = pulse * (0.35F + 0.65F * strength);
            g.fill(0, 0, w, h, argb((int)(amount * 70.0F), 0xB9E6F2));
            drawVignette(g, w, h, 0xCFEFF6, amount * 0.85F, 0.5F);
         }
      }

      // Mask visor, first person only (like the pumpkin). Cracks sit at the edges.
      ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
      if (Masks.isMask(head) && mc.options.getCameraType().isFirstPerson()) {
         g.blit(RenderPipelines.GUI_TEXTURED, MASK_OVERLAY, 0, 0, 0.0F, 0.0F, w, h, w, h);
         int stage = ClientState.stageOf(head);
         if (stage >= 1) {
            drawCrack(g, MASK_CRACKS_1, w, h, ClientState.crackAnim > 0 && ClientState.crackAnimStage == 1);
         }
         if (stage >= 2) {
            drawCrack(g, MASK_CRACKS_2, w, h, ClientState.crackAnim > 0 && ClientState.crackAnimStage == 2);
         }
         if (ClientState.crackAnim > 0) {
            // A quick white flash as the glass gives.
            g.fill(0, 0, w, h, argb(ClientState.crackAnim * 9, 0xE8F8FF));
         }
      }
   }

   /** A crack layer. While it is new it slams in: starts oversized and a little shaky, then settles. */
   private static void drawCrack(GuiGraphicsExtractor g, Identifier texture, int w, int h, boolean fresh) {
      if (!fresh) {
         g.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0.0F, 0.0F, w, h, w, h);
         return;
      }
      float k = ClientState.crackAnim / (float)ClientState.CRACK_ANIM_TICKS;
      float scale = 1.0F + 0.14F * k;
      int dw = (int)(w * scale);
      int dh = (int)(h * scale);
      int shake = (int)(4.0F * k) * ((ClientState.clock % 2 == 0) ? 1 : -1);
      g.blit(RenderPipelines.GUI_TEXTURED, texture, (w - dw) / 2 + shake, (h - dh) / 2 - shake, 0.0F, 0.0F, dw, dh, dw, dh);
   }

   // ---- filter durability meter (under the gauge, while a mask is worn) ----

   private static final Identifier FILTER_ICONS = Breathless.id("textures/gui/filters.png");
   /** Same layout, purple band: used for reinforced filters. */
   private static final Identifier FILTER_ICONS_REINFORCED = Breathless.id("textures/gui/filters_reinforced.png");
   /** Your 16x16 filter art, drawn at its own size. */
   private static final int FILTER_ICON = 16;
   private static final int BAR_W = 30;
   private static final int BAR_H = 6;
   private static final int FILTER_ROW = 18;

   static void renderFilters(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
      long now = System.nanoTime();
      float dt = lastFadeNanos == 0L ? 0.0F : Math.min(0.25F, (now - lastFadeNanos) / 1.0E9F);
      lastFadeNanos = now;
      boolean showFilters = Masks.isMask(head) && (Masks.isUnderground(player) || ClientState.filterShow > 0) && ClientState.fiendTick < 0;
      filterFade = stepFade(filterFade, showFilters, dt);
      if (filterFade <= 0.0F || !Masks.isMask(head)) {
         return;
      }
      float ff = filterFade;
      MaskFilters filters = Masks.getFilters(head);
      // In the bottom-left corner of the screen.
      int x0 = 6;
      int y0 = g.guiHeight() - 4 - FILTER_ROW * 2;
      for (int slot = 0; slot < 2; slot++) {
         int life = slot == 0 ? filters.first() : filters.second();
         int iy = y0 + slot * FILTER_ROW;
         if (life > 0) {
            g.blit(RenderPipelines.GUI_TEXTURED, filters.reinforced(slot) ? FILTER_ICONS_REINFORCED : FILTER_ICONS, x0, iy, slot * 16.0F, 5.0F, 15, 15, 15, 15, 32, 32, fa(0xFFFFFFFF, ff));
         } else {
            // an empty slot: just a faint outline
            int o = fa(0x50A8C8D0, ff);
            g.fill(x0 + 2, iy + 2, x0 + FILTER_ICON - 2, iy + 3, o);
            g.fill(x0 + 2, iy + FILTER_ICON - 3, x0 + FILTER_ICON - 2, iy + FILTER_ICON - 2, o);
            g.fill(x0 + 2, iy + 2, x0 + 3, iy + FILTER_ICON - 2, o);
            g.fill(x0 + FILTER_ICON - 3, iy + 2, x0 + FILTER_ICON - 2, iy + FILTER_ICON - 2, o);
         }
         int bx = x0 + FILTER_ICON + 3;
         int by = iy + 5;
         g.fill(bx - 1, by - 1, bx + BAR_W + 1, by + BAR_H + 1, fa(0xFF000000, ff));
         g.fill(bx, by, bx + BAR_W, by + BAR_H, fa(0xFF272740, ff));
         float fr = life > 0 ? filters.fraction(slot) : 0.0F;
         int fw = Math.round(BAR_W * fr);
         if (life > 0 && fw < 1) {
            fw = 1;
         }
         int color = fr > Tuning.CRACK_STAGE_1 ? 0xFF4CD964 : (fr > Tuning.CRACK_STAGE_2 ? 0xFFE6C229 : 0xFFD93A3A);
         if (life > 0 && fr <= Tuning.CRACK_STAGE_2 && (ClientState.clock / 6) % 2 == 0) {
            color = 0xFFFF7070;
         }
         if (fw > 0) {
            g.fill(bx, by, bx + fw, by + BAR_H, fa(color, ff));
            g.fill(bx, by, bx + fw, by + 1, fa(0x55FFFFFF, ff));
         }
         g.text(mc.font, Masks.percent(life) + "%", bx + BAR_W + 4, iy + 4, fa(life > 0 ? color : 0xFF6A7A80, ff));
      }
   }

   // ---- exposure gauge + F13ND pop-ups ----

   static void renderMeter(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      float exposure = ClientState.shownExposure();
      boolean fiend = ClientState.fiendTick >= 0;
      long now = System.nanoTime();
      float dt = lastMeterNanos == 0L ? 0.0F : Math.min(0.25F, (now - lastMeterNanos) / 1.0E9F);
      lastMeterNanos = now;
      boolean showMeter = fiend || exposure >= 0.5F || Masks.isUnderground(player);
      meterFade = fiend ? 1.0F : stepFade(meterFade, showMeter, dt);
      if (meterFade <= 0.0F) {
         return;
      }
      float mf = meterFade;
      int gw = Math.round(GAUGE_W * GAUGE_SCALE);
      int gh = Math.round(GAUGE_H * GAUGE_SCALE);
      int x = 6;
      int y = g.guiHeight() / 2 - gh / 2;

      g.blit(RenderPipelines.GUI_TEXTURED, GAUGE, x, y, 0.0F, 0.0F, gw, gh, gw, gh, gw, gh, fa(0xFFFFFFFF, mf));

      int tx = x + Math.round(TUBE_X * GAUGE_SCALE);
      int ty = y + Math.round(TUBE_Y * GAUGE_SCALE);
      int tw = Math.round(TUBE_W * GAUGE_SCALE);
      int th = Math.round(TUBE_H * GAUGE_SCALE);
      int filled = Math.round(th * Math.min(100.0F, exposure) / 100.0F);
      if (filled > 0) {
         // cyan when light, deep blue when heavy; flashes at 100
         float f = Math.min(1.0F, exposure / 100.0F);
         int r = (int)(0x3F + (0x14 - 0x3F) * f);
         int gr = (int)(0xD0 + (0x48 - 0xD0) * f);
         int b = (int)(0xD8 + (0x7A - 0xD8) * f);
         int color = fa(0xE6000000 | (r << 16) | (gr << 8) | b, mf);
         if (exposure >= 99.99F && (mc.player.tickCount / 5) % 2 == 0) {
            color = fa(0xE6E8FFFF, mf);
         }
         g.fill(tx, ty + th - filled, tx + tw, ty + th, color);
      }
      int pct = Math.round(exposure);
      int ft = ClientState.fiendTick;
      if (fiend && ft >= Tuning.BUILDUP_START_TICK && ft < Tuning.POPUP_CLEAR_TICK) {
         // The number warbles while F13ND works on it.
         Random warble = new Random(ClientState.fiendSeed * 31L + ft / 2);
         pct = Math.max(0, Math.min(100, pct + warble.nextInt(2 * Tuning.WARBLE_PERCENT + 1) - Tuning.WARBLE_PERCENT));
      }
      String label = pct + "%";
      g.text(mc.font, label, x + gw / 2 - mc.font.width(label) / 2, y + gh + 3, fa(0xFF9FD9DD, mf));

      if (fiend) {
         // The cover area includes the percentage label under the gauge, so nothing peeks out.
         int cover = gh + 16;
         drawPopups(g, x + gw / 2, y + cover / 2, cover);
      }
   }

   /** The F13ND pop-ups: they pile onto the gauge, vanish together, and one last error pop-up appears. */
   private static void drawPopups(GuiGraphicsExtractor g, int cx, int cy, int gaugeHeight) {
      int t = ClientState.fiendTick;
      int screenW = g.guiWidth();
      int screenH = g.guiHeight();
      int dh = Math.round(POPUP_H * POPUP_SCALE);

      if (t < Tuning.POPUP_CLEAR_TICK) {
         // The loading pop-up sits in the middle of the screen for the whole build-up.
         if (t >= Tuning.BUILDUP_START_TICK) {
            int frame = ((t - Tuning.BUILDUP_START_TICK) / LOAD_TICKS_PER_FRAME) % LOAD_FRAMES;
            float u = frame * LOAD_W;
            float v = 0;
            g.blit(RenderPipelines.GUI_TEXTURED, POPUP_LOADING, Math.round(screenW * 0.60F - LOAD_W / 2.0F), Math.round(screenH * 0.40F - LOAD_H / 2.0F), u, v, LOAD_W, LOAD_H, LOAD_SHEET_W, LOAD_SHEET_H);
         }
         Random rnd = new Random(ClientState.fiendSeed);
         List<Integer> kinds = new ArrayList<>();
         for (int i = 0; i < POPUPS.length; i++) {
            kinds.add(i);
         }
         Collections.shuffle(kinds, rnd);
         for (int i = 0; i < Tuning.POPUP_COUNT; i++) {
            int born = Tuning.popupBornTick(i);
            int kind = i < kinds.size() ? kinds.get(i) : rnd.nextInt(POPUPS.length);
            int dw = Math.round(POPUP_W[kind] * POPUP_SCALE);
            // Narrow pop-ups stay centered on the gauge so they always cover it; wide ones may wander sideways.
            float wander = Math.max(0.0F, Math.min(30.0F, dw / 2.0F - 16.0F));
            float ox = (rnd.nextFloat() - 0.5F) * 2.0F * wander;
            // Stratified over the cover height (gauge + label) with a little overshoot at both ends, plus some mess.
            float span = gaugeHeight - dh + 12.0F;
            float oy = (((i * 5) % 6) / 5.0F - 0.5F) * span + (rnd.nextFloat() - 0.5F) * 8.0F;
            float phase = rnd.nextFloat() * 6.28F;
            if (t < born) {
               continue;
            }
            // They shift around a little after landing.
            float sx = (float)Math.sin((t + i * 5) * 0.35F + phase) * 3.0F;
            float sy = (float)Math.cos((t + i * 3) * 0.30F + phase) * 2.0F;
            int px = Math.max(2, Math.min(screenW - dw - 2, Math.round(cx + ox + sx - dw / 2.0F)));
            int py = Math.max(2, Math.min(screenH - dh - 2, Math.round(cy + oy + sy - dh / 2.0F)));
            g.blit(RenderPipelines.GUI_TEXTURED, POPUPS[kind], px, py, 0.0F, 0.0F, dw, dh, dw, dh);
         }
      } else if (t >= Tuning.POPUP_FINAL_TICK && t < Tuning.POPUP_END_TICK) {
         int dw = Math.round(POPUP_FINAL_W * POPUP_SCALE);
         int shake = t - Tuning.POPUP_FINAL_TICK < 5 ? ((t % 2 == 0) ? 3 : -3) : 0;
         // The final pop-up sits in the middle of the screen, not on the gauge.
         int px = Math.round(screenW / 2.0F - dw / 2.0F) + shake;
         int py = Math.round(screenH / 2.0F - dh / 2.0F);
         g.blit(RenderPipelines.GUI_TEXTURED, POPUP_FINAL, px, py, 0.0F, 0.0F, dw, dh, dw, dh);
      }
   }

   // ---- critical state + F13ND vignette ----

   static void renderCritical(GuiGraphicsExtractor g, DeltaTracker tracker) {
      int w = g.guiWidth();
      int h = g.guiHeight();

      // F13ND: a red flash at the injection, a red vignette that fades in and stays while the pop-ups cover the bar,
      // fades out when they vanish, then a sudden flash with the final pop-up.
      int ft = ClientState.fiendTick;
      if (ft >= 0) {
         if (ft < Tuning.FLASH_TICKS) {
            float k = 1.0F - ft / (float)Tuning.FLASH_TICKS;
            g.fill(0, 0, w, h, argb((int)(190.0F * k), 0xC01020));
         }
         float red = 0.0F;
         if (ft < Tuning.POPUP_CLEAR_TICK + Tuning.VIGNETTE_FADEOUT_TICKS) {
            red = 0.8F * smooth(Math.min(1.0F, (ft + 1) / 14.0F));
            if (ft >= Tuning.POPUP_CLEAR_TICK) {
               float out = (ft - Tuning.POPUP_CLEAR_TICK) / (float)Tuning.VIGNETTE_FADEOUT_TICKS;
               red *= 1.0F - smooth(Math.min(1.0F, out));
            }
         } else if (ft >= Tuning.POPUP_FINAL_TICK && ft < Tuning.POPUP_END_TICK) {
            float rise = smooth(Math.min(1.0F, (ft - Tuning.POPUP_FINAL_TICK + 1) / 7.0F));
            red = 0.95F * rise * (1.0F - (ft - Tuning.POPUP_FINAL_TICK) / (float)(Tuning.POPUP_END_TICK - Tuning.POPUP_FINAL_TICK));
         }
         if (red > 0.0F) {
            drawVignette(g, w, h, 0xB0101A, red, 0.5F);
         }
      }

      int ticks = ClientState.critTicks;
      if (ticks <= 0) {
         return;
      }

      // The fade to black.
      float fade = Math.min(1.0F, ticks / (float)(Tuning.FADE_SECONDS * 20));
      int blackAlpha = (int)(fade * 255.0F);
      if (blackAlpha > 0) {
         g.fill(0, 0, w, h, blackAlpha << 24);
      }

      // Heartbeat (lub-dub).
      float phase = (ticks % 26) / 26.0F;
      float pulse = Math.max(bump(phase, 0.08F, 0.10F), 0.7F * bump(phase, 0.30F, 0.10F));
      // The effects stay faintly visible even when the screen is black.
      float visible = 1.0F - 0.65F * fade;

      float intensity = Math.min(1.0F, ticks / 40.0F) * (0.40F + 0.45F * pulse) * (0.6F + 0.4F * visible);
      drawVignette(g, w, h, 0x0A3A52, intensity, 0.42F);
      Tendrils.draw(g, w, h, Math.min(1.0F, ticks / 160.0F), pulse, visible);
   }

   /** Soft frame around the screen. reach is how far in it goes, as a fraction of the shorter side. */
   private static void drawVignette(GuiGraphicsExtractor g, int w, int h, int rgb, float intensity, float reach) {
      int steps = 22;
      int depth = (int)(Math.min(w, h) * reach);
      int th = Math.max(1, depth / steps);
      for (int k = 0; k < steps; k++) {
         float f = 1.0F - k / (float)steps;
         int alpha = (int)(Math.min(1.0F, intensity) * f * f * 255.0F);
         if (alpha <= 0) {
            continue;
         }
         int c = argb(alpha, rgb);
         int o = k * th;
         g.fill(o, o, w - o, o + th, c);
         g.fill(o, h - o - th, w - o, h - o, c);
         g.fill(o, o + th, o + th, h - o - th, c);
         g.fill(w - o - th, o + th, w - o, h - o - th, c);
      }
   }
}
