package com.kodari.breathless;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * What a Carrion leaves behind. Two things, both kept in memory (they are short-lived, so a restart just clears them):
 * the sculk spread (natural blocks near the burst turn to sculk in a growing shell, nothing is destroyed) and the gas cloud (wears filters down fast, fire burns it away).
 */
public final class GasClouds {
   /** How far the sculk spreads, and the chance a surface block in the shell turns (falls off with distance). */
   static final int SPREAD_RADIUS = 10;
   static final double SPREAD_PER_TICK = 0.8;
   /** The gas: radius, lifetime in ticks (a burnt Carrion only leaves a thin one), and filter life taken from a player inside every 10 ticks (the two filters take turns). */
   static final double CLOUD_RADIUS = 7.0;
   static final int CLOUD_TICKS = 600;
   static final int BURNT_TICKS = 100;
   static final int CLOUD_DRAIN = 30;
   /** Lifetime a fire burns off every 10 ticks (a burning thing inside) or every 20 ticks (a fire block inside). */
   static final int FIRE_BURNS_THINGS = 120;
   static final int FIRE_BURNS_BLOCKS = 200;

   private static final class Cloud {
      final ServerLevel level;
      final Vec3 centre;
      int life;
      final int maxLife;

      Cloud(ServerLevel level, Vec3 centre, int life) {
         this.level = level;
         this.centre = centre;
         this.life = life;
         this.maxLife = life;
      }
   }

   private static final class Spread {
      final ServerLevel level;
      final BlockPos centre;
      double radius = 0.0;

      Spread(ServerLevel level, BlockPos centre) {
         this.level = level;
         this.centre = centre;
      }
   }

   private static final List<Cloud> CLOUDS = new ArrayList<>();
   private static final List<Spread> SPREADS = new ArrayList<>();

   private GasClouds() {
   }

