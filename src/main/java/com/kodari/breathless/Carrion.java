package com.kodari.breathless;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/**
 * The Carrion: a stationary sculk growth that sits on patches of sculk. It swells (the death clip, 2.9 s) and bursts when a player walks or runs near it (sneaking is silent),
 * when a sculk sensor nearby goes off, when a player gets too close, or when anything hurts it (so an arrow from far away sets it off far away).
 * The burst does not break blocks: it turns the natural blocks around it into sculk (GasClouds) and leaves a gas cloud that wears filters down fast for a while.
 * Fire puts the gas out: a Carrion that is burning when it bursts leaves only a thin short cloud, and burning things or fire blocks inside a cloud burn it away quickly.
 */
public class Carrion extends Mob {
   /** Swell length in ticks (the death clip is 2.875 s) and the tick the explode sound starts so its boom (1.8 s in) lands on the burst. */
   static final int SWELL_TICKS = 58;
   static final int SOUND_AT = 22;
   /** How far a walking or running player is "heard", and how close a sneaking player can get before it goes off anyway. */
   static final double HEAR_RANGE = 7.0;
   static final double CLOSE_RANGE = 2.2;
   /** A sculk sensor that is going off within this many blocks sets it off too. */
   static final int SENSOR_RANGE = 6;
   /** The entity event that starts the swell on the client. */
   static final byte EVENT_SWELL = 120;

   /** Ticks into the swell, or -1. Counted on both sides. */
   private int swell = -1;
   private final Map<UUID, Vec3> lastSeen = new HashMap<>();

   public Carrion(EntityType<? extends Carrion> type, Level level) {
      super(type, level);
      this.setNoGravity(true);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   /** Seconds into the swell (with the partial tick), or -1. Used by the renderer. */
   public float swellSeconds(float partialTick) {
      return this.swell < 0 ? -1.0F : (this.swell + partialTick) / 20.0F;
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
   public void handleEntityEvent(byte id) {
      if (id == EVENT_SWELL) {
         this.swell = 0;
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.swell >= 0) {
         this.swell++;
      }
      if (this.level().isClientSide() || !this.isAlive()) {
         return;
      }
      ServerLevel level = (ServerLevel)this.level();
      if (this.swell < 0) {
         if (this.tickCount % 4 == 0 && this.shouldGoOff(level)) {
            this.startSwell();
         }
         return;
      }
      if (this.swell == SOUND_AT) {
         level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.CARRION_EXPLODE, SoundSource.HOSTILE, 3.0F, 1.0F);
      }
      if (this.swell >= SWELL_TICKS) {
         this.burst(level);
      }
   }

   /** Anything hurting it sets it off, but it takes almost no damage, so it cannot die before the burst. */
   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
      this.startSwell();
      return super.hurtServer(level, source, Math.min(amount, 0.5F));
   }

   private void startSwell() {
      if (this.swell >= 0 || this.level().isClientSide()) {
         return;
      }
      this.swell = 0;
      this.level().broadcastEntityEvent(this, EVENT_SWELL);
   }

   /** Noise from a player that is not sneaking, a player too close, or a sculk sensor going off. */
   private boolean shouldGoOff(ServerLevel level) {
      List<ServerPlayer> near = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(HEAR_RANGE));
      Map<UUID, Vec3> now = new HashMap<>();
      boolean go = false;
      for (ServerPlayer player : near) {
         if (player.isCreative() || player.isSpectator() || !player.isAlive()) {
            continue;
         }
         now.put(player.getUUID(), player.position());
         if (player.distanceToSqr(this) < CLOSE_RANGE * CLOSE_RANGE) {
            go = true;
            continue;
         }
         Vec3 before = this.lastSeen.get(player.getUUID());
         boolean moved = before != null && before.distanceToSqr(player.position()) > 0.04;
         boolean airborne = !player.onGround() && !player.isFallFlying();
         if (!player.isShiftKeyDown() && (moved || airborne)) {
            go = true;
         }
      }
      this.lastSeen.clear();
      this.lastSeen.putAll(now);
      if (go) {
         return true;
      }
      // A sensor going off (only looked for when a player is around, every 8 ticks).
      if (!near.isEmpty() || level.getNearestPlayer(this, 24.0) != null) {
         if (this.tickCount % 8 == 0) {
            BlockPos centre = this.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-SENSOR_RANGE, -3, -SENSOR_RANGE), centre.offset(SENSOR_RANGE, 3, SENSOR_RANGE))) {
               BlockState st = level.getBlockState(pos);
               if (st.hasProperty(BlockStateProperties.SCULK_SENSOR_PHASE) && st.getValue(BlockStateProperties.SCULK_SENSOR_PHASE).name().equals("ACTIVE")) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private void burst(ServerLevel level) {
      boolean burnt = this.isOnFire();
      GasClouds.start(level, this.position().add(0.0, 0.8, 0.0), burnt);
      this.remove(Entity.RemovalReason.KILLED);
   }

   @Override
   public int getAmbientSoundInterval() {
      return 200;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.swell >= 0 ? null : ModSounds.CARRION_IDLE[this.random.nextInt(ModSounds.CARRION_IDLE.length)];
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
