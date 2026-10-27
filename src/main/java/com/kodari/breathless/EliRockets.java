package com.kodari.breathless;

import java.util.Iterator;
import java.util.List;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Eli's firework rockets. They are not real firework entities: a fast, slightly homing shot that is stepped by hand,
 * so the speed, the damage and the cyan / dark teal burst are all ours. A raised shield facing him stops it completely.
 */
public final class EliRockets {
   private EliRockets() {
   }

   /** One rocket in flight. */
   public static final class Rocket {
      double x;
      double y;
      double z;
      double vx;
      double vy;
      double vz;
      LivingEntity target;
      float damage;
      boolean burn;
      double speed;
      double homing;
      int age;
   }

   /** Damage before armor by distance to the target: close hurts most (the player has to alternate between close and far). */
   public static float damageAt(double dist) {
      double[][] pts = {{2.5, 24.0}, {4.5, 19.0}, {8.0, 14.0}, {12.0, 9.0}};
      if (dist <= pts[0][0]) {
         return (float)pts[0][1];
      }
      for (int i = 1; i < pts.length; i++) {
         if (dist <= pts[i][0]) {
            double k = (dist - pts[i - 1][0]) / (pts[i][0] - pts[i - 1][0]);
            return (float)(pts[i - 1][1] + (pts[i][1] - pts[i - 1][1]) * k);
         }
      }
      return (float)pts[pts.length - 1][1];
   }

   public static void fire(ServerLevel level, Stagnated eli, LivingEntity target, float damage, boolean burn, boolean air, List<Rocket> list) {
      double yaw = Math.toRadians(eli.getYHeadRot());
      Rocket r = new Rocket();
      // The muzzle: in front of him at hand height, a little to his left (the crossbow is in his left hand).
      r.x = eli.getX() - Math.sin(yaw) * 0.8 + Math.cos(yaw) * 0.3;
      r.y = eli.getY() + 1.35;
      r.z = eli.getZ() + Math.cos(yaw) * 0.8 + Math.sin(yaw) * 0.3;
      Vec3 aim = target.getBoundingBox().getCenter().subtract(r.x, r.y, r.z).normalize();
      r.speed = air ? Tuning.XB_AIR_SPEED : Tuning.XB_SPEED;
      r.homing = air ? 0.05 : 0.09;
      r.vx = aim.x;
      r.vy = aim.y;
      r.vz = aim.z;
      r.target = target;
      r.damage = damage;
      r.burn = burn;
      list.add(r);
   }

   public static void tick(ServerLevel level, Stagnated eli, List<Rocket> list) {
      for (Iterator<Rocket> it = list.iterator(); it.hasNext();) {
         Rocket r = it.next();
         if (step(level, eli, r)) {
            it.remove();
         }
      }
   }

