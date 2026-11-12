package com.kodari.breathless;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** An invisible-in-practice marker the world generator places: the first time it ticks it turns into a fake half-buried Odium (OdiumHusk) most of the time and a real buried Odium otherwise (same trick as the Carrion marker). */
public class OdiumMarkerBlock extends Block {
   private static final MapCodec<OdiumMarkerBlock> CODEC = simpleCodec(OdiumMarkerBlock::new);

   public OdiumMarkerBlock(BlockBehaviour.Properties properties) {
      super(properties);
   }

   @Override
   protected MapCodec<OdiumMarkerBlock> codec() {
      return CODEC;
   }

   @Override
   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.scheduleTick(pos, this, 5);
      }
   }

   @Override
   protected boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   @Override
   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.convert(level, pos);
   }

   @Override
   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.convert(level, pos);
   }

   /** Share of markers that become a fake (OdiumHusk) instead of a real buried Odium. */
   static final float FAKE_SHARE = 0.65F;

   private void convert(ServerLevel level, BlockPos pos) {
      float yaw = level.getRandom().nextFloat() * 360.0F;
      if (level.getRandom().nextFloat() < FAKE_SHARE) {
         OdiumHusk husk = ModEntities.ODIUM_HUSK.create(level, EntitySpawnReason.STRUCTURE);
         if (husk == null) {
            return;
         }
         husk.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, 0.0F);
         level.removeBlock(pos, false);
         level.addFreshEntity(husk);
         return;
      }
      Odium odium = ModEntities.ODIUM.create(level, EntitySpawnReason.STRUCTURE);
      if (odium == null) {
         return;
      }
      odium.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, 0.0F);
      odium.setDormant();
      level.removeBlock(pos, false);
      level.addFreshEntity(odium);
   }
}
