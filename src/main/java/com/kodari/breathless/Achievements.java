package com.kodari.breathless;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
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
   }

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
