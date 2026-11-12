package com.kodari.breathless;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
   static final int CLOUD_DRAIN = 80;
   /** Lifetime a fire burns off every 10 ticks (a burning thing inside) or every 20 ticks (a fire block inside). */
   static final int FIRE_BURNS_THINGS = 120;
   static final int FIRE_BURNS_BLOCKS = 200;

   private static final class Cloud {
      final ServerLevel level;
      final Vec3 centre;
      final double baseRadius;
      int life;
      final int maxLife;

      Cloud(ServerLevel level, Vec3 centre, double radius, int life) {
         this.level = level;
         this.centre = centre;
         this.baseRadius = radius;
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

   /** The look of the gas: dark and lighter blue dust spread through the whole volume, and a shell of bright dots marking exactly where it ends. */
   private static final DustParticleOptions DARK = new DustParticleOptions(0x0A2347, 3.0F);
   private static final DustParticleOptions MID = new DustParticleOptions(0x1F5FB0, 2.2F);
   private static final DustParticleOptions EDGE = new DustParticleOptions(0x7FD6FF, 1.6F);
   /** Dots in the edge shell. */
   private static final int SHELL_POINTS = 56;
   /** The little puff a fake Odium makes: a small short cloud with no sculk spread. */
   static final double PUFF_RADIUS = 3.5;
   static final int PUFF_TICKS = 120;

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

   /** A fake Odium bursting: a small short cloud that still wears filters down, and no sculk. */
   public static void puff(ServerLevel level, Vec3 centre) {
      CLOUDS.add(new Cloud(level, centre, PUFF_RADIUS, PUFF_TICKS));
      level.sendParticles(ParticleTypes.SCULK_SOUL, centre.x, centre.y, centre.z, 20, 0.6, 0.6, 0.6, 0.1);
      level.sendParticles(DARK, centre.x, centre.y, centre.z, 40, 1.2, 0.8, 1.2, 0.0);
      level.sendParticles(ModParticles.SPORE_EYE, centre.x, centre.y, centre.z, 20, 1.0, 0.8, 1.0, 0.06);
      level.playSound(null, centre.x, centre.y, centre.z, ModSounds.ANATHEMA_BURST, SoundSource.HOSTILE, 0.8F, 1.8F);
   }

   private static Vec3 randomInSphere(ServerLevel level, Vec3 c, double r) {
      for (int i = 0; i < 12; i++) {
         double x = level.getRandom().nextDouble() * 2.0 - 1.0;
         double y = level.getRandom().nextDouble() * 2.0 - 1.0;
         double z = level.getRandom().nextDouble() * 2.0 - 1.0;
         if (x * x + y * y + z * z <= 1.0) {
            return new Vec3(c.x + x * r, c.y + y * r, c.z + z * r);
         }
      }
      return c;
   }

   /** A Carrion burst at this spot. burnt means it was on fire: the gas is mostly burnt off already. */
   public static void start(ServerLevel level, Vec3 centre, boolean burnt) {
      SPREADS.add(new Spread(level, BlockPos.containing(centre)));
      CLOUDS.add(new Cloud(level, centre, CLOUD_RADIUS, burnt ? BURNT_TICKS : CLOUD_TICKS));
      level.sendParticles(ParticleTypes.SCULK_SOUL, centre.x, centre.y, centre.z, 40, 1.0, 1.0, 1.0, 0.15);
      level.sendParticles(DARK, centre.x, centre.y, centre.z, 80, 2.5, 1.5, 2.5, 0.0);
      level.sendParticles(MID, centre.x, centre.y, centre.z, 50, 2.5, 1.5, 2.5, 0.0);
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
      double radius = cloud.baseRadius * (0.6 + 0.4 * fade);
      Vec3 c = cloud.centre;
      if (cloud.life % 2 == 0) {
         // The body: dust spread evenly through the whole volume (never outside it).
         int n = (int)(16 * fade) + 3;
         for (int i = 0; i < n; i++) {
            Vec3 p = randomInSphere(level, c, radius * 0.92);
            level.sendParticles(i % 3 == 0 ? MID : DARK, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
         }
         // The edge: bright dots over the whole sphere, turning slowly, right where the gas stops.
         double spin = cloud.life * 0.05;
         for (int k = 0; k < SHELL_POINTS; k++) {
            double yy = 1.0 - 2.0 * (k + 0.5) / SHELL_POINTS;
            double rr = Math.sqrt(1.0 - yy * yy);
            double a = k * 2.399963 + spin;
            level.sendParticles(EDGE, c.x + Math.cos(a) * rr * radius, c.y + yy * radius, c.z + Math.sin(a) * rr * radius, 1, 0.0, 0.0, 0.0, 0.0);
         }
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
