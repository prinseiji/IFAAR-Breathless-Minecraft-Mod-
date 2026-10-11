package com.kodari.breathless.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Masks;
import com.kodari.breathless.ModItems;
import com.kodari.breathless.Tuning;

/** All the screen drawing: spore haze, mask visor, exposure meter, and the critical-state effects. */
public final class SporeHud {
   private SporeHud() {
   }

   private static final Identifier MASK_OVERLAY = Breathless.id("textures/misc/gas_mask_overlay.png");
   private static final Identifier MASK_CRACKS_1 = Breathless.id("textures/misc/gas_mask_cracks_1.png");
   private static final Identifier MASK_CRACKS_2 = Breathless.id("textures/misc/gas_mask_cracks_2.png");

   private static int argb(int alpha, int rgb) {
      return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0xFFFFFF);
   }

   // ---- spore haze + mask visor ----

   static void renderFogAndMask(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      int w = g.guiWidth();
      int h = g.guiHeight();

      // Light haze on top of the real fog (FogRendererMixin). It is also the fallback if that mixin cannot apply.
      double y = player.getY();
      if (y < Tuning.HAZARD_Y && mc.level.dimension() == Level.OVERWORLD) {
         float depth = (float)Math.min(1.0D, -y / 8.0D);
         float deep = (float)Math.min(1.0D, -y / 64.0D);
         int alpha = (int)(depth * (16.0F + 14.0F * deep));
         g.fill(0, 0, w, h, argb(alpha, 0x9CCFD6));
      }

      // Mask visor, first person only (like the pumpkin).
      ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
      boolean filtered = head.is(ModItems.GAS_MASK);
      if ((filtered || head.is(ModItems.GAS_MASK_EMPTY)) && mc.options.getCameraType().isFirstPerson()) {
         g.blit(RenderPipelines.GUI_TEXTURED, MASK_OVERLAY, 0, 0, 0.0F, 0.0F, w, h, w, h);
         if (filtered) {
            float remaining = 1.0F - head.getDamageValue() / (float)Math.max(1, head.getMaxDamage());
            if (remaining < Tuning.CRACK_STAGE_2) {
               g.blit(RenderPipelines.GUI_TEXTURED, MASK_CRACKS_1, 0, 0, 0.0F, 0.0F, w, h, w, h);
               g.blit(RenderPipelines.GUI_TEXTURED, MASK_CRACKS_2, 0, 0, 0.0F, 0.0F, w, h, w, h);
            } else if (remaining < Tuning.CRACK_STAGE_1) {
               g.blit(RenderPipelines.GUI_TEXTURED, MASK_CRACKS_1, 0, 0, 0.0F, 0.0F, w, h, w, h);
            }
         }
      }
   }

   // ---- exposure meter ----

   static void renderMeter(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      float exposure = ClientState.exposure;
      if (exposure < 0.5F && !Masks.isUnderground(player)) {
         return;
      }
      int w = 64;
      int bar = 6;
      int x = 8;
      int y = g.guiHeight() - 24;

      g.fill(x - 1, y - 1, x + w + 1, y + bar + 1, 0xCC0B1A22);
      g.fill(x, y, x + w, y + bar, 0xFF05090C);
      int filled = Math.round(w * Math.min(100.0F, exposure) / 100.0F);
      if (filled > 0) {
         // cyan when light, deep blue when heavy; flashes at 100
         float f = Math.min(1.0F, exposure / 100.0F);
         int r = (int)(0x3F + (0x14 - 0x3F) * f);
         int gr = (int)(0xD0 + (0x48 - 0xD0) * f);
         int b = (int)(0xD8 + (0x7A - 0xD8) * f);
         int color = 0xFF000000 | (r << 16) | (gr << 8) | b;
         if (exposure >= 99.99F && (mc.player.tickCount / 5) % 2 == 0) {
            color = 0xFFE8FFFF;
         }
         g.fill(x, y, x + filled, y + bar, color);
      }
      g.text(mc.font, "SPORES " + Math.round(exposure) + "%", x, y - 10, 0xFF9FD9DD);
   }

   // ---- critical state ----

   private static float bump(float x, float center, float width) {
      return Math.max(0.0F, 1.0F - Math.abs(x - center) / width);
   }

   static void renderCritical(GuiGraphicsExtractor g, DeltaTracker tracker) {
      int ticks = ClientState.critTicks;
      if (ticks <= 0) {
         return;
      }
      int w = g.guiWidth();
      int h = g.guiHeight();

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
      drawVignette(g, w, h, 0x0A3A52, intensity);
      Tendrils.draw(g, w, h, Math.min(1.0F, ticks / 160.0F), pulse, visible);
   }

   private static void drawVignette(GuiGraphicsExtractor g, int w, int h, int rgb, float intensity) {
      int steps = 22;
      int depth = (int)(Math.min(w, h) * 0.42F);
      int th = Math.max(1, depth / steps);
      for (int k = 0; k < steps; k++) {
         float f = 1.0F - k / (float)steps;
         int alpha = (int)(intensity * f * f * 255.0F);
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
