package com.kodari.breathless;

import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The Silence effect. While it is on you make no noise:
 * - no game events are sent from you (see SilenceMixin), so sculk sensors, shriekers and Wardens never hear you and the Stagnated's echolocation never wakes;
 * - a sprinting player is not heard from further away (Stagnated.canSense);
 * - the vanilla "silent" flag is set on you so your own sounds stop (Tuning.SILENCE_MUTES_SOUNDS);
 * - together with Invisibility you cannot be sensed at all unless you touch a Stagnated (see ghost()).
 */
public final class Silence {
   private Silence() {
   }

   /** Players whose silent flag was set by this effect, so it is only cleared when we set it. Also saved as an entity tag in case of a restart. */
   private static final String TAG = "breathless_muted";

   public static boolean silenced(LivingEntity entity) {
      return ModEffects.SILENCE != null && entity.hasEffect(ModEffects.SILENCE);
   }

   /** Silent and invisible: Stagnated lose you unless you bump into one. */
   public static boolean ghost(LivingEntity entity) {
      return silenced(entity) && entity.isInvisible();
   }

   public static void init() {
      ServerTickEvents.END_SERVER_TICK.register(server -> {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean on = Tuning.SILENCE_MUTES_SOUNDS && silenced(player);
            boolean muted = player.getTags().contains(TAG);
            if (on && !muted) {
               player.addTag(TAG);
               player.setSilent(true);
            } else if (!on && muted) {
               player.removeTag(TAG);
               player.setSilent(false);
            }
         }
      });
   }
}
