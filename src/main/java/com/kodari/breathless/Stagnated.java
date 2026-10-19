package com.kodari.breathless;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** 5-L0's puppet: a Steve with a chest cavity engulfed in sculk. Stays calm until something disturbs the layer. */
public class Stagnated extends Monster {
   /** Client-side: ticks since the "spots you" pose began, -1 when idle. */
   public int spotClock = -1;
   /** Server-side: ticks left frozen in place after spotting a target. */
   private int spotTicks;

   public Stagnated(EntityType<? extends Stagnated> type, Level level) {
      super(type, level);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, Tuning.STAGNATED_HEALTH)
         .add(Attributes.MOVEMENT_SPEED, Tuning.STAGNATED_SPEED)
         .add(Attributes.ATTACK_DAMAGE, Tuning.STAGNATED_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, Tuning.STAGNATED_MAX_SIGHT)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, Tuning.STAGNATED_CHASE_MODIFIER, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      // Players first (how far depends on their exposure), then anything else living that disturbs the layer.
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, (target, level) -> this.canSense(target)));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, (target, level) -> !(target instanceof Player) && this.canSense(target)));
   }

   /** How close a target must be before the Stagnated notices it. */
   private boolean canSense(LivingEntity target) {
      if (target instanceof Stagnated || target instanceof Warden || !target.isAlive()) {
         return false;
      }
      double dist = this.distanceTo(target);
      if (target instanceof Player player) {
         if (player.isCreative() || player.isSpectator()) {
            return false;
         }
         return dist <= Tuning.stagnatedSight(Hazard.getExposure(player), Masks.hasWorkingMask(player));
      }
      if (target instanceof Zombie || target instanceof AbstractSkeleton) {
         return dist <= Tuning.STAGNATED_UNDEAD_SIGHT;
      }
      return dist <= Tuning.STAGNATED_OTHER_SIGHT;
   }

   @Override
   public void setTarget(LivingEntity target) {
      boolean first = target != null && this.getTarget() == null;
      super.setTarget(target);
      if (first && !this.level().isClientSide()) {
         this.spotTicks = Tuning.STAGNATED_SPOT_TICKS;
         this.level().broadcastEntityEvent(this, (byte)61);
         this.playSound(ModSounds.STAGNATED_SPOT, 1.2F, 1.0F);
         this.getNavigation().stop();
      }
   }

   @Override
   protected boolean isImmobile() {
      return super.isImmobile() || this.spotTicks > 0;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide()) {
         if (this.spotClock >= 0 && ++this.spotClock > Tuning.STAGNATED_SPOT_TICKS) {
            this.spotClock = -1;
         }
      } else if (this.spotTicks > 0 && --this.spotTicks == 0) {
         this.playSound(ModSounds.STAGNATED_GROWL, 1.2F, 1.0F);
      }
   }

   @Override
   public void handleEntityEvent(byte event) {
      if (event == 61) {
         this.spotClock = 0;
      } else {
         super.handleEntityEvent(event);
      }
   }

   @Override
   public boolean doHurtTarget(ServerLevel level, Entity target) {
      boolean hit = super.doHurtTarget(level, target);
      if (hit) {
         this.playSound(ModSounds.STAGNATED_HIT, 1.0F, 1.0F);
         if (target instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator()) {
            Hazard.addExposure(player, Tuning.STAGNATED_HIT_EXPOSURE);
         }
      }
      return hit;
   }

   @Override
   public int getAmbientSoundInterval() {
      return 200;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return ModSounds.STAGNATED_IDLE;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.STAGNATED_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return ModSounds.STAGNATED_DEATH;
   }

   @Override
   public boolean removeWhenFarAway(double distance) {
      return true;
   }
}
