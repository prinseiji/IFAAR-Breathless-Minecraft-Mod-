package com.kodari.breathless.client;

import com.kodari.breathless.Odium;
import com.kodari.breathless.StrugglePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/** While an Odium holds you: each tap of the jump key is sent to the server (it fills the struggle bar), and the spacebar icon presses in for a moment. */
public final class Struggle {
   private static boolean wasDown;
   private static int flash;

   private Struggle() {
   }

   /** The Odium holding the local player, or null. */
   static Odium holder() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null || mc.level == null) {
         return null;
      }
      for (Odium odium : mc.level.getEntitiesOfClass(Odium.class, mc.player.getBoundingBox().inflate(8.0))) {
         if (odium.getHeldId() == mc.player.getId()) {
            return odium;
         }
      }
      return null;
   }

   static boolean pressed() {
      return flash > 0;
   }

   static void tick(Minecraft mc) {
      if (flash > 0) {
         flash--;
      }
      boolean down = mc.options.keyJump.isDown();
      if (down && !wasDown && holder() != null) {
         ClientPlayNetworking.send(new StrugglePayload());
         flash = 3;
      }
      wasDown = down;
   }
}
