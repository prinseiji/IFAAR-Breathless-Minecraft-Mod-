package com.kodari.breathless;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The Odium: a lesser, hunched Stagnated. It starts buried and rises when a player comes near (DORMANT, EMERGE). It wanders slowly (PASSIVE), but when a sculk sensor
 * near it goes off it locks onto the nearest player (SPOT), and then it is very fast and reckless (HUNT). Close up it lunges to catch you in its arms (ATTEMPT).
 * A catch (SUCCESS, TRAPPED) holds you in place while it shrieks and calls a Stagnated or two; it does no damage itself. Hit it hard enough, or wait for the shriek to
 * finish, and it lets go (ESCAPE), then goes back to chasing. A missed lunge (FAIL) gives you a moment to run.
 * The state is synced to the client, which picks the matching clip (OdiumModel).
 */
public class Odium extends Monster {
   public static final int DORMANT = 0;
   public static final int EMERGE = 1;
   public static final int PASSIVE = 2;
   public static final int SPOT = 3;
   public static final int HUNT = 4;
   public static final int ATTEMPT = 5;
   public static final int SUCCESS = 6;
   public static final int FAIL = 7;
   public static final int TRAPPED = 8;
   public static final int ESCAPE = 9;

   private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Odium.class, EntityDataSerializers.INT);

   // Numbers to tune. Times are in ticks (20 per second) and match the clip lengths.
   static final double HEALTH = 24.0;
   /** Base speed, and the multipliers: sluggish when passive, very fast when hunting. */
   static final double SPEED = 0.3;
   static final double PASSIVE_SPEED = 0.4;
   static final double HUNT_SPEED = 1.7;
   /** Rising from the ground: the emerge clip is 7.92 s. */
   static final int EMERGE_TICKS = 158;
   /** The spot clip is 0.83 s. */
   static final int SPOT_TICKS = 17;
   /** Lunge clip (0.71 s): it springs at 0.33 s and decides at 0.63 s whether it caught you. */
   static final int LUNGE_AT = 7;
   static final int DECIDE_AT = 13;
   /** Success clip (0.79 s): the catch lands at 0.13 s. */
   static final int CATCH_AT = 3;
   static final int SUCCESS_TICKS = 16;
   /** Fail clip is 0.42 s. */
   static final int FAIL_TICKS = 9;
   /** How long it holds you: the length of its trapped sound (11.3 s). */
   static final int TRAP_HOLD_TICKS = 226;
   /** Escape clip (1.04 s): you are let go at 0.67 s. */
   static final int RELEASE_AT = 13;
   static final int ESCAPE_TICKS = 21;
   /** Damage you must deal to it while held to break free. */
   static final float ESCAPE_DAMAGE = 8.0F;
   /** Ticks into the hold when it calls a Stagnated (the shriek sound peaks near 1.5 s and 7.5 s). */
   static final int[] SUMMON_AT = {30, 150};
   /** It lunges from this close, and catches you if you are within GRAB_RANGE when the lunge lands. */
   static final double ATTEMPT_RANGE = 3.4;
   static final double GRAB_RANGE = 2.5;
   /** How close a sculk sensor going off must be, how close a noisy player must be, how close a player wakes it from the ground, how far it chases before giving up. */
   static final int SENSOR_RANGE = 12;
   static final double HEAR_RANGE = 4.0;
   static final double WAKE_RANGE = 8.0;
   static final double GIVE_UP = 56.0;
   /** Breathing room after a lunge before the next one. */
   static final int TRAP_COOLDOWN = 80;
   static final int FAIL_COOLDOWN = 40;

   /** Players that are being held right now, by any Odium. Cleared when the server stops. */
   static final Set<UUID> HELD = new HashSet<>();

   private int stateTicks;
   /** Client only: ticks since the last hit, or -1. */
   private int hitTicks = -1;
   private int trapCooldown;
   private int noPathTicks;
   private float escapeDamage;
   private ServerPlayer quarry;
   private ServerPlayer held;

   public Odium(EntityType<? extends Odium> type, Level level) {
      super(type, level);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, HEALTH)
         .add(Attributes.MOVEMENT_SPEED, SPEED)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STATE, PASSIVE);
   }

   public int getState() {
      return this.entityData.get(STATE);
   }

   /** Used by the world generator: it starts buried and rises when someone comes near. */
   public void setDormant() {
      this.entityData.set(STATE, DORMANT);
   }

   public float stateSeconds(float partialTick) {
      return (this.stateTicks + partialTick) / 20.0F;
   }

   public float hitSeconds(float partialTick) {
      return this.hitTicks < 0 ? -1.0F : (this.hitTicks + partialTick) / 20.0F;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (STATE.equals(accessor)) {
         this.stateTicks = 0;
      }
   }

   @Override
   public void handleEntityEvent(byte id) {
      if (id == 2) {
         this.hitTicks = 0;
      }
      super.handleEntityEvent(id);
   }

   @Override
   public boolean removeWhenFarAway(double distance) {
      return false;
   }

   /** A player this can lock onto: alive, not creative or spectator, and not wearing the Silence and Invisibility cloak. */
   static boolean valid(ServerPlayer player) {
      return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator() && !Silence.ghost(player);
   }

   private void setState(int state) {
      this.stateTicks = 0;
      this.entityData.set(STATE, state);
      if (this.level() instanceof ServerLevel level) {
         switch (state) {
            case EMERGE -> this.play(level, ModSounds.STAGNATED_EMERGE, 3.0F);
            case SPOT -> this.play(level, ModSounds.ODIUM_SPOT[this.random.nextInt(ModSounds.ODIUM_SPOT.length)], 3.0F);
            case ATTEMPT -> this.play(level, ModSounds.ODIUM_TRAP_ATTEMPT, 2.5F);
            case SUCCESS -> this.play(level, ModSounds.ODIUM_TRAP_SUCCESS, 2.5F);
            case FAIL -> this.play(level, ModSounds.ODIUM_TRAP_FAIL, 2.5F);
            case TRAPPED -> {
               this.play(level, ModSounds.ODIUM_TRAPPED, 3.0F);
               // The shriek.
               level.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 1.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
               level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 1.2, this.getZ(), 30, 0.6, 0.6, 0.6, 0.1);
            }
            case ESCAPE -> this.play(level, ModSounds.ODIUM_ESCAPE, 2.5F);
            default -> {
            }
         }
      }
   }

   private void play(ServerLevel level, SoundEvent sound, float volume) {
      level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, volume, 1.0F);
   }

   private void freeze() {
      this.getNavigation().stop();
      Vec3 v = this.getDeltaMovement();
      this.setDeltaMovement(0.0, v.y, 0.0);
   }

   private void face(ServerPlayer target) {
      if (target == null) {
         return;
      }
      float yaw = (float)(Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX()) * 57.29578F) - 90.0F;
      this.setYRot(yaw);
      this.yBodyRot = yaw;
      this.yHeadRot = yaw;
   }

   @Override
   public void tick() {
      super.tick();
      this.stateTicks++;
      if (this.hitTicks >= 0) {
         this.hitTicks++;
      }
      if (this.level().isClientSide()) {
         return;
      }
      ServerLevel level = (ServerLevel)this.level();
      if (!this.isAlive()) {
         this.release();
         return;
      }
      if (this.trapCooldown > 0) {
         this.trapCooldown--;
      }
      switch (this.getState()) {
         case DORMANT -> this.tickDormant(level);
         case EMERGE -> this.tickEmerge(level);
         case SPOT -> {
            this.freeze();
            this.face(this.quarry);
            if (this.stateTicks >= SPOT_TICKS) {
               this.setState(HUNT);
            }
         }
         case HUNT -> this.tickHunt(level);
         case ATTEMPT -> this.tickAttempt();
         case SUCCESS -> this.tickSuccess();
         case FAIL -> {
            this.freeze();
            if (this.stateTicks >= FAIL_TICKS) {
               this.trapCooldown = FAIL_COOLDOWN;
               this.setState(HUNT);
            }
         }
         case TRAPPED -> this.tickTrapped(level);
         case ESCAPE -> this.tickEscape();
         default -> this.tickPassive(level);
      }
   }

   /** Buried: wakes when a player comes near or a sensor goes off nearby. */
   private void tickDormant(ServerLevel level) {
      this.freeze();
      if (this.tickCount % 4 != 0) {
         return;
      }
      List<ServerPlayer> near = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(WAKE_RANGE), Odium::valid);
      if (!near.isEmpty() || (level.getNearestPlayer(this, 24.0) != null && this.sensorGoingOff(level, 10))) {
         this.setState(EMERGE);
      }
   }

   private void tickEmerge(ServerLevel level) {
      this.freeze();
      if (this.stateTicks % 3 == 0 && this.stateTicks < 110) {
         level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.SCULK.defaultBlockState()), this.getX(), this.getY() + 0.1, this.getZ(), 6, 0.5, 0.1, 0.5, 0.05);
      }
      if (this.stateTicks >= EMERGE_TICKS) {
         this.setState(PASSIVE);
      }
   }

   /** Slow wandering, and listening for a sensor. */
   private void tickPassive(ServerLevel level) {
      if (this.tickCount % 8 == 0) {
         ServerPlayer heard = this.listen(level);
         if (heard != null) {
            this.lockOn(heard);
            return;
         }
      }
      if (this.getNavigation().isDone() && this.random.nextInt(80) == 0) {
         Vec3 pos = LandRandomPos.getPos(this, 7, 3);
         if (pos != null) {
            this.getNavigation().moveTo(pos.x, pos.y, pos.z, PASSIVE_SPEED);
         }
      }
   }

   private void lockOn(ServerPlayer player) {
      this.quarry = player;
      this.noPathTicks = 0;
      this.freeze();
      this.setState(SPOT);
   }

   /** A sensor going off near it, or a noisy player right beside it: the nearest valid player within 40 blocks, or null. */
   private ServerPlayer listen(ServerLevel level) {
      List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(40.0), Odium::valid);
      if (players.isEmpty()) {
         return null;
      }
      ServerPlayer nearest = null;
      for (ServerPlayer p : players) {
         if (nearest == null || p.distanceToSqr(this) < nearest.distanceToSqr(this)) {
            nearest = p;
         }
         if (p.distanceToSqr(this) < HEAR_RANGE * HEAR_RANGE && !p.isShiftKeyDown() && !Silence.silenced(p)) {
            return p;
         }
      }
      return this.sensorGoingOff(level, SENSOR_RANGE) ? nearest : null;
   }

   private boolean sensorGoingOff(ServerLevel level, int range) {
      BlockPos centre = this.blockPosition();
      for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-range, -4, -range), centre.offset(range, 4, range))) {
         BlockState st = level.getBlockState(pos);
         if (st.hasProperty(BlockStateProperties.SCULK_SENSOR_PHASE) && st.getValue(BlockStateProperties.SCULK_SENSOR_PHASE).name().equals("ACTIVE")) {
            return true;
         }
      }
      return false;
   }

   private void tickHunt(ServerLevel level) {
      if (!valid(this.quarry) || this.quarry.level() != level || this.distanceTo(this.quarry) > GIVE_UP) {
         this.quarry = null;
         this.getNavigation().stop();
         this.setState(PASSIVE);
         return;
      }
      this.getLookControl().setLookAt(this.quarry, 40.0F, 40.0F);
      if (this.tickCount % 4 == 0) {
         this.getNavigation().moveTo(this.quarry, HUNT_SPEED);
      }
      double dx = this.quarry.getX() - this.getX();
      double dz = this.quarry.getZ() - this.getZ();
      double horizontal = Math.sqrt(dx * dx + dz * dz);
      // No way to reach them for 5 s: it loses interest.
      if (this.getNavigation().isDone() && horizontal > 4.0) {
         if (++this.noPathTicks > 100) {
            this.quarry = null;
            this.setState(PASSIVE);
            return;
         }
      } else {
         this.noPathTicks = 0;
      }
      if (this.trapCooldown <= 0 && horizontal <= ATTEMPT_RANGE && Math.abs(this.quarry.getY() - this.getY()) <= 1.6
         && this.hasLineOfSight(this.quarry) && !HELD.contains(this.quarry.getUUID())) {
         this.setState(ATTEMPT);
      }
   }

   private void tickAttempt() {
      this.face(this.quarry);
      if (this.stateTicks < LUNGE_AT) {
         this.freeze();
      } else if (this.stateTicks == LUNGE_AT && this.quarry != null) {
         // The spring.
         double dx = this.quarry.getX() - this.getX();
         double dz = this.quarry.getZ() - this.getZ();
         double len = Math.max(0.001, Math.sqrt(dx * dx + dz * dz));
         this.setDeltaMovement(dx / len * 0.95, 0.22, dz / len * 0.95);
      } else if (this.stateTicks >= DECIDE_AT) {
         this.freeze();
         boolean caught = valid(this.quarry) && !HELD.contains(this.quarry.getUUID())
            && Math.hypot(this.quarry.getX() - this.getX(), this.quarry.getZ() - this.getZ()) <= GRAB_RANGE
            && Math.abs(this.quarry.getY() - this.getY()) <= 1.8;
         this.setState(caught ? SUCCESS : FAIL);
      }
   }

   private void tickSuccess() {
      this.freeze();
      if (!valid(this.quarry)) {
         this.release();
         this.quarry = null;
         this.setState(PASSIVE);
         return;
      }
      this.face(this.quarry);
      if (this.stateTicks == CATCH_AT) {
         this.grab(this.quarry);
      }
      if (this.held != null) {
         this.hold(this.held);
      }
      if (this.stateTicks >= SUCCESS_TICKS) {
         this.escapeDamage = 0.0F;
         this.setState(TRAPPED);
      }
   }

   /** The catch: it steps right up to the player, and they are held. */
   private void grab(ServerPlayer player) {
      this.held = player;
      HELD.add(player.getUUID());
      Vec3 away = this.position().subtract(player.position());
      double len = Math.max(0.001, Math.sqrt(away.x * away.x + away.z * away.z));
      Vec3 old = this.position();
      float yaw = this.getYRot();
      this.snapTo(player.getX() + away.x / len * 0.9, player.getY(), player.getZ() + away.z / len * 0.9, yaw, 0.0F);
      if (!this.level().noCollision(this)) {
         this.snapTo(old.x, old.y, old.z, yaw, 0.0F);
      }
      this.face(player);
   }

   /** Held in place: heavy slowness and the player's own motion cancelled, so they cannot walk or jump away. */
   private void hold(ServerPlayer player) {
      if (this.tickCount % 4 == 0) {
         player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 12, 9, false, false));
      }
      player.setSprinting(false);
      player.setDeltaMovement(0.0, Math.min(0.0, player.getDeltaMovement().y), 0.0);
      player.hurtMarked = true;
   }

   private void release() {
      if (this.held != null) {
         HELD.remove(this.held.getUUID());
         this.held.removeEffect(MobEffects.SLOWNESS);
         this.held = null;
      }
   }

   private void tickTrapped(ServerLevel level) {
      this.freeze();
      if (!valid(this.held) || this.held.distanceTo(this) > 4.5) {
         this.release();
         this.quarry = null;
         this.setState(PASSIVE);
         return;
      }
      this.face(this.held);
      this.hold(this.held);
      if (this.stateTicks % 10 == 0) {
         level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 1.0, this.getZ(), 3, 0.5, 0.6, 0.5, 0.02);
      }
      for (int at : SUMMON_AT) {
         if (this.stateTicks == at) {
            this.summon(level, this.held);
         }
      }
      if (this.stateTicks >= TRAP_HOLD_TICKS) {
         this.setState(ESCAPE);
      }
   }

   private void tickEscape() {
      this.freeze();
      if (this.stateTicks == RELEASE_AT) {
         this.release();
         this.trapCooldown = TRAP_COOLDOWN;
      }
      if (this.stateTicks >= ESCAPE_TICKS) {
         this.release();
         this.setState(valid(this.quarry) ? HUNT : PASSIVE);
      }
   }

   /** Calls one Stagnated out of the ground a few blocks away (not when there are already plenty around). */
   private void summon(ServerLevel level, ServerPlayer player) {
      if (level.getEntitiesOfClass(Stagnated.class, player.getBoundingBox().inflate(64.0)).size() >= Tuning.STAGNATED_MAX_NEAR) {
         return;
      }
      for (int attempt = 0; attempt < 20; attempt++) {
         double angle = this.random.nextDouble() * Math.PI * 2.0;
         double dist = 4.0 + this.random.nextDouble() * 3.0;
         int x = (int)Math.floor(this.getX() + Math.cos(angle) * dist);
         int z = (int)Math.floor(this.getZ() + Math.sin(angle) * dist);
         for (int dy = 3; dy >= -3; dy--) {
            BlockPos pos = new BlockPos(x, (int)Math.floor(this.getY()) + dy, z);
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
               continue;
            }
            Stagnated mob = ModEntities.STAGNATED.create(level, EntitySpawnReason.EVENT);
            if (mob == null) {
               return;
            }
            mob.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.random.nextFloat() * 360.0F, 0.0F);
            if (!level.noCollision(mob)) {
               mob.discard();
               continue;
            }
            level.addFreshEntity(mob);
            mob.startEmerging();
            level.sendParticles(ParticleTypes.SCULK_SOUL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 0.4, 0.5, 0.4, 0.05);
            return;
         }
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
      int state = this.getState();
      if (state == DORMANT || state == EMERGE) {
         return false;
      }
      boolean hurt = super.hurtServer(level, source, amount);
      if (hurt && source.getEntity() instanceof ServerPlayer player && valid(player)) {
         if (state == TRAPPED && player == this.held) {
            this.escapeDamage += amount;
            if (this.escapeDamage >= ESCAPE_DAMAGE) {
               this.setState(ESCAPE);
            }
         } else if (state == PASSIVE) {
            this.lockOn(player);
         }
      }
      return hurt;
   }

   @Override
   public void die(DamageSource source) {
      this.release();
      super.die(source);
   }

   @Override
   public void remove(Entity.RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   @Override
   public int getAmbientSoundInterval() {
      return 160;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.getState() == PASSIVE ? ModSounds.ODIUM_IDLE[this.random.nextInt(ModSounds.ODIUM_IDLE.length)] : null;
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
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("OdiumState", this.getState() == DORMANT ? DORMANT : PASSIVE);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.entityData.set(STATE, input.getIntOr("OdiumState", PASSIVE) == DORMANT ? DORMANT : PASSIVE);
   }
}
