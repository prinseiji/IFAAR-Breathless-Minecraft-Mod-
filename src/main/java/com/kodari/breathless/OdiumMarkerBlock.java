package com.kodari.breathless;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** An invisible-in-practice marker the world generator places: the first time it ticks it turns into an Odium, an Ire or the fakes of either (OdiumHusk, IreHusk) (same trick as the Carrion marker). */
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

   /** Share of markers that become a fake (OdiumHusk) instead of a real buried Odium, and the share that become an Ire (or its fake) instead of an Odium. */
   static final float FAKE_SHARE = 0.65F;
   static final float IRE_SHARE = 0.35F;

   private void convert(ServerLevel level, BlockPos pos) {
      float yaw = level.getRandom().nextFloat() * 360.0F;
      boolean fake = level.getRandom().nextFloat() < FAKE_SHARE;
      if (level.getRandom().nextFloat() < IRE_SHARE) {
         Entity ire = fake ? ModEntities.IRE_HUSK.create(level, EntitySpawnReason.STRUCTURE) : ModEntities.IRE.create(level, EntitySpawnReason.STRUCTURE);
         this.place(level, pos, ire, yaw);
         return;
      }
      if (fake) {
         this.place(level, pos, ModEntities.ODIUM_HUSK.create(level, EntitySpawnReason.STRUCTURE), yaw);
         return;
      }
      Odium odium = ModEntities.ODIUM.create(level, EntitySpawnReason.STRUCTURE);
      if (odium != null) {
         odium.setDormant();
      }
      this.place(level, pos, odium, yaw);
   }

   private void place(ServerLevel level, BlockPos pos, Entity entity, float yaw) {
      if (entity == null) {
         return;
      }
      entity.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, 0.0F);
      level.removeBlock(pos, false);
      level.addFreshEntity(entity);
   }
}