   public static void init() {
      ServerTickEvents.END_SERVER_TICK.register(GasClouds::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
         CLOUDS.clear();
         SPREADS.clear();
      });
   }

   /** A Carrion burst at this spot. burnt means it was on fire: the gas is mostly burnt off already. */
   public static void start(ServerLevel level, Vec3 centre, boolean burnt) {
      SPREADS.add(new Spread(level, BlockPos.containing(centre)));
      CLOUDS.add(new Cloud(level, centre, burnt ? BURNT_TICKS : CLOUD_TICKS));
      level.sendParticles(ParticleTypes.SCULK_SOUL, centre.x, centre.y, centre.z, 40, 1.0, 1.0, 1.0, 0.15);
      level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, centre.x, centre.y, centre.z, 120, 2.5, 1.5, 2.5, 0.2);
      level.sendParticles(ModParticles.SPORE_EYE, centre.x, centre.y, centre.z, 60, 2.0, 1.2, 2.0, 0.08);
      level.playSound(null, centre.x, centre.y, centre.z, ModSounds.ANATHEMA_BURST, SoundSource.HOSTILE, 1.5F, 1.4F);
   }

   private static void tick(MinecraftServer server) {
      // Work on copies: spreading and burning never change the lists mid-loop, but this keeps it safe.
      for (Spread spread : new ArrayList<>(SPREADS)) {
         if (spread.radius > SPREAD_RADIUS) {
            SPREADS.remove(spread);
            continue;
         }
         spreadShell(spread);
         spread.radius += SPREAD_PER_TICK;
      }
      for (Cloud cloud : new ArrayList<>(CLOUDS)) {
         tickCloud(cloud);
         if (--cloud.life <= 0) {
            CLOUDS.remove(cloud);
         }
      }
   }

   /** Turn the natural surface blocks in the shell between radius and radius + 1.2 into sculk. */
   private static void spreadShell(Spread spread) {
      ServerLevel level = spread.level;
      double r0 = spread.radius;
      double r1 = r0 + 1.2;
      int reach = (int)Math.ceil(r1);
      BlockState sculk = Blocks.SCULK.defaultBlockState();
      for (int dx = -reach; dx <= reach; dx++) {
         for (int dy = -reach; dy <= reach; dy++) {
            for (int dz = -reach; dz <= reach; dz++) {
               double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
               if (dist < r0 || dist >= r1) {
                  continue;
               }
               BlockPos pos = spread.centre.offset(dx, dy, dz);
               if (!level.hasChunkAt(pos) || level.getRandom().nextDouble() > 0.95 - 0.5 * (dist / SPREAD_RADIUS)) {
                  continue;
               }
               BlockState st = level.getBlockState(pos);
               if (!st.is(BlockTags.SCULK_REPLACEABLE) || !exposed(level, pos)) {
                  continue;
               }
               level.setBlock(pos, sculk, 3);
            }
         }
      }
      if (spread.radius < SPREAD_RADIUS) {
         level.sendParticles(ParticleTypes.SCULK_SOUL, spread.centre.getX() + 0.5, spread.centre.getY() + 0.5, spread.centre.getZ() + 0.5, 3, spread.radius * 0.5, spread.radius * 0.4, spread.radius * 0.5, 0.02);
      }
   }

   /** Only the surface: a block with air next to it. */
   private static boolean exposed(ServerLevel level, BlockPos pos) {
      return level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.below()).isAir() || level.getBlockState(pos.north()).isAir()
         || level.getBlockState(pos.south()).isAir() || level.getBlockState(pos.east()).isAir() || level.getBlockState(pos.west()).isAir();
   }

   private static void tickCloud(Cloud cloud) {
      ServerLevel level = cloud.level;
      // The cloud thins out over its last fifth.
      double fade = Math.min(1.0, cloud.life / (cloud.maxLife * 0.2));
      double radius = CLOUD_RADIUS * (0.6 + 0.4 * fade);
      Vec3 c = cloud.centre;
      if (cloud.life % 2 == 0) {
         level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, c.x, c.y, c.z, (int)(14 * fade) + 1, radius * 0.6, radius * 0.4, radius * 0.6, 0.02);
      }
      if (cloud.life % 10 == 0) {
         level.sendParticles(ModParticles.SPORE_EYE, c.x, c.y, c.z, 2, radius * 0.6, radius * 0.4, radius * 0.6, 0.01);
      }
      // Filters: every player inside, on the same 10 tick beat the normal wear uses so the two filters still take turns.
      for (ServerPlayer player : level.players()) {
         if (player.isCreative() || player.isSpectator() || !player.isAlive() || player.tickCount % Tuning.DRAIN_INTERVAL_TICKS != 0) {
            continue;
         }
         if (player.distanceToSqr(c) <= radius * radius) {
            Masks.drainFilter(player, CLOUD_DRAIN);
         }
      }
      // Fire: burning things inside burn the gas off quickly, so do fire blocks and lava.
      AABB box = new AABB(c.x - radius, c.y - radius, c.z - radius, c.x + radius, c.y + radius, c.z + radius);
      if (cloud.life % 10 == 0) {
         for (Entity e : level.getEntities((Entity)null, box, Entity::isOnFire)) {
            if (e.distanceToSqr(c) <= radius * radius) {
               cloud.life -= FIRE_BURNS_THINGS;
               level.sendParticles(ParticleTypes.FLAME, e.getX(), e.getY() + 0.5, e.getZ(), 12, 0.6, 0.5, 0.6, 0.03);
               level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.6F, 1.0F);
               break;
            }
         }
      }
      if (cloud.life % 20 == 0) {
         BlockPos origin = BlockPos.containing(c);
         int r = (int)Math.ceil(radius);
         for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-r, -r, -r), origin.offset(r, r, r))) {
            BlockState st = level.getBlockState(pos);
            if ((st.is(BlockTags.FIRE) || st.is(Blocks.LAVA)) && pos.distToCenterSqr(c) <= radius * radius) {
               cloud.life -= FIRE_BURNS_BLOCKS;
               level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.03);
               break;
            }
         }
      }
   }
}
