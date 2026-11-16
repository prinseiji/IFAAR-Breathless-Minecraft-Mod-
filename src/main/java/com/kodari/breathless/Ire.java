package com.kodari.breathless;

import java.util.List;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The Ire: a variant of the Odium that lies half buried to its waist and never moves. It waits (IDLE) until a player comes close, then springs (AMBUSH, the grab lands at 0.17 s).
 * A catch pulls you in (PULL): tap the jump key to fight the pull and break free. If it pulls you all the way in (TRAPPED) it holds you, floods you with spores
 * (your exposure fills even with a mask on), and the only way out is to damage it. A miss leaves it reaching (REACH): come near and it tries to trap you again (TRAP_ATTEMPT)
 * or attacks (ATTACK1 one swing, ATTACK2 two swings that add up to the same damage). It calls no reinforcements. Its fakes are IreHusk.
 */
public class Ire extends Monster implements StruggleHolder {
   public static final int IDLE = 0;
   public static final int AMBUSH = 1;
   public static final int PULL = 2;
   public static final int TRAPPED = 3;
   public static final int REACH = 4;
   public static final int TRAP_ATTEMPT = 5;
   public static final int ATTACK1 = 6;
   public static final int ATTACK2 = 7;
   public static final int ESCAPE = 8;
   /** Not a real state: the renderer uses it for the fake (IreHusk). */
   public static final int HUSK = 9;

