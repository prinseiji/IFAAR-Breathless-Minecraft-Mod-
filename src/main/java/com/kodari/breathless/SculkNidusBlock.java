package com.kodari.breathless;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;

/**
 * The Sculk Nidus. While it is whole it beats like a heart (louder the closer you are), lets spore eyes drift around it, and wakes Stagnateds for players nearby.
 * It can only wake a limited number of them (the "wakes" state), then goes quiet. Breaking it gives experience and two Rotting Hearts (see the loot table).
 * No block entity: it keeps its own timer with scheduled ticks and its wake count in the block state.
 */
public class SculkNidusBlock extends Block {
   private static final MapCodec<SculkNidusBlock> CODEC = simpleCodec(SculkNidusBlock::new);
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty WAKES = IntegerProperty.create("wakes", 0, Tuning.NIDUS_WAKES);
   /** One heartbeat: the sound is 0.76 s long, so every 15 ticks it plays again. */
   private static final int BEAT = 15;

   public SculkNidusBlock(BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WAKES, 0));
   }

   @Override
   protected MapCodec<SculkNidusBlock> codec() {
      return CODEC;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, WAKES);
   }

   /** The front faces whoever places it. */
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.scheduleTick(pos, this, BEAT);
      }
   }

   /** Safety net: a Nidus that lost its timer (placed while a chunk was generating, say) picks it up again. Scheduling twice does nothing. */
   @Override
   protected boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   @Override
   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      level.scheduleTick(pos, this, BEAT);
   }

   @Override
   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      level.scheduleTick(pos, this, BEAT);
      Vec3 centre = Vec3.atCenterOf(pos);
      ServerPlayer nearest = null;
      double best = Tuning.NIDUS_RANGE * Tuning.NIDUS_RANGE;
      for (ServerPlayer player : level.players()) {
         double d = player.distanceToSqr(centre);
         if (d < best) {
            best = d;
            nearest = player;
         }
      }
      if (nearest == null) {
         return;
      }
      level.playSound(null, pos, ModSounds.NIDUS_HEARTBEAT, SoundSource.BLOCKS, 1.2F, 1.0F);
      int wakes = state.getValue(WAKES);
      if (wakes >= Tuning.NIDUS_WAKES || nearest.isCreative() || nearest.isSpectator() || !Masks.isUnderground(nearest)) {
         return;
      }
      if (random.nextInt(Tuning.NIDUS_SPAWN_ODDS) == 0 && StagnatedSpawner.trySpawn(nearest)) {
         level.setBlock(pos, state.setValue(WAKES, wakes + 1), 3);
      }
   }

   /** Spore eyes drifting around it, not too many. (The game calls this now and then for blocks near the player.) */
   @Override
   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      for (int i = 0; i < 2; i++) {
         double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 4.0;
         double y = pos.getY() + 0.5 + (random.nextDouble() - 0.4) * 2.5;
         double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 4.0;
         level.addParticle(ModParticles.SPORE_EYE, x, y, z, (random.nextDouble() - 0.5) * 0.012, (random.nextDouble() - 0.5) * 0.008, (random.nextDouble() - 0.5) * 0.012);
      }
   }

   @Override
   protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience);
      if (dropExperience) {
         ExperienceOrb.award(level, Vec3.atCenterOf(pos), Tuning.NIDUS_XP);
      }
   }
}