   /** Moves one rocket a tick (in short steps so it cannot skip through anything). Returns true when it is done. */
   private static boolean step(ServerLevel level, Stagnated eli, Rocket r) {
      r.age++;
      int steps = Math.max(1, (int)Math.ceil(r.speed / 0.6));
      double len = r.speed / steps;
      for (int i = 0; i < steps; i++) {
         if (r.target != null && r.target.isAlive()) {
            Vec3 want = r.target.getBoundingBox().getCenter().subtract(r.x, r.y, r.z).normalize();
            Vec3 dir = new Vec3(r.vx, r.vy, r.vz).scale(1.0 - r.homing).add(want.scale(r.homing)).normalize();
            r.vx = dir.x;
            r.vy = dir.y;
            r.vz = dir.z;
         }
         Vec3 from = new Vec3(r.x, r.y, r.z);
         Vec3 to = from.add(r.vx * len, r.vy * len, r.vz * len);
         HitResult block = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, eli));
         Vec3 end = block.getType() == HitResult.Type.MISS ? to : block.getLocation();
         LivingEntity hit = null;
         double best = Double.MAX_VALUE;
         for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(from, end).inflate(0.4), e -> e != eli && !(e instanceof Stagnated) && e.isAlive() && !e.isSpectator()
            && !(e instanceof Player p && p.isCreative()))) {
            double d = e.getBoundingBox().getCenter().distanceToSqr(from);
            if (d < best && e.getBoundingBox().inflate(0.3).clip(from, end).isPresent()) {
               best = d;
               hit = e;
            }
         }
         if (hit != null) {
            explode(level, eli, r, hit.getBoundingBox().getCenter(), hit);
            return true;
         }
         if (block.getType() != HitResult.Type.MISS) {
            explode(level, eli, r, end, null);
            return true;
         }
         r.x = to.x;
         r.y = to.y;
         r.z = to.z;
         level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, r.x, r.y, r.z, 1, 0.03, 0.03, 0.03, 0.0);
         if (i == 0) {
            level.sendParticles(ParticleTypes.END_ROD, r.x, r.y, r.z, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
      if (r.age > 90) {
         explode(level, eli, r, new Vec3(r.x, r.y, r.z), null);
         return true;
      }
      return false;
   }

   private static boolean shieldStops(LivingEntity e, Stagnated eli) {
      if (!e.isBlocking()) {
         return false;
      }
      Vec3 to = new Vec3(eli.getX() - e.getX(), 0.0, eli.getZ() - e.getZ());
      Vec3 look = e.getViewVector(1.0F);
      Vec3 flat = new Vec3(look.x, 0.0, look.z);
      if (to.lengthSqr() < 1.0E-4 || flat.lengthSqr() < 1.0E-4) {
         return true;
      }
      return to.normalize().dot(flat.normalize()) > 0.0;
   }

   private static void explode(ServerLevel level, Stagnated eli, Rocket r, Vec3 at, LivingEntity primary) {
      level.playSound(null, at.x, at.y, at.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 3.0F, 0.9F);
      level.playSound(null, at.x, at.y, at.z, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 2.5F, 1.0F);
      level.playSound(null, at.x, at.y, at.z, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.HOSTILE, 2.0F, 0.8F);
      // Cyan and dark teal: a bright expanding ball, a darker cloud, and sculk souls drifting off it.
      level.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH, 0xFF0F9F98), at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      for (int i = 0; i < 56; i++) {
         double a = eli.getRandom().nextDouble() * Math.PI * 2.0;
         double b = Math.acos(2.0 * eli.getRandom().nextDouble() - 1.0);
         level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, at.x, at.y, at.z, 0, Math.sin(b) * Math.cos(a), Math.cos(b), Math.sin(b) * Math.sin(a), 0.35);
      }
      level.sendParticles(new DustParticleOptions(0x064B49, 2.2F), at.x, at.y, at.z, 34, 0.9, 0.9, 0.9, 0.0);
      level.sendParticles(new DustParticleOptions(0x0F9F98, 1.6F), at.x, at.y, at.z, 22, 0.7, 0.7, 0.7, 0.0);
      level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y, at.z, 18, 0.5, 0.5, 0.5, 0.12);
      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(Tuning.XB_BLAST_RADIUS), e -> e != eli && !(e instanceof Stagnated) && e.isAlive() && !e.isSpectator()
         && !(e instanceof Player p && p.isCreative()))) {
         float dmg = e == primary ? r.damage : r.damage * 0.6F;
         if (shieldStops(e, eli)) {
            level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.4F, 0.9F);
            level.sendParticles(ParticleTypes.CRIT, e.getX(), e.getEyeY(), e.getZ(), 10, 0.3, 0.2, 0.3, 0.2);
            continue;
         }
         if (e.hurtServer(level, ModDamage.eliRocket(level, eli), dmg)) {
            if (r.burn && e == primary) {
               e.igniteForSeconds(Tuning.XB_BURN_SECONDS);
            }
            Vec3 push = e.position().subtract(at).multiply(1.0, 0.0, 1.0);
            if (push.lengthSqr() > 1.0E-4) {
               push = push.normalize().scale(0.5);
               e.setDeltaMovement(e.getDeltaMovement().add(push.x, 0.25, push.z));
               e.hurtMarked = true;
            }
         }
      }
   }
}