   private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Ire.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HELD_ID = SynchedEntityData.defineId(Ire.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> STRUGGLE = SynchedEntityData.defineId(Ire.class, EntityDataSerializers.FLOAT);

   // Numbers to tune. Times are in ticks (20 per second) and match the clip lengths.
   static final double HEALTH = 30.0;
   /** It springs when a player comes this close (closer if they are sneaking or silenced). */
   static final double TRIGGER_RANGE = 4.5;
   static final double QUIET_TRIGGER_RANGE = 3.0;
   /** Ambush clip (1 s): the grab lands at 0.17 s. It catches you if you are within GRAB_RANGE then. */
   static final int AMBUSH_TICKS = 20;
   static final int GRAB_AT = 4;
   static final double GRAB_RANGE = 3.4;
   /** Pulling: blocks per tick (slower the further the bar is filled), the bar per jump tap, the drain per tick, and how far you can get before it lets go. */
   static final double PULL_SPEED = 0.09;
   static final float PRESS = 0.13F;
   static final float DRAIN = 0.008F;
   static final double PULL_BREAK_RANGE = 8.0;
   /** Pulled in to this close, you are trapped. */
   static final double TRAP_DISTANCE = 1.4;
   /** Trapped: exposure added every tick (100 in about 5.5 s) and the damage to the Ire that breaks you free. */
   static final float EXPOSURE_PER_TICK = 0.9F;
   static final float ESCAPE_DAMAGE = 12.0F;
   /** Reaching: how close a player must be for it to act, ticks between actions, and how long with nobody around before it settles back to idle. */
   static final double REACH_RANGE = 4.0;
   static final int ACTION_COOLDOWN = 30;
   static final int SETTLE_TICKS = 160;
   /** Trap attempt clip (0.54 s): the grab lands at 0.25 s. */
   static final int ATTEMPT_TICKS = 11;
   static final int ATTEMPT_CATCH_AT = 5;
   static final double ATTEMPT_RANGE = 3.2;
   /** Attacks: the clips are 0.63 s (one swing at 0.33 s) and 0.83 s (swings at 0.33 s and 0.62 s). Both deal ATTACK_DAMAGE in total. */
   static final int ATTACK1_TICKS = 13;
   static final int ATTACK1_HIT_AT = 7;
   static final int ATTACK2_TICKS = 17;
   static final int ATTACK2_HIT_AT = 7;
   static final int ATTACK2_HIT2_AT = 12;
   static final float ATTACK_DAMAGE = 6.0F;
   static final double ATTACK_RANGE = 3.2;
   /** Escape clip is 0.5 s: you are let go at 0.2 s. */
   static final int ESCAPE_TICKS = 10;
   static final int RELEASE_AT = 4;

   private int stateTicks;
   private int hitTicks = -1;
   private int cooldown;
   private int alone;
   private float progress;
   private float damageTaken;
   private ServerPlayer quarry;
   private ServerPlayer held;

   public Ire(EntityType<? extends Ire> type, Level level) {
      super(type, level);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, HEALTH)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STATE, IDLE);
      builder.define(HELD_ID, -1);
      builder.define(STRUGGLE, 0.0F);
   }

   public int getState() {
      return this.entityData.get(STATE);
   }

   @Override
   public int getHeldId() {
      return this.entityData.get(HELD_ID);
   }

   @Override
   public float getStruggle() {
      return this.entityData.get(STRUGGLE);
   }

   @Override
   public boolean tapsCount() {
      return this.getState() == PULL;
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

   /** A tap of the jump key from a player being pulled in (sent by the client). */
   public static void struggle(ServerPlayer player) {
      if (!(player.level() instanceof ServerLevel level)) {
         return;
      }
      for (Ire ire : level.getEntitiesOfClass(Ire.class, player.getBoundingBox().inflate(10.0))) {
         if (ire.held == player && ire.getState() == PULL) {
            ire.progress = Math.min(1.0F, ire.progress + PRESS);
            ire.entityData.set(STRUGGLE, ire.progress);
            if (ire.progress >= 1.0F) {
               ire.breakFree();
            }
         }
      }
   }

   private void setState(int state) {
      this.stateTicks = 0;
      this.entityData.set(STATE, state);
      if (this.level() instanceof ServerLevel level) {
         switch (state) {
            case AMBUSH -> this.play(level, ModSounds.IRE_AMBUSH, 2.5F);
            case PULL -> this.play(level, ModSounds.IRE_PULLING, 2.5F);
            case TRAPPED -> {
               this.stopSounds(level, "ire_pulling");
               this.play(level, ModSounds.IRE_TRAPPED, 2.5F);
            }
            case REACH -> this.play(level, ModSounds.IRE_REACHING, 2.0F);
            case TRAP_ATTEMPT -> this.play(level, ModSounds.IRE_TRAP_ATTEMPT, 2.5F);
            case ATTACK1 -> this.play(level, ModSounds.IRE_ATTACK[0], 2.5F);
            case ATTACK2 -> this.play(level, ModSounds.IRE_ATTACK[1], 2.5F);
            case ESCAPE -> {
               this.stopSounds(level, "ire_trapped", "ire_pulling");
               this.play(level, ModSounds.IRE_ESCAPE, 2.5F);
            }
            default -> {
            }
         }
      }
   }

   private void play(ServerLevel level, SoundEvent sound, float volume) {
      level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, volume, 1.0F);
   }

   private void stopSounds(ServerLevel level, String... names) {
      for (ServerPlayer player : level.players()) {
         for (String name : names) {
            player.connection.send(new ClientboundStopSoundPacket(Breathless.id(name), SoundSource.HOSTILE));
         }
      }
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

   private double horizontal(ServerPlayer p) {
      return Math.hypot(p.getX() - this.getX(), p.getZ() - this.getZ());
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
      if (this.cooldown > 0) {
         this.cooldown--;
      }
      switch (this.getState()) {
         case IDLE -> this.tickIdle(level);
         case AMBUSH -> this.tickAmbush();
         case PULL -> this.tickPull();
         case TRAPPED -> this.tickTrapped(level);
         case REACH -> this.tickReach(level);
         case TRAP_ATTEMPT -> this.tickAttempt();
         case ATTACK1 -> this.tickAttack(ATTACK1_TICKS, ATTACK1_HIT_AT, -1, ATTACK_DAMAGE);
         case ATTACK2 -> this.tickAttack(ATTACK2_TICKS, ATTACK2_HIT_AT, ATTACK2_HIT2_AT, ATTACK_DAMAGE / 2.0F);
         case ESCAPE -> this.tickEscape();
         default -> {
         }
      }
   }

   /** Lying in wait: springs when a player comes close. */
   private void tickIdle(ServerLevel level) {
      if (this.tickCount % 4 != 0) {
         return;
      }
      List<ServerPlayer> near = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(TRIGGER_RANGE), Odium::valid);
      for (ServerPlayer p : near) {
         boolean quiet = p.isShiftKeyDown() || Silence.silenced(p);
         if (this.distanceToSqr(p) <= (quiet ? QUIET_TRIGGER_RANGE : TRIGGER_RANGE) * (quiet ? QUIET_TRIGGER_RANGE : TRIGGER_RANGE)) {
            this.quarry = p;
            this.setState(AMBUSH);
            return;
         }
      }
   }

   private void tickAmbush() {
      if (this.stateTicks <= GRAB_AT) {
         this.face(this.quarry);
      }
      if (this.stateTicks == GRAB_AT) {
         if (Odium.valid(this.quarry) && !Odium.HELD.contains(this.quarry.getUUID()) && this.horizontal(this.quarry) <= GRAB_RANGE && Math.abs(this.quarry.getY() - this.getY()) <= 2.0) {
            this.grab(this.quarry);
            return;
         }
      }
      if (this.stateTicks >= AMBUSH_TICKS) {
         this.cooldown = ACTION_COOLDOWN;
         this.setState(REACH);
      }
   }

   private void grab(ServerPlayer player) {
      this.held = player;
      this.quarry = player;
      Odium.HELD.add(player.getUUID());
      this.progress = 0.0F;
      this.damageTaken = 0.0F;
      this.entityData.set(HELD_ID, player.getId());
      this.entityData.set(STRUGGLE, 0.0F);
      this.setState(PULL);
   }

   /** Being pulled in. Tapping jump fights it (the bar); the pull is slower the further the bar is filled. */
   private void tickPull() {
      if (!Odium.valid(this.held) || this.horizontal(this.held) > PULL_BREAK_RANGE) {
         this.breakFree();
         return;
      }
      this.face(this.held);
      this.progress = Math.max(0.0F, this.progress - DRAIN);
      this.entityData.set(STRUGGLE, this.progress);
      double dist = this.horizontal(this.held);
      if (dist <= TRAP_DISTANCE) {
         this.damageTaken = 0.0F;
         this.progress = 0.0F;
         this.entityData.set(STRUGGLE, 0.0F);
         this.setState(TRAPPED);
         return;
      }
      double speed = PULL_SPEED * (1.0 - 0.5 * this.progress);
      double dx = (this.getX() - this.held.getX()) / dist;
      double dz = (this.getZ() - this.held.getZ()) / dist;
      if (this.tickCount % 4 == 0) {
         this.held.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 12, 3, false, false));
      }
      this.held.setDeltaMovement(dx * speed * 3.0, this.held.onGround() ? 0.0 : this.held.getDeltaMovement().y, dz * speed * 3.0);
      this.held.hurtMarked = true;
   }

   /** Held fast: no way to walk off, spores flood you whatever you wear, and the bar is the damage you do to it. */
   private void tickTrapped(ServerLevel level) {
      if (!Odium.valid(this.held)) {
         this.release();
         this.setState(REACH);
         return;
      }
      this.face(this.held);
      if (this.tickCount % 4 == 0) {
         this.held.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 12, 9, false, false));
      }
      this.held.setSprinting(false);
      this.held.setDeltaMovement(0.0, Math.min(0.0, this.held.getDeltaMovement().y), 0.0);
      this.held.hurtMarked = true;
      // The exposure fills even with a mask on.
      Hazard.addExposure(this.held, EXPOSURE_PER_TICK);
      if (this.stateTicks % 2 == 0) {
         level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 0.8, this.getZ(), 4, 0.6, 0.6, 0.6, 0.06);
         level.sendParticles(ModParticles.SPORE_EYE, this.held.getX(), this.held.getY() + 1.0, this.held.getZ(), 2, 0.5, 0.6, 0.5, 0.03);
      }
   }

   /** Out of the pull (the bar filled, or you got too far): it goes back to reaching. */
   private void breakFree() {
      this.release();
      this.cooldown = ACTION_COOLDOWN + 20;
      this.setState(REACH);
   }

   private void release() {
      if (this.held != null) {
         Odium.HELD.remove(this.held.getUUID());
         this.held.removeEffect(MobEffects.SLOWNESS);
         this.held = null;
         this.entityData.set(HELD_ID, -1);
         this.entityData.set(STRUGGLE, 0.0F);
      }
      if (this.level() instanceof ServerLevel level) {
         this.stopSounds(level, "ire_pulling", "ire_trapped");
      }
   }

   private void tickReach(ServerLevel level) {
      if (this.tickCount % 4 != 0) {
         return;
      }
      List<ServerPlayer> near = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(14.0), Odium::valid);
      ServerPlayer nearest = null;
      for (ServerPlayer p : near) {
         if (nearest == null || p.distanceToSqr(this) < nearest.distanceToSqr(this)) {
            nearest = p;
         }
      }
      if (nearest == null) {
         this.alone += 4;
         if (this.alone >= SETTLE_TICKS) {
            this.alone = 0;
            this.quarry = null;
            this.setState(IDLE);
         }
         return;
      }
      this.alone = 0;
      this.quarry = nearest;
      this.face(nearest);
      if (this.cooldown <= 0 && this.horizontal(nearest) <= REACH_RANGE && Math.abs(nearest.getY() - this.getY()) <= 2.0) {
         float roll = this.random.nextFloat();
         this.setState(roll < 0.4F ? TRAP_ATTEMPT : (roll < 0.7F ? ATTACK1 : ATTACK2));
      }
   }

   private void tickAttempt() {
      if (this.stateTicks <= ATTEMPT_CATCH_AT) {
         this.face(this.quarry);
      }
      if (this.stateTicks == ATTEMPT_CATCH_AT && Odium.valid(this.quarry) && !Odium.HELD.contains(this.quarry.getUUID())
         && this.horizontal(this.quarry) <= ATTEMPT_RANGE && Math.abs(this.quarry.getY() - this.getY()) <= 2.0) {
         this.grab(this.quarry);
         return;
      }
      if (this.stateTicks >= ATTEMPT_TICKS) {
         this.cooldown = ACTION_COOLDOWN + 10;
         this.setState(REACH);
      }
   }

   /** One swing (hit2 is -1) or two. Each landed swing deals the damage given; two swings of half add up to one swing of the whole. */
   private void tickAttack(int total, int hit1, int hit2, float damage) {
      if (this.stateTicks < hit1) {
         this.face(this.quarry);
      }
      if (this.stateTicks == hit1 || (hit2 > 0 && this.stateTicks == hit2)) {
         this.swing(damage);
      }
      if (this.stateTicks >= total) {
         this.cooldown = ACTION_COOLDOWN;
         this.setState(REACH);
      }
   }

   private void swing(float damage) {
      if (this.quarry != null && Odium.valid(this.quarry) && this.horizontal(this.quarry) <= ATTACK_RANGE && Math.abs(this.quarry.getY() - this.getY()) <= 2.0
         && this.level() instanceof ServerLevel level) {
         this.quarry.hurtServer(level, level.damageSources().mobAttack(this), damage);
      }
   }

   private void tickEscape() {
      if (this.stateTicks == RELEASE_AT) {
         this.release();
         this.cooldown = ACTION_COOLDOWN + 20;
      }
      if (this.stateTicks >= ESCAPE_TICKS) {
         this.setState(REACH);
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
      int state = this.getState();
      boolean hurt = super.hurtServer(level, source, amount);
      if (hurt && source.getEntity() instanceof ServerPlayer player && Odium.valid(player)) {
         if (state == TRAPPED && player == this.held) {
            // Damage is the only way out of a trap.
            this.damageTaken += amount;
            this.progress = Math.min(1.0F, this.damageTaken / ESCAPE_DAMAGE);
            this.entityData.set(STRUGGLE, this.progress);
            if (this.progress >= 1.0F) {
               this.setState(ESCAPE);
            }
         } else if (state == IDLE && this.horizontal(player) <= 8.0) {
            this.quarry = player;
            this.setState(AMBUSH);
         }
      }
      return hurt;
   }

   @Override
   public void die(DamageSource source) {
      this.release();
      if (this.level() instanceof ServerLevel level) {
         this.stopSounds(level, "ire_reaching", "ire_trap_attempt", "ire_attack_1", "ire_attack_2");
      }
      super.die(source);
   }

   @Override
   public void remove(Entity.RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public void push(double x, double y, double z) {
   }

   @Override
   protected void doPush(Entity entity) {
   }

   @Override
   public int getAmbientSoundInterval() {
      return 160;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.getState() == IDLE ? ModSounds.IRE_IDLE : null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.IRE_HIT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return ModSounds.STAGNATED_DEATH;
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      // Anything it was doing is forgotten on loading: back to lying in wait.
      this.entityData.set(STATE, IDLE);
   }
}
