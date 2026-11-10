package com.kodari.breathless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Nidus: one big cave carved into the wall of a deep dark cavern (ancient cities sit in the deep dark too, so they are covered).
 * It looks for the edge of an open cavern, then hollows out a wide dome of rock behind it with a short passage back to the cavern. The inside is lined with sculk,
 * with tendrils hanging from the ceiling and tendons (thin sculk columns) joining floor and ceiling. Three Anathemas stand far apart, each with shriekers, catalysts and sensors around it.
 * Rarity: the world is cut into regions of REGION_CHUNKS chunks; each region has a REGION_CHANCE chance of trying once, in one chosen chunk. If no cavern wall is found, nothing is placed.
 * A feature may only touch its own chunk and the ones around it, so the cave is limited to about 44 blocks across (this one is about 42 wide and 32 deep).
 * The cavern edge is searched at 3 sideways offsets, 4 directions and several heights, so a spot is much easier to find.
 * "forceHere" is the test version (/place feature breathless:nidus_cave_here): it skips the region lottery and tries the chunk it is placed in.
 */
public class NidusCaveFeature extends Feature<NoneFeatureConfiguration> {
   /** Size of a region in chunks, and the chance a region has a Nidus cave at all. */
   static final int REGION_CHUNKS = 12;
   static final double REGION_CHANCE = 0.4;
   /** Half the width along the wall, how far it goes into the rock, and how tall the dome is. */
   static final int HALF_WIDTH = 21;
   static final int DEPTH = 15;
   static final int HEIGHT = 14;
   /** Distance from the cave middle to the cavern edge it opens onto. */
   static final int EDGE = 19;
   static final int ANATHEMAS = 3;
   static final int ANATHEMA_SPACING = 15;
   static final int TENDONS = 10;
   /** Around each Anathema, and scattered anywhere in the cave. */
   static final int SHRIEKERS_EACH = 2;
   static final int CATALYSTS_EACH = 2;
   static final int SENSORS_EACH = 2;
   static final int EXTRA_SENSORS = 3;
   static final int EXTRA_CATALYSTS = 2;
   static final float TENDRIL_CHANCE = 0.07F;

   private final boolean forceHere;

   public NidusCaveFeature(boolean forceHere) {
      super(NoneFeatureConfiguration.CODEC);
      this.forceHere = forceHere;
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      int chunkX = origin.getX() >> 4;
      int chunkZ = origin.getZ() >> 4;
      if (!this.forceHere) {
         int regX = Math.floorDiv(chunkX, REGION_CHUNKS);
         int regZ = Math.floorDiv(chunkZ, REGION_CHUNKS);
         RandomSource regional = RandomSource.create(level.getSeed() * 31L + regX * 341873128712L + regZ * 132897987541L + 5817L);
         double roll = regional.nextDouble();
         int winnerX = regX * REGION_CHUNKS + regional.nextInt(REGION_CHUNKS);
         int winnerZ = regZ * REGION_CHUNKS + regional.nextInt(REGION_CHUNKS);
         if (roll >= REGION_CHANCE || chunkX != winnerX || chunkZ != winnerZ) {
            return false;
         }
      }
      int cx = chunkX * 16 + 8;
      int cz = chunkZ * 16 + 8;
      List<Direction> dirs = new ArrayList<>();
      for (Direction d : Direction.Plane.HORIZONTAL) {
         dirs.add(d);
      }
      Collections.shuffle(dirs, new java.util.Random(random.nextLong()));
      int[] shifts = {0, -7, 7};
      for (Direction d : dirs) {
         for (int shift : shifts) {
            if (this.tryDirection(level, origin, random, cx, cz, d, shift)) {
               return true;
            }
         }
      }
      return false;
   }

