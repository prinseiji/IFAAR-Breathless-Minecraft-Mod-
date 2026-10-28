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
import java.util.List;

/**
 * A patch of sculk on the floor of a cave with a Sculk Nidus in the middle, a few catalysts and sensors, and one shrieker guarding it.
 * It uses a cave that is already there and changes nothing but the floor, so it joins onto caves. The guard shrieker always has open air on all four sides and above,
 * and the other sculk blocks stay at least 4 blocks from it, so it can be walled off with wool (or just broken). It can summon a warden.
 * Nothing is placed unless a cave with room was found, so most tries do nothing; how often it is tried is in the placed feature json.
 */
public class NidusPocketFeature extends Feature<NoneFeatureConfiguration> {
   public NidusPocketFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      // 1. Find a spot on a cave floor with plenty of open air around it.
      BlockPos centre = null;
      for (int i = 0; i < 30 && centre == null; i++) {
         BlockPos floor = floorAt(level, origin, origin.getX() + random.nextInt(9) - 4, origin.getZ() + random.nextInt(9) - 4, origin.getY() + random.nextInt(21) - 10);
         if (floor != null && openRoom(level, origin, floor)) {
            centre = floor;
         }
      }
      if (centre == null) {
         return false;
      }
      // 2. Choose the spots before changing anything: the guard shrieker first (clear on all sides), then the rest away from it.
      BlockPos shrieker = null;
      for (int i = 0; i < 60 && shrieker == null; i++) {
         BlockPos spot = pick(level, origin, centre, random, 4, 8);
         if (spot != null && clearAround(level, spot)) {
            shrieker = spot;
         }
      }
      if (shrieker == null) {
         return false;
      }
      List<BlockPos> taken = new ArrayList<>();
      taken.add(shrieker);
      taken.add(centre);
      List<BlockPos> catalysts = new ArrayList<>();
      List<BlockPos> sensors = new ArrayList<>();
      for (int i = 0; i < 80 && (catalysts.size() < Tuning.NIDUS_POCKET_CATALYSTS || sensors.size() < Tuning.NIDUS_POCKET_SENSORS); i++) {
         BlockPos spot = pick(level, origin, centre, random, 2, 8);
         if (spot == null || farEnough(spot, shrieker, 4) == false) {
            continue;
         }
         boolean free = true;
         for (BlockPos t : taken) {
            if (Math.abs(t.getX() - spot.getX()) <= 1 && Math.abs(t.getZ() - spot.getZ()) <= 1) {
               free = false;
            }
         }
         if (!free) {
            continue;
         }
         if (catalysts.size() < Tuning.NIDUS_POCKET_CATALYSTS) {
            catalysts.add(spot);
         } else {
            sensors.add(spot);
         }
         taken.add(spot);
      }
      // 3. Now build it: a patch of sculk on the floor, then the blocks on top.
      int rx = Tuning.NIDUS_POCKET_RX;
      int rz = Tuning.NIDUS_POCKET_RZ;
      boolean swap = random.nextBoolean();
      for (int dx = -rx; dx <= rx; dx++) {
         for (int dz = -rx; dz <= rx; dz++) {
            double a = (double)(swap ? dz : dx) / rx;
            double b = (double)(swap ? dx : dz) / rz;
            if (a * a + b * b > 1.0 + random.nextFloat() * 0.25) {
               continue;
            }
            BlockPos floor = floorAt(level, origin, centre.getX() + dx, centre.getZ() + dz, centre.getY());
            if (floor != null) {
               BlockPos ground = floor.below();
               if (level.getBlockState(ground).is(BlockTags.SCULK_REPLACEABLE)) {
                  level.setBlock(ground, Blocks.SCULK.defaultBlockState(), 2);
               }
            }
         }
      }
      level.setBlock(centre.below(), Blocks.SCULK.defaultBlockState(), 2);
      BlockState nidus = ModBlocks.SCULK_NIDUS.defaultBlockState().setValue(SculkNidusBlock.FACING, Direction.from2DDataValue(random.nextInt(4)));
      level.setBlock(centre, nidus, 2);
      level.scheduleTick(centre, ModBlocks.SCULK_NIDUS, 15);
      level.setBlock(shrieker, Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 2);
      for (BlockPos p : catalysts) {
         level.setBlock(p, Blocks.SCULK_CATALYST.defaultBlockState(), 2);
      }
      for (BlockPos p : sensors) {
         level.setBlock(p, Blocks.SCULK_SENSOR.defaultBlockState(), 2);
      }
      return true;
   }

   /** Only touch the chunk being generated and its neighbours (a feature may not reach further). */
   private static boolean inRegion(BlockPos origin, int x, int z) {
      return Math.abs((x >> 4) - (origin.getX() >> 4)) <= 1 && Math.abs((z >> 4) - (origin.getZ() >> 4)) <= 1;
   }

   /** The first open spot above a solid floor in this column, looking a few blocks up and down from y. Null when there is none, or the floor is no use. */
   private static BlockPos floorAt(WorldGenLevel level, BlockPos origin, int x, int z, int y) {
      if (!inRegion(origin, x, z)) {
         return null;
      }
      for (int dy = 3; dy >= -4; dy--) {
         BlockPos p = new BlockPos(x, y + dy, z);
         BlockState here = level.getBlockState(p);
         BlockState below = level.getBlockState(p.below());
         if (here.isAir() && level.getBlockState(p.above()).isAir() && !below.isAir() && below.getFluidState().isEmpty() && below.is(BlockTags.SCULK_REPLACEABLE)) {
            return p;
         }
      }
      return null;
   }

   /** Enough open air around the floor spot to be a proper cave: at least 60% of a 9 by 9 by 3 box. */
   private static boolean openRoom(WorldGenLevel level, BlockPos origin, BlockPos floor) {
      int air = 0;
      for (int dx = -4; dx <= 4; dx++) {
         for (int dz = -4; dz <= 4; dz++) {
            if (!inRegion(origin, floor.getX() + dx, floor.getZ() + dz)) {
               return false;
            }
            for (int dy = 0; dy < 3; dy++) {
               BlockState st = level.getBlockState(floor.offset(dx, dy, dz));
               if (st.isAir()) {
                  air++;
               } else if (!st.getFluidState().isEmpty()) {
                  return false;
               }
            }
         }
      }
      return air >= 146;
   }

   /** A random floor spot between min and max blocks from the middle. */
   private static BlockPos pick(WorldGenLevel level, BlockPos origin, BlockPos centre, RandomSource random, int min, int max) {
      double angle = random.nextDouble() * Math.PI * 2.0;
      double dist = min + random.nextDouble() * (max - min);
      return floorAt(level, origin, centre.getX() + (int)Math.round(Math.cos(angle) * dist), centre.getZ() + (int)Math.round(Math.sin(angle) * dist), centre.getY());
   }

   private static boolean farEnough(BlockPos a, BlockPos b, int blocks) {
      return Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getZ() - b.getZ())) >= blocks;
   }

   /** Open air on all four sides and above (two blocks up), so wool can go all the way round it. */
   private static boolean clearAround(WorldGenLevel level, BlockPos p) {
      for (Direction d : Direction.Plane.HORIZONTAL) {
         if (!level.getBlockState(p.relative(d)).isAir()) {
            return false;
         }
      }
      return level.getBlockState(p.above()).isAir() && level.getBlockState(p.above(2)).isAir();
   }
}
