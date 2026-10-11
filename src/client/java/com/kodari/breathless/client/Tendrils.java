package com.kodari.breathless.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Dark blue sculk-like veins that creep in from the screen edges. Generated once per screen size. */
final class Tendrils {
   private Tendrils() {
   }

   private static final class Path {
      float[] xs;
      float[] ys;
      float delay;
   }

   private static final List<Path> PATHS = new ArrayList<>();
   private static int builtW = -1;
   private static int builtH = -1;

   private static void build(int w, int h) {
      PATHS.clear();
      builtW = w;
      builtH = h;
      Random r = new Random(7331L);
      float step = Math.max(3.0F, Math.min(w, h) / 120.0F);
      int mainCount = 16;
      for (int t = 0; t < mainCount; t++) {
         float x;
         float y;
         switch (r.nextInt(4)) {
            case 0 -> { x = r.nextFloat() * w; y = 0; }
            case 1 -> { x = w; y = r.nextFloat() * h; }
            case 2 -> { x = r.nextFloat() * w; y = h; }
            default -> { x = 0; y = r.nextFloat() * h; }
         }
         float angle = (float)Math.atan2(h / 2.0F - y, w / 2.0F - x) + (r.nextFloat() - 0.5F) * 1.0F;
         int len = 26 + r.nextInt(30);
         Path main = walk(r, x, y, angle, len, step, 0.0F);
         PATHS.add(main);
         // a couple of side branches
         for (int b = 0; b < 2; b++) {
            int at = 6 + r.nextInt(Math.max(1, len - 12));
            float side = (r.nextBoolean() ? 1.0F : -1.0F) * (0.6F + r.nextFloat() * 0.5F);
            float bx = main.xs[at];
            float by = main.ys[at];
            float bAngle = angle + side;
            PATHS.add(walk(r, bx, by, bAngle, len / 2, step, 0.15F + 0.7F * at / len));
         }
      }
   }

   private static Path walk(Random r, float x, float y, float angle, int len, float step, float delay) {
      Path p = new Path();
      p.xs = new float[len];
      p.ys = new float[len];
      p.delay = delay;
      for (int i = 0; i < len; i++) {
         p.xs[i] = x;
         p.ys[i] = y;
         angle += (float)r.nextGaussian() * 0.28F;
         x += (float)Math.cos(angle) * step;
         y += (float)Math.sin(angle) * step;
      }
      return p;
   }

   /**
    * @param grow 0..1 how far the veins have crept in
    * @param pulse 0..1 heartbeat
    * @param visible 0..1 overall opacity
    */
   static void draw(GuiGraphicsExtractor g, int w, int h, float grow, float pulse, float visible) {
      if (w != builtW || h != builtH) {
         build(w, h);
      }
      int coreAlpha = (int)(235.0F * visible);
      int glowAlpha = (int)(70.0F * visible);
      for (Path p : PATHS) {
         float local = (grow - p.delay) / (1.0F - p.delay);
         if (local <= 0.0F) {
            continue;
         }
         int len = p.xs.length;
         int n = Math.min(len, (int)(len * Math.min(1.0F, local)));
         for (int i = 0; i < n; i++) {
            float prog = i / (float)len;
            int size = Math.round((3.4F * (1.0F - prog) + 1.0F) * (1.0F + 0.45F * pulse));
            int x = (int)p.xs[i];
            int y = (int)p.ys[i];
            if (i % 2 == 0) {
               g.fill(x - 1, y - 1, x + size + 1, y + size + 1, (glowAlpha << 24) | 0x2FB8D0);
            }
            g.fill(x, y, x + size, y + size, (coreAlpha << 24) | 0x0B3A66);
         }
      }
   }
}
