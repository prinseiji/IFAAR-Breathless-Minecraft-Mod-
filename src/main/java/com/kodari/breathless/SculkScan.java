package com.kodari.breathless;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * How much sculk is around a player, counting only sculk the spores can reach.
 * The scan starts at the player's head and spreads through open air (anything you could walk through),
 * up to Tuning.SCULK_RADIUS blocks away. A sculk block counts when it touches that open space, so sculk behind a
 * solid wall or floor is ignored, and walling a room off protects you.
 */
public final class SculkScan {
   private SculkScan() {
   }

   private static final Direction[] DIRECTIONS = Direction.values();

   /** The sculk score near this player (see Tuning.SCULK_TIER_*). */
   public static int score(ServerPlayer player) {
      ServerLevel level = (ServerLevel)player.level();
      BlockPos start = BlockPos.containing(player.getEyePosition());
      int radius = Tuning.SCULK_RADIUS;
      long radiusSq = (long)radius * radius;

      LongOpenHashSet seen = new LongOpenHashSet();
      LongOpenHashSet counted = new LongOpenHashSet();
      long[] queue = new long[Tuning.SCULK_MAX_CELLS + 8];
      int head = 0;
      int tail = 0;
      queue[tail++] = start.asLong();
      seen.add(start.asLong());
      int score = 0;
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos next = new BlockPos.MutableBlockPos();

      while (head < tail) {
         pos.set(queue[head++]);
         BlockState here = level.getBlockState(pos);
         // Sculk veins have no collision, so they are open cells that are also sculk.
         if (here.is(Blocks.SCULK_VEIN) && counted.add(pos.asLong())) {
            score += 1;
         }
         for (Direction direction : DIRECTIONS) {
            next.setWithOffset(pos, direction);
            long key = next.asLong();
            if (seen.contains(key)) {
               continue;
            }
            if (next.distSqr(start) > radiusSq) {
               continue;
            }
            seen.add(key);
            BlockState state = level.getBlockState(next);
            int weight = weightOf(state);
            if (weight > 0) {
               if (counted.add(key)) {
                  score += weight;
               }
            }
            boolean open = state.getCollisionShape(level, next).isEmpty();
            if (open && tail < queue.length) {
               queue[tail++] = key;
            }
         }
      }
      return score;
   }

   private static int weightOf(BlockState state) {
      if (state.is(Blocks.SCULK) || state.is(Blocks.SCULK_VEIN)) {
         return 1;
      }
      if (state.is(Blocks.SCULK_CATALYST) || state.is(Blocks.SCULK_SHRIEKER) || state.is(Blocks.SCULK_SENSOR) || state.is(Blocks.CALIBRATED_SCULK_SENSOR)) {
         return 4;
      }
      return 0;
   }

   /** 0 (none) to 3 (Moribund) for a score. */
   public static int tier(int score) {
      if (score >= Tuning.SCULK_TIER_3) {
         return 3;
      }
      if (score >= Tuning.SCULK_TIER_2) {
         return 2;
      }
      return score >= Tuning.SCULK_TIER_1 ? 1 : 0;
   }
}
