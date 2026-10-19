package com.kodari.breathless;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.AABB;

/**
 * 5-L0 sends a Stagnated now and then: only while a player is deep in heavy sculk or critically exposed.
 * Natural spawning is left alone (MobRules still blocks everything but zombies and skeletons below Y=0).
 */
public final class StagnatedSpawner {
   private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

   private StagnatedSpawner() {
   }

   /** Called once a second per player from Hazard. */
   public static void tick(ServerPlayer player, int sculkTier, float exposure) {
      UUID id = player.getUUID();
      int cooldown = COOLDOWN.getOrDefault(id, 0);
      if (cooldown > 0) {
         COOLDOWN.put(id, cooldown - 1);
         return;
      }
      if (player.isCreative() || player.isSpectator() || !Masks.isUnderground(player)) {
         return;
      }
      if (sculkTier < Tuning.STAGNATED_MIN_TIER && exposure < 100.0F) {
         return;
      }
      if (player.getRandom().nextInt(Tuning.STAGNATED_SPAWN_ODDS) != 0) {
         return;
      }
      ServerLevel level = (ServerLevel)player.level();
      AABB area = player.getBoundingBox().inflate(64.0);
      if (level.getEntitiesOfClass(Stagnated.class, area).size() >= Tuning.STAGNATED_MAX_NEAR) {
         return;
      }
      for (int attempt = 0; attempt < 24; attempt++) {
         double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
         double dist = 18.0 + player.getRandom().nextDouble() * 12.0;
         int x = (int)Math.floor(player.getX() + Math.cos(angle) * dist);
         int z = (int)Math.floor(player.getZ() + Math.sin(angle) * dist);
         for (int dy = 6; dy >= -6; dy--) {
            BlockPos pos = new BlockPos(x, (int)Math.floor(player.getY()) + dy, z);
            if (pos.getY() >= Tuning.HAZARD_Y || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
               continue;
            }
            Stagnated mob = ModEntities.STAGNATED.create(level, EntitySpawnReason.EVENT);
            if (mob == null) {
               return;
            }
            mob.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getRandom().nextFloat() * 360.0F, 0.0F);
            if (!level.noCollision(mob) || player.hasLineOfSight(mob)) {
               mob.discard();
               continue;
            }
            level.addFreshEntity(mob);
            mob.startEmerging();
            COOLDOWN.put(id, Tuning.STAGNATED_COOLDOWN_SECONDS);
            return;
         }
      }
   }
}
