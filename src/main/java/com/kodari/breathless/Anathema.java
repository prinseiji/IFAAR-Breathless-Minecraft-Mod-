package com.kodari.breathless;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The Sculk Anathema (it used to be the Sculk Nidus block). It stands still, beats like a heart (louder the closer you are), has spore eyes drifting around it and
 * wakes Stagnateds for players nearby, up to a limit, then goes quiet. Killing it drops three Rotting Hearts (loot table entities/anathema) and experience.
 * When it dies the death clip plays, the death pose is held for a moment, then it bursts into sculk.
 */
public class Anathema extends Mob {
   /** How many Stagnateds it has woken so far (it stops at Tuning.NIDUS_WAKES). */
   private int wakes;

   public Anathema(EntityType<? extends Anathema> type, Level level) {
      super(type, level);
      this.setNoGravity(true);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, Tuning.ANATHEMA_HEALTH)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.SCALE, Tuning.ANATHEMA_SCALE)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   public void setWakes(int wakes) {
      this.wakes = wakes;
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

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         // Spore eyes drifting around it (none once it is dying).
         if (this.isAlive() && this.random.nextInt(3) == 0) {
            for (int i = 0; i < 2; i++) {
               double x = this.getX() + (this.random.nextDouble() - 0.5) * 4.0;
               double y = this.getY() + 0.5 + (this.random.nextDouble() - 0.4) * 2.5;
               double z = this.getZ() + (this.random.nextDouble() - 0.5) * 4.0;
               this.level().addParticle(ModParticles.SPORE_EYE, x, y, z, (this.random.nextDouble() - 0.5) * 0.012, (this.random.nextDouble() - 0.5) * 0.008, (this.random.nextDouble() - 0.5) * 0.012);
            }
         }
         return;
      }
      if (this.isAlive() && this.tickCount % 15 == 0) {
         this.pulse((ServerLevel)this.level());
      }
   }

   /** The heartbeat, and now and then a Stagnated for the nearest player (the same rules the block had). */
   private void pulse(ServerLevel level) {
      Vec3 centre = this.position().add(0.0, this.getBbHeight() * 0.5, 0.0);
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
      level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.NIDUS_HEARTBEAT, SoundSource.HOSTILE, 1.2F, 1.0F);
      if (this.wakes >= Tuning.NIDUS_WAKES || nearest.isCreative() || nearest.isSpectator() || !Masks.isUnderground(nearest)) {
         return;
      }
      if (this.random.nextInt(Tuning.NIDUS_SPAWN_ODDS) == 0 && StagnatedSpawner.trySpawn(nearest)) {
         this.wakes++;
      }
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (this.level() instanceof ServerLevel level) {
         ExperienceOrb.award(level, this.position(), Tuning.NIDUS_XP);
      }
   }

   /** A longer death than the usual second: the death clip (3 s) and a held pose, then the burst. */
   @Override
   protected void tickDeath() {
      this.deathTime++;
      if (this.level() instanceof ServerLevel dying && this.deathTime % 2 == 0 && this.deathTime < Tuning.ANATHEMA_DEATH_TICKS) {
         // It crumbles while the death clip plays: sculk chips and the floating spore eyes drifting off it.
         double h = this.getBbHeight();
         dying.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SCULK.defaultBlockState()), this.getX(), this.getY() + h * 0.5, this.getZ(), 8, this.getBbWidth() * 0.4, h * 0.35, this.getBbWidth() * 0.4, 0.05);
         dying.sendParticles(ModParticles.SPORE_EYE, this.getX(), this.getY() + h * 0.5, this.getZ(), 5, this.getBbWidth() * 0.6, h * 0.4, this.getBbWidth() * 0.6, 0.02);
      }
      if (this.deathTime >= Tuning.ANATHEMA_DEATH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
         if (this.level() instanceof ServerLevel level) {
            double y = this.getY() + this.getBbHeight() * 0.5;
            double w = this.getBbWidth();
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SCULK.defaultBlockState()), this.getX(), y, this.getZ(), 200, w * 0.5, this.getBbHeight() * 0.4, w * 0.5, 0.2);
            level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), y, this.getZ(), 60, w * 0.4, this.getBbHeight() * 0.4, w * 0.4, 0.1);
            // The spore eyes scatter in every direction and drift away: the air around it disperses.
            level.sendParticles(ModParticles.SPORE_EYE, this.getX(), y, this.getZ(), 120, w * 0.8, this.getBbHeight() * 0.5, w * 0.8, 0.06);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ANATHEMA_BURST, SoundSource.HOSTILE, 2.5F, 1.0F);
         }
         this.remove(Entity.RemovalReason.KILLED);
      }
   }

   @Override
   public int getAmbientSoundInterval() {
      return 140;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return ModSounds.ANATHEMA_IDLE[this.random.nextInt(ModSounds.ANATHEMA_IDLE.length)];
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.ANATHEMA_HIT[this.random.nextInt(ModSounds.ANATHEMA_HIT.length)];
   }

   @Override
   protected SoundEvent getDeathSound() {
      return ModSounds.ANATHEMA_DEATH;
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("Wakes", this.wakes);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.wakes = input.getIntOr("Wakes", 0);
   }
}
