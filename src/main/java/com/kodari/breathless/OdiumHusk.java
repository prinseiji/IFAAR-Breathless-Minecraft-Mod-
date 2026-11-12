package com.kodari.breathless;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * A fake Odium: half buried, still, and never rises. It looks like the real thing, but one hit (or an arrow, or fire) and it bursts into a small gas puff
 * (GasClouds.puff) that wears filters down. The world generator places these next to the buried real ones.
 */
public class OdiumHusk extends Mob {
   public OdiumHusk(EntityType<? extends OdiumHusk> type, Level level) {
      super(type, level);
      this.setNoGravity(true);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   @Override
   public boolean removeWhenFarAway(double distance) {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public void push(double x, double y, double z) {
   }

   /** Only an attack, a projectile, fire or an explosion sets it off; the spores, suffocation and the like do not. */
   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
      boolean attacked = source.getEntity() != null || source.getDirectEntity() != null || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION);
      if (!attacked) {
         return false;
      }
      GasClouds.puff(level, this.position().add(0.0, 0.6, 0.0));
      level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 0.6, this.getZ(), 25, 0.5, 0.5, 0.5, 0.08);
      this.remove(Entity.RemovalReason.KILLED);
      return true;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return null;
   }
}
