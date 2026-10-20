package com.kodari.breathless;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.EntityPositionSource;
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
import net.minecraft.world.level.block.state.BlockState;

/** 5-L0's puppet: a Steve with a chest cavity engulfed in sculk. Stays calm until something disturbs the layer. */
public class Stagnated extends Monster {
   /** Client-side: ticks since the "spots you" pose began, -1 when idle. */
   public int spotClock = -1;
   /** Server-side: ticks left frozen in place after spotting a target. */
   private int spotTicks;
   /** Client-side: ticks since it began to climb out of the ground, -1 when not emerging. */
   public int emergeClock = -1;
   /** Server-side: ticks left of the emerge animation (immobile and untouchable until then). */
   private int emergeTicks;
   /** Where an active sculk sensor made it curious, and when it should start walking there. */
   private BlockPos investigate;
   private int investigateAt;
   private int heardCooldown;

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
      this.goalSelector.addGoal(3, new InvestigateGoal());
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
      if (this.emergeTicks > 0 || Math.abs(target.getY() - this.getY()) > Tuning.STAGNATED_VERTICAL) {
         return false;
      }
      // Height is ignored within that reach: it is oblivious, not precise.
      double dx = target.getX() - this.getX();
      double dz = target.getZ() - this.getZ();
      double dist = Math.sqrt(dx * dx + dz * dz);
      if (target instanceof Player player) {
         if (player.isCreative() || player.isSpectator()) {
            return false;
         }
         double sight = Tuning.stagnatedSight(Hazard.getExposure(player), Masks.hasWorkingMask(player));
         return dist <= (player.isSprinting() ? sight * Tuning.STAGNATED_NOISE_FACTOR : sight);
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
         if (target instanceof ServerPlayer player) {
            ServerPlayNetworking.send(player, new SpotPayload(0.0F));
         }
      }
   }

   /** Starts the climb-out-of-the-ground animation (used by 5-L0's spawns and the Stagnated Head). */
   public void startEmerging() {
      this.emergeTicks = Tuning.STAGNATED_EMERGE_TICKS;
      this.level().broadcastEntityEvent(this, (byte)62);
      this.playSound(ModSounds.STAGNATED_EMERGE, 1.5F, 1.0F);
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
      if (this.emergeTicks > 0) {
         return false;
      }
      return super.hurtServer(level, source, amount);
   }

   @Override
   protected boolean isImmobile() {
      return super.isImmobile() || this.spotTicks > 0 || this.emergeTicks > 0;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide()) {
         if (this.emergeClock >= 0) {
            this.emergeParticles();
            if (++this.emergeClock > Tuning.STAGNATED_EMERGE_TICKS) {
               this.emergeClock = -1;
            }
         }
         if (this.spotClock >= 0 && ++this.spotClock > Tuning.STAGNATED_SPOT_TICKS) {
            this.spotClock = -1;
         }
      } else if (this.emergeTicks > 0) {
         this.emergeTicks--;
      } else {
         if (this.spotTicks > 0) {
            this.faceTarget();
         }
         if (this.heardCooldown > 0) {
            this.heardCooldown--;
         } else if (this.getTarget() == null && this.tickCount % 10 == 0) {
            this.listen((ServerLevel)this.level());
         }
         if (this.spotTicks > 0 && --this.spotTicks == 0) {
            this.playSound(ModSounds.STAGNATED_GROWL, 1.2F, 1.0F);
         }
      }
   }

   /** The "spots" pose raises its head and arm, so it has to be looking at whatever it spotted. */
   private void faceTarget() {
      LivingEntity target = this.getTarget();
      if (target == null) {
         return;
      }
      float yaw = (float)(Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX()) * 180.0F / (float)Math.PI) - 90.0F;
      this.setYRot(yaw);
      this.setYBodyRot(yaw);
      this.setYHeadRot(yaw);
   }

   /**
    * Echolocation: an active sculk sensor (its tendrils are moving) sends a vibration wave to the Stagnated,
    * and it walks over to see what made the noise.
    */
   private void listen(ServerLevel level) {
      BlockPos origin = this.blockPosition();
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      BlockPos best = null;
      double bestDist = Double.MAX_VALUE;
      int r = Tuning.STAGNATED_LISTEN_RADIUS;
      int h = Tuning.STAGNATED_LISTEN_HEIGHT;
      for (int x = -r; x <= r; x++) {
         for (int z = -r; z <= r; z++) {
            for (int y = -h; y <= h; y++) {
               pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
               var state = level.getBlockState(pos);
               if (state.getBlock() instanceof SculkSensorBlock && state.getValue(SculkSensorBlock.PHASE) == SculkSensorPhase.ACTIVE) {
                  double d = pos.distSqr(origin);
                  if (d < bestDist) {
                     bestDist = d;
                     best = pos.immutable();
                  }
               }
            }
         }
      }
      if (best == null) {
         return;
      }
      int arrival = Math.max(4, (int)Math.sqrt(bestDist));
      level.sendParticles(new VibrationParticleOption(new EntityPositionSource(this, this.getEyeHeight()), arrival),
         best.getX() + 0.5, best.getY() + 0.5, best.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
      this.investigate = best;
      this.investigateAt = this.tickCount + arrival;
      this.heardCooldown = 40;
   }

   private class InvestigateGoal extends Goal {
      InvestigateGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         return Stagnated.this.investigate != null && Stagnated.this.tickCount >= Stagnated.this.investigateAt && Stagnated.this.getTarget() == null;
      }

      @Override
      public void start() {
         BlockPos at = Stagnated.this.investigate;
         Stagnated.this.getNavigation().moveTo(at.getX() + 0.5, at.getY(), at.getZ() + 0.5, 1.0);
      }

      @Override
      public boolean canContinueToUse() {
         return Stagnated.this.getTarget() == null && !Stagnated.this.getNavigation().isDone();
      }

      @Override
      public void stop() {
         Stagnated.this.investigate = null;
      }
   }

   @Override
   public void handleEntityEvent(byte event) {
      if (event == 61) {
         this.spotClock = 0;
      } else if (event == 62) {
         this.emergeClock = 0;
      } else {
         super.handleEntityEvent(event);
      }
   }

   /** Like a Warden climbing out: debris from the floor and sculk souls drifting up. */
   private void emergeParticles() {
      BlockState below = this.level().getBlockState(this.blockPosition().below());
      if (!below.isAir()) {
         for (int i = 0; i < 4; i++) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, below), this.getRandomX(0.9), this.getY() + 0.05, this.getRandomZ(0.9), 0.0, 0.15, 0.0);
         }
      }
      if (this.emergeClock % 3 == 0) {
         this.level().addParticle(ParticleTypes.SCULK_SOUL, this.getRandomX(0.7), this.getY() + this.random.nextDouble() * 1.2, this.getRandomZ(0.7), 0.0, 0.06, 0.0);
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
