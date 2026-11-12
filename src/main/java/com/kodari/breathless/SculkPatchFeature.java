package com.kodari.breathless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Sculk patches on cave floors, in three kinds (each is its own feature):
 * SPLOTCH: a small patch of sculk in an ordinary deepslate cave, nothing on it (uncommon).
 * CARRION_PATCH: a patch with a Carrion in the middle, in ordinary caves below Y=0 (rare).
 * DEEP_CARRION: a Carrion on an existing sculk floor in the deep dark (abundant, at most MAX_DEEP per chunk).
 * ODIUM_DEEP: a buried Odium on a sculk floor in the deep dark (rare, at most one per chunk).
 * Each try picks a random spot in the chunk and a random height, and looks down for a cave floor, so most tries find nothing.
 */
public class SculkPatchFeature extends Feature<NoneFeatureConfiguration> {
   public enum Kind {
      SPLOTCH(4, -58, -2),
      CARRION_PATCH(3, -58, -4),
      DEEP_CARRION(8, -56, -12),
      ODIUM_DEEP(6, -56, -12);

      final int tries;
      final int minY;
      final int maxY;

      Kind(int tries, int minY, int maxY) {
         this.tries = tries;
         this.minY = minY;
         this.maxY = maxY;
      }
   }

   /** Most Carrions one deep dark chunk gets from this feature. */
   static final int MAX_DEEP = 2;
   static final int PATCH_MIN_RADIUS = 3;
   static final int PATCH_MAX_RADIUS = 5;

   private final Kind kind;

   public SculkPatchFeature(Kind kind) {
      super(NoneFeatureConfiguration.CODEC);
      this.kind = kind;
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      int minX = (origin.getX() >> 4) << 4;
      int minZ = (origin.getZ() >> 4) << 4;
      int placed = 0;
      for (int i = 0; i < this.kind.tries; i++) {
         int x = minX + random.nextInt(16);
         int z = minZ + random.nextInt(16);
         int y = this.kind.minY + random.nextInt(this.kind.maxY - this.kind.minY + 1);
         BlockPos floor = findFloor(level, x, y, z);
         if (floor == null) {
            continue;
         }
         switch (this.kind) {
            case SPLOTCH -> {
               paint(level, floor, random);
               placed++;
            }
            case CARRION_PATCH -> {
               paint(level, floor, random);
               level.setBlock(floor, ModBlocks.CARRION_MARKER.defaultBlockState(), 2);
               level.scheduleTick(floor, ModBlocks.CARRION_MARKER, 5);
               placed++;
            }
            case DEEP_CARRION -> {
               if (!level.getBlockState(floor.below()).is(Blocks.SCULK)) {
                  continue;
               }
               level.setBlock(floor, ModBlocks.CARRION_MARKER.defaultBlockState(), 2);
               level.scheduleTick(floor, ModBlocks.CARRION_MARKER, 5);
               placed++;
               if (placed >= MAX_DEEP) {
                  return true;
               }
            }
            case ODIUM_DEEP -> {
               if (!level.getBlockState(floor.below()).is(Blocks.SCULK)) {
                  continue;
               }
               level.setBlock(floor, ModBlocks.ODIUM_MARKER.defaultBlockState(), 2);
               level.scheduleTick(floor, ModBlocks.ODIUM_MARKER, 5);
               return true;
            }
         }
      }
      return placed > 0;
   }

   /** Searches down (up to 24 blocks) from y for an air block with room above it and solid, dry ground below. Null if the column holds none. */
   private static BlockPos findFloor(WorldGenLevel level, int x, int startY, int z) {
      for (int y = startY; y > startY - 24 && y > -62; y--) {
         BlockPos p = new BlockPos(x, y, z);
         if (isFloor(level, p)) {
            return p;
         }
      }
      return null;
   }

   private static boolean isFloor(WorldGenLevel level, BlockPos p) {
      BlockState below = level.getBlockState(p.below());
      if (!level.getBlockState(p).isAir() || !level.getBlockState(p.above()).isAir() || !level.getBlockState(p.above(2)).isAir()) {
         return false;
      }
      if (below.isAir() || !below.getFluidState().isEmpty() || !(below.is(BlockTags.SCULK_REPLACEABLE) || below.is(Blocks.SCULK))) {
         return false;
      }
      // Nothing wet next to it.
      for (Direction d : Direction.Plane.HORIZONTAL) {
         if (!level.getBlockState(p.relative(d)).getFluidState().isEmpty()) {
            return false;
         }
      }
      return true;
   }

   /** An uneven disc of sculk on the floor around the spot, thinning toward the edge, with a few wall blocks next to it. */
   private static void paint(WorldGenLevel level, BlockPos centre, RandomSource random) {
      int radius = PATCH_MIN_RADIUS + random.nextInt(PATCH_MAX_RADIUS - PATCH_MIN_RADIUS + 1);
      BlockState sculk = Blocks.SCULK.defaultBlockState();
      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz) / radius;
            if (d > 1.0 || random.nextDouble() > 1.0 - d * d * 0.8) {
               continue;
            }
            // The floor may be a step up or down from the middle.
            for (int dy = 2; dy >= -2; dy--) {
               BlockPos air = centre.offset(dx, dy, dz);
               BlockPos ground = air.below();
               if (level.getBlockState(air).isAir() && level.getBlockState(ground).is(BlockTags.SCULK_REPLACEABLE) && level.getBlockState(ground).getFluidState().isEmpty()) {
                  level.setBlock(ground, sculk, 2);
                  if (random.nextFloat() < 0.35F) {
                     Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                     BlockPos wall = air.relative(side);
                     if (level.getBlockState(wall).is(BlockTags.SCULK_REPLACEABLE)) {
                        level.setBlock(wall, sculk, 2);
                     }
                  }
                  break;
               }
            }
         }
      }
   }
}