   /** Looks for cavern floors at the mouth (up to 3 heights) and tries to build from each. shift moves the mouth sideways. */
   private boolean tryDirection(WorldGenLevel level, BlockPos origin, RandomSource random, int cx, int cz, Direction d, int shift) {
      int sx = d.getStepX();
      int sz = d.getStepZ();
      int mx = cx - sx * EDGE + (sx != 0 ? 0 : shift);
      int mz = cz - sz * EDGE + (sx != 0 ? shift : 0);
      int tried = 0;
      for (int y = -12; y >= -58 && tried < 3; y--) {
         BlockPos p = new BlockPos(mx, y, mz);
         if (isFloor(level, p) && roomAt(level, origin, p)) {
            tried++;
            if (this.tryMouth(level, origin, random, cx, cz, d, shift, p)) {
               return true;
            }
         }
      }
      return false;
   }

   /** d points from the cavern into the rock. The cavern edge ("mouth") is EDGE blocks behind the cave middle. */
   private boolean tryMouth(WorldGenLevel level, BlockPos origin, RandomSource random, int cx, int cz, Direction d, int shift, BlockPos mouth) {
      int sx = d.getStepX();
      int sz = d.getStepZ();
      int floorY = mouth.getY();
      // 2. Work out the shape without changing anything. Give up if it hits water, lava, or the reinforced deepslate of a city.
      double ph1 = random.nextDouble() * 6.28;
      double ph2 = random.nextDouble() * 6.28;
      double ph3 = random.nextDouble() * 6.28;
      Set<BlockPos> open = new HashSet<>();
      List<BlockPos> toCarve = new ArrayList<>();
      int air = 0;
      for (int x = cx - 22; x <= cx + 22; x++) {
         for (int z = cz - 22; z <= cz + 22; z++) {
            if (!inRegion(origin, x, z)) {
               continue;
            }
            double u = (x - cx) * sx + (z - cz) * sz;
            double p = sx != 0 ? (z - cz) : (x - cx);
            double base = (u / DEPTH) * (u / DEPTH) + (p / HALF_WIDTH) * (p / HALF_WIDTH);
            boolean tunnel = u <= -DEPTH + 5 && u >= -EDGE && Math.abs(p - shift) <= 3.0;
            if (base > 1.4 && !tunnel) {
               continue;
            }
            double n = 0.07 * Math.sin(x * 0.37 + ph1) + 0.07 * Math.sin(z * 0.31 + ph2) + 0.05 * Math.sin((x + z) * 0.23 + ph3);
            for (int h = 0; h <= HEIGHT; h++) {
               boolean inside = (tunnel && h <= 4) || (base + (double)(h * h) / (HEIGHT * HEIGHT) < 1.0 + n);
               if (!inside) {
                  continue;
               }
               BlockPos pos = new BlockPos(x, floorY + h, z);
               BlockState st = level.getBlockState(pos);
               if (!st.getFluidState().isEmpty() || st.is(Blocks.REINFORCED_DEEPSLATE)) {
                  return false;
               }
               if (st.isAir()) {
                  air++;
                  open.add(pos);
               } else if (st.is(BlockTags.SCULK_REPLACEABLE)) {
                  toCarve.add(pos);
                  open.add(pos);
               }
            }
         }
      }
      // Mostly solid rock, or it is just an existing cavern and there is nothing to carve.
      if (open.size() < 600 || air > open.size() * 0.4) {
         return false;
      }
      // 3. Floor spots (the floor level is flat), then choose the Anathemas before touching anything.
      List<BlockPos> spots = new ArrayList<>();
      for (BlockPos p : open) {
         if (p.getY() != floorY || horizontal(p, mouth) < 6.0) {
            continue;
         }
         BlockState below = level.getBlockState(p.below());
         if (!below.isAir() && below.getFluidState().isEmpty()) {
            spots.add(p);
         }
      }
      Collections.shuffle(spots, new java.util.Random(random.nextLong()));
      List<BlockPos> anathemas = new ArrayList<>();
      for (BlockPos s : spots) {
         if (anathemas.size() >= ANATHEMAS) {
            break;
         }
         if (!roomFor(open, s) || horizontal(s, mouth) < 10.0) {
            continue;
         }
         boolean far = true;
         for (BlockPos a : anathemas) {
            if (horizontal(a, s) < ANATHEMA_SPACING) {
               far = false;
            }
         }
         if (far) {
            anathemas.add(s);
         }
      }
      if (anathemas.size() < 2) {
         return false;
      }
      // 4. Build: hollow it out, then line it with sculk.
      for (BlockPos p : toCarve) {
         put(level, origin, p, Blocks.AIR.defaultBlockState());
      }
      BlockState sculk = Blocks.SCULK.defaultBlockState();
      List<BlockPos> ceilings = new ArrayList<>();
      for (BlockPos p : open) {
         for (Direction dir : Direction.values()) {
            BlockPos n = p.relative(dir);
            if (open.contains(n) || !level.getBlockState(n).is(BlockTags.SCULK_REPLACEABLE)) {
               continue;
            }
            boolean ceiling = dir == Direction.UP;
            if (dir == Direction.DOWN || random.nextFloat() < (ceiling ? 0.9F : 0.7F)) {
               put(level, origin, n, sculk);
            }
            if (ceiling) {
               ceilings.add(p);
            }
         }
      }
      // Tendrils hanging from the ceiling.
      for (BlockPos p : ceilings) {
         if (random.nextFloat() >= TENDRIL_CHANCE) {
            continue;
         }
         int len = 2 + random.nextInt(5);
         for (int i = 0; i < len; i++) {
            BlockPos q = p.below(i);
            if (!open.contains(q) || q.getY() < floorY + 4) {
               break;
            }
            put(level, origin, q, sculk);
         }
      }
      // x, z, radius of ground already taken: the Anathemas first, then the tendons, then each object.
      List<int[]> used = new ArrayList<>();
      for (BlockPos a : anathemas) {
         used.add(new int[]{a.getX(), a.getZ(), 3});
      }
      // Tendons: thin sculk columns from floor to ceiling, wider at both ends.
      int tendons = 0;
      for (BlockPos s : spots) {
         if (tendons >= TENDONS) {
            break;
         }
         if (horizontal(s, mouth) < 8.0 || isUsed(used, s, 3)) {
            continue;
         }
         boolean spaced = true;
         for (BlockPos a : anathemas) {
            if (horizontal(a, s) < 6.0) {
               spaced = false;
            }
         }
         int top = floorY;
         while (open.contains(new BlockPos(s.getX(), top + 1, s.getZ()))) {
            top++;
         }
         if (!spaced || top - floorY < 6) {
            continue;
         }
         for (int y = floorY; y <= top; y++) {
            put(level, origin, new BlockPos(s.getX(), y, s.getZ()), sculk);
            if (y <= floorY + 1 || y >= top - 1) {
               for (Direction side : Direction.Plane.HORIZONTAL) {
                  BlockPos q = new BlockPos(s.getX(), y, s.getZ()).relative(side);
                  if (open.contains(q)) {
                     put(level, origin, q, sculk);
                  }
               }
            }
         }
         used.add(new int[]{s.getX(), s.getZ(), 3});
         tendons++;
      }
      // The Anathemas (the marker block turns into the entity when it ticks), facing the way out.
      for (BlockPos a : anathemas) {
         BlockState nidus = ModBlocks.SCULK_NIDUS.defaultBlockState().setValue(SculkNidusBlock.FACING, d.getOpposite());
         put(level, origin, a, nidus);
         level.scheduleTick(a, ModBlocks.SCULK_NIDUS, 15);
      }
      // Shriekers, catalysts and sensors: some around each Anathema, some scattered.
      BlockState shrieker = Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true);
      BlockState catalyst = Blocks.SCULK_CATALYST.defaultBlockState();
      BlockState sensor = Blocks.SCULK_SENSOR.defaultBlockState();
      for (BlockPos a : anathemas) {
         scatter(level, origin, random, spots, used, a, 4.0, 9.0, SHRIEKERS_EACH, shrieker);
         scatter(level, origin, random, spots, used, a, 3.0, 9.0, CATALYSTS_EACH, catalyst);
         scatter(level, origin, random, spots, used, a, 3.0, 10.0, SENSORS_EACH, sensor);
      }
      scatter(level, origin, random, spots, used, null, 0.0, 0.0, EXTRA_SENSORS, sensor);
      scatter(level, origin, random, spots, used, null, 0.0, 0.0, EXTRA_CATALYSTS, catalyst);
      return true;
   }

   /** Places up to count of this block on free floor spots, between min and max blocks from near (anywhere when near is null). */
   private static void scatter(WorldGenLevel level, BlockPos origin, RandomSource random, List<BlockPos> spots, List<int[]> used, BlockPos near, double min, double max, int count, BlockState state) {
      if (spots.isEmpty()) {
         return;
      }
      int placed = 0;
      for (int i = 0; i < 60 && placed < count; i++) {
         BlockPos s = spots.get(random.nextInt(spots.size()));
         if (near != null) {
            double dist = horizontal(s, near);
            if (dist < min || dist > max) {
               continue;
            }
         }
         if (isUsed(used, s, 1)) {
            continue;
         }
         put(level, origin, s, state);
         used.add(new int[]{s.getX(), s.getZ(), 1});
         placed++;
      }
   }

   /** True when the spot is within the radius (plus the used area's own radius) of something already placed. */
   private static boolean isUsed(List<int[]> used, BlockPos s, int radius) {
      for (int[] u : used) {
         if (Math.max(Math.abs(u[0] - s.getX()), Math.abs(u[1] - s.getZ())) <= Math.max(u[2], radius)) {
            return true;
         }
      }
      return false;
   }

   /** Room for an Anathema: a 3 by 3 area, 5 blocks high, all inside the cave. */
   private static boolean roomFor(Set<BlockPos> open, BlockPos s) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            for (int dy = 0; dy < 5; dy++) {
               if (!open.contains(s.offset(dx, dy, dz))) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static double horizontal(BlockPos a, BlockPos b) {
      return Math.hypot(a.getX() - b.getX(), a.getZ() - b.getZ());
   }

   private static void put(WorldGenLevel level, BlockPos origin, BlockPos pos, BlockState state) {
      if (inRegion(origin, pos.getX(), pos.getZ())) {
         level.setBlock(pos, state, 2);
      }
   }

   /** Only touch the chunk being generated and its neighbours (a feature may not reach further). */
   private static boolean inRegion(BlockPos origin, int x, int z) {
      return Math.abs((x >> 4) - (origin.getX() >> 4)) <= 1 && Math.abs((z >> 4) - (origin.getZ() >> 4)) <= 1;
   }

   /** Air with two more air above, on solid ground that is not in water. */
   private static boolean isFloor(WorldGenLevel level, BlockPos p) {
      BlockState below = level.getBlockState(p.below());
      return level.getBlockState(p).isAir() && level.getBlockState(p.above()).isAir() && level.getBlockState(p.above(2)).isAir()
         && !below.isAir() && below.getFluidState().isEmpty() && below.is(BlockTags.SCULK_REPLACEABLE);
   }

   /** Open cavern around the mouth: at least two thirds of a 7 by 7 by 4 box is air, and no fluid. */
   private static boolean roomAt(WorldGenLevel level, BlockPos origin, BlockPos floor) {
      int air = 0;
      for (int dx = -3; dx <= 3; dx++) {
         for (int dz = -3; dz <= 3; dz++) {
            if (!inRegion(origin, floor.getX() + dx, floor.getZ() + dz)) {
               return false;
            }
            for (int dy = 0; dy < 4; dy++) {
               BlockState st = level.getBlockState(floor.offset(dx, dy, dz));
               if (st.isAir()) {
                  air++;
               } else if (!st.getFluidState().isEmpty()) {
                  return false;
               }
            }
         }
      }
      return air >= 130;
   }
}
