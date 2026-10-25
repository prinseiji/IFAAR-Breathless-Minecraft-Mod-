package com.kodari.breathless;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;

/**
 * The "IFAAR: Breathless" advancement tab (data/breathless/advancement/*.json).
 * The advancements themselves are plain "impossible" ones; this class hands them out when the thing happens.
 */
public final class Achievements {
   private Achievements() {
   }

   public static void init() {
      ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
         Entity killer = source.getEntity();
         if (entity instanceof Stagnated stagnated) {
            if (stagnated.isSweaty() && stagnated.isEnraged() && killer instanceof ServerPlayer) {
               for (Player other : stagnated.level().players()) {
                  if (other instanceof ServerPlayer near && near.distanceToSqr(stagnated) <= 32.0 * 32.0) {
                     award(near, "the_imperfect_vessel");
                     awardNext(near, "skill_issue");
                  }
               }
            }
            if (killer instanceof ServerPlayer player) {
               award(player, "at_peace_at_last");
               awardNext(player, "finally_still");
               if (stagnated.isArmored()) {
                  award(player, "bravery");
               }
            } else if (killer instanceof Warden) {
               awardNear(stagnated, 32.0, "hate_filled_amalgamate");
            }
         } else if ((entity instanceof Zombie || entity instanceof AbstractSkeleton) && killer instanceof Stagnated) {
            awardNear(entity, 32.0, "malignant_flesh");
         }
      });
      // No Flesh, No Lungs: five seconds next to a skeleton below Y=0.
      ServerTickEvents.END_SERVER_TICK.register(server -> {
         if (server.getTickCount() % 20 != 0) {
            return;
         }
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean near = false;
            if (player.getY() < 0.0 && Masks.isHazardLevel(player.level()) && !player.isSpectator()) {
               near = !player.level().getEntitiesOfClass(AbstractSkeleton.class, player.getBoundingBox().inflate(5.0)).isEmpty();
            }
            if (near) {
               if (SKELETON_TIME.merge(player.getUUID(), 1, Integer::sum) >= 5) {
                  award(player, "no_flesh_no_lungs");
               }
            } else {
               SKELETON_TIME.remove(player.getUUID());
            }
         }
      });
   }

   private static final java.util.Map<java.util.UUID, Integer> SKELETON_TIME = new java.util.HashMap<>();

   private static AdvancementHolder holder(ServerPlayer player, String id) {
      return player.level().getServer().getAdvancements().get(Breathless.id(id));
   }

   /** Gives the advancement (its single "done" criterion). Safe to call over and over. */
   public static void award(ServerPlayer player, String id) {
      if (player.isSpectator()) {
         return;
      }
      AdvancementHolder holder = holder(player, id);
      if (holder != null) {
         player.getAdvancements().award(holder, "done");
      }
   }

   /** Gives the next unfinished criterion of a counting advancement (the ones with k1..kN). */
   public static void awardNext(ServerPlayer player, String id) {
      AdvancementHolder holder = holder(player, id);
      if (holder == null) {
         return;
      }
      AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
      String next = null;
      for (String criterion : progress.getRemainingCriteria()) {
         next = criterion;
         break;
      }
      if (next != null) {
         player.getAdvancements().award(holder, next);
      }
   }

   /** Gives the advancement to every player within the given distance of the entity. */
   public static void awardNear(Entity at, double radius, String id) {
      for (Player other : at.level().players()) {
         if (other instanceof ServerPlayer player && player.distanceToSqr(at) <= radius * radius) {
            award(player, id);
         }
      }
   }
}
