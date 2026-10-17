package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;

import com.kodari.breathless.Tuning;

/**
 * The surface of the gas: one flat, see-through sheet exactly at Y=0, like the top of a brine pool.
 * It is drawn with the game's own flat-quad drawing (the same one the debug tools use), as a grid of tiles around the
 * player that gets fainter with distance so the edge melts away. Both faces are submitted so it shows from above and below.
 */
final class LevelSheet {
   private LevelSheet() {
   }

   private static final int TILE = 4;
   private static final int RADIUS_TILES = 10;
   private static final int COLOR = 0x7BE3FF;
   private static final float MAX_ALPHA = 0.30F;

   static void render(LevelRenderContext context) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return;
      }
      double y = Tuning.HAZARD_Y;
      double dy = player.getY() - y;
      // Same range as the gas particles: from 24 blocks above the line to 40 below it.
      if (dy > 24.0D || dy < -40.0D) {
         return;
      }
      // Fainter the further you are from the line, so it is strongest when you are right at it.
      float vertical = 1.0F - (float)Math.min(1.0D, Math.abs(dy) / 40.0D) * 0.55F;

      double px = player.getX();
      double pz = player.getZ();
      int cx = (int)Math.floor(px / TILE);
      int cz = (int)Math.floor(pz / TILE);
      double reach = (RADIUS_TILES + 0.5D) * TILE;

      for (int ix = -RADIUS_TILES; ix <= RADIUS_TILES; ix++) {
         for (int iz = -RADIUS_TILES; iz <= RADIUS_TILES; iz++) {
            double x0 = (cx + ix) * (double)TILE;
            double z0 = (cz + iz) * (double)TILE;
            double mx = x0 + TILE * 0.5D - px;
            double mz = z0 + TILE * 0.5D - pz;
            double d = Math.sqrt(mx * mx + mz * mz) / reach;
            if (d >= 1.0D) {
               continue;
            }
            float falloff = (float)((1.0D - d) * (1.0D - d));
            int alpha = (int)(255.0F * MAX_ALPHA * vertical * falloff);
            if (alpha < 3) {
               continue;
            }
            int color = (alpha << 24) | COLOR;
            GizmoStyle style = GizmoStyle.fill(color);
            Vec3 a = new Vec3(x0, y, z0);
            Vec3 b = new Vec3(x0 + TILE, y, z0);
            Vec3 c = new Vec3(x0 + TILE, y, z0 + TILE);
            Vec3 e = new Vec3(x0, y, z0 + TILE);
            // Both winding orders, so one face is always the front whichever side you look from.
            Gizmos.rect(a, b, c, e, style);
            Gizmos.rect(e, c, b, a, style);
         }
      }
   }
}
