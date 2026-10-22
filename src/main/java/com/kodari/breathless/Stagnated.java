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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

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
   /** What this one does when it meets something: 0 none, 1 soldier (armor), 2 excavator (breaks and builds). */
   private int role;
   /** Client-side: ticks into the armoring animation, -1 when not armoring. */
   public int armoringClock = -1;
   /** Server-side: ticks left of the armoring animation. */
   private int armoringTicks;
   /** Excavator work in progress: 0 none, 1 breaking a block, 2 placing a bridge / step, 3 pillaring up. */
   private int action;
   private BlockPos actionPos;
   private int actionTicks;
   private int placed;
   /** Can it walk to its target? Refreshed every 5 ticks. Also how long it has been standing still while chasing. */
   private boolean unreachable;
   private int stuckTicks;
   private Vec3 lastPos = Vec3.ZERO;
   /** Client-side clocks for the looping mining / placing animations, -1 when not doing it. */
   public int mineClock = -1;
   public int placeClock = -1;
   /** Where an active sculk sensor made it curious, and when it should start walking there. */
   private BlockPos investigate;
   private int investigateAt;
   private int heardCooldown;
   /** Server-side bookkeeping for the advancements and for giving up a chase. */
   private boolean armoredFlag;
   private int pillared;
   private ServerPlayer chased;
   private int chaseTicks;
   private int lostTicks;
   private boolean chaseSprinted;
   /** The way it was bridging, kept so a diagonal bridge does not flip direction every block. */
   private Direction heading;
   /** Soldier weapon: 0 none, 1 iron sword, 2 stone axe. Equipped in the main hand when it spots a target. */
   private int weapon;
   /** Soldier weapon handling (server): drawing it during the armed spot pose, stashing it after losing the target. */
   private boolean drawPending;
   private int unarmTicks;
   private boolean hadTarget;
   /** Server-side: ticks left of the axe's shield-break move. Client-side clock for the animation, -1 when not doing it. */
   private int shieldTicks;
   public int shieldClock = -1;
   /** Client-side clocks for the armed spot pose and the unarm, -1 when not doing it. */
   public int spotArmedClock = -1;
   public int unarmClock = -1;
   /** The rare diamond-armored mace variant ("sweaty"): wind-charge launch, mace smash, water-bucket clutch. */
   private boolean sweaty;
   private boolean geared;
   private int windTicks;
   /** Sweaty attack cycle: sword fight, swap to the mace, launch + smash/clutch, swap back. */
   private int swordTicks;
   private int swordHits;
   private int swapTicks;
   private boolean swapToMace;
   private int maceStuck;
   private boolean decided;
   private boolean diving;
   private boolean smashing;
   private boolean smashAnim;
   private boolean landed;
   private float landedFall;
   private int airTicks;
   private int clutchTicks;
   private BlockPos clutchPos;
   private boolean clutchPlaced;
   /** Client-side clocks for the sweaty moves (-1 when not doing it) and whether it is in its airborne pose. */
   public int windClock = -1;
   public int smashClock = -1;
   public int clutchClock = -1;
   public boolean flying;
   private int flyTicks;

   public Stagnated(EntityType<? extends Stagnated> type, Level level) {
      super(type, level);
      this.role = level.getRandom().nextInt(2) + 1;
      float roll = level.getRandom().nextFloat();
      this.weapon = roll < Tuning.STAGNATED_SWORD_CHANCE ? 1 : (roll < Tuning.STAGNATED_SWORD_CHANCE + Tuning.STAGNATED_AXE_CHANCE ? 2 : 0);
      // About 1 in 150 soldiers is the sweaty one (saved data overrides this when a world loads).
      if (!level.isClientSide() && this.role == 1 && level.getRandom().nextFloat() < Tuning.SWEATY_CHANCE) {
         this.sweaty = true;
         this.weapon = 0;
      }
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
      this.goalSelector.addGoal(4, new ApproachGoal());
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
         if (this.unarmTicks > 0) {
            this.unarmTicks = 0;
            this.level().broadcastEntityEvent(this, (byte)73);
         }
         // A soldier with a weapon uses the longer spot pose and draws the weapon in the middle of it.
         boolean drawing = this.role == 1 && this.weapon != 0 && this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         this.drawPending = drawing;
         this.spotTicks = drawing ? Tuning.STAGNATED_SPOT_ARMED_TICKS : Tuning.STAGNATED_SPOT_TICKS;
         this.level().broadcastEntityEvent(this, drawing ? (byte)75 : (byte)61);
         this.playSound(ModSounds.STAGNATED_SPOT, 1.2F, 1.0F);
         this.placed = 0;
         this.heading = null;
         if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && this.role == 2) {
            // Held in the left hand (off-hand, so a tool adds no attack damage). It swaps between pickaxe and sculk as it works.
            this.hold(Items.IRON_PICKAXE);
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
         }
         this.getNavigation().stop();
         if (target instanceof ServerPlayer player) {
            ServerPlayNetworking.send(player, new SpotPayload(0.0F));
            Achievements.award(player, "not_alone");
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
      boolean hurt = super.hurtServer(level, source, amount);
      // A soldier puts its iron on the moment anything hits it (a player or a mob).
      if (hurt && this.role == 1 && this.armoringTicks <= 0 && this.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && source.getEntity() instanceof LivingEntity && this.isAlive()) {
         this.armoringTicks = Tuning.STAGNATED_ARMOR_TICKS;
         this.level().broadcastEntityEvent(this, (byte)70);
      }
      return hurt;
   }

   /** True once it has put its iron on (used by the Bravery advancement). */
   public boolean isArmored() {
      return this.armoredFlag || !this.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
   }

   /**
    * Follows the player it is chasing. If it cannot sense that player any more for a few seconds it gives up,
    * and a player who sprinted away gets "Run Rabbit, Run Rabbit".
    */
   private void trackChase() {
      LivingEntity target = this.getTarget();
      if (target instanceof ServerPlayer player && player.isAlive() && !player.isCreative() && !player.isSpectator()) {
         if (this.chased != player) {
            this.chased = player;
            this.chaseTicks = 0;
            this.chaseSprinted = false;
         }
         this.chaseTicks++;
         if (player.isSprinting()) {
            this.chaseSprinted = true;
         }
         if (this.spotTicks <= 0 && this.action == 0 && !this.canSense(player)) {
            if (++this.lostTicks >= Tuning.STAGNATED_GIVE_UP_TICKS) {
               if (this.chaseSprinted && this.chaseTicks >= 100) {
                  Achievements.award(player, "run_rabbit");
               }
               this.setTarget(null);
               this.getNavigation().stop();
               this.lostTicks = 0;
               this.chased = null;
            }
         } else {
            this.lostTicks = 0;
         }
      } else {
         this.chased = null;
         this.chaseTicks = 0;
         this.lostTicks = 0;
         this.chaseSprinted = false;
      }
   }

   private void putOnArmor() {
      this.armoredFlag = true;
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
      for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         this.setDropChance(slot, 0.0F);
      }
      this.playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0F, 1.0F);
      this.refreshStats();
   }

   private void equipWeapon() {
      if (this.weapon == 0) {
         return;
      }
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.weapon == 2 ? Items.STONE_AXE : Items.IRON_SWORD));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      this.refreshStats();
   }

   /**
    * Damage and speed follow what it is carrying: a held weapon lowers the base damage (the weapon adds its own),
    * the armor slows it down, and the axe slows it down a little more.
    */
   private void refreshStats() {
      if (this.level().isClientSide()) {
         return;
      }
      ItemStack main = this.getItemBySlot(EquipmentSlot.MAINHAND);
      boolean armed = main.is(Items.IRON_SWORD) || main.is(Items.STONE_AXE) || main.is(Items.MACE) || main.is(Items.DIAMOND_SWORD);
      AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
         damage.setBaseValue(armed ? Tuning.STAGNATED_ARMED_BASE_DAMAGE : Tuning.STAGNATED_DAMAGE);
      }
      AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         slow(speed, "stagnated_armor_slow", this.isArmored() && !this.sweaty ? Tuning.STAGNATED_ARMOR_SLOW : 0.0);
         slow(speed, "stagnated_axe_slow", main.is(Items.STONE_AXE) ? Tuning.STAGNATED_AXE_SLOW : 0.0);
      }
   }

   private static void slow(AttributeInstance attribute, String name, double amount) {
      Identifier id = Breathless.id(name);
      if (amount <= 0.0) {
         attribute.removeModifier(id);
      } else {
         attribute.addOrUpdateTransientModifier(new AttributeModifier(id, -amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("StagnatedRole", this.role);
      output.putInt("StagnatedWeapon", this.weapon);
      output.putBoolean("Sweaty", this.sweaty);
      output.putBoolean("SweatyGeared", this.geared);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      int saved = input.getIntOr("StagnatedRole", 0);
      // The old builder role (3) became part of the excavator.
      this.role = saved == 3 ? 2 : saved;
      this.weapon = input.getIntOr("StagnatedWeapon", 0);
      this.sweaty = input.getBooleanOr("Sweaty", false);
      this.geared = input.getBooleanOr("SweatyGeared", false);
      this.refreshStats();
   }

   @Override
   protected boolean isImmobile() {
      return super.isImmobile() || this.spotTicks > 0 || this.emergeTicks > 0 || this.armoringTicks > 0 || this.shieldTicks > 0 || this.unarmTicks > 0 || this.action != 0 || this.windTicks > 0 || this.diving || this.swapTicks > 0;
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
         if (this.armoringClock >= 0 && ++this.armoringClock > Tuning.STAGNATED_ARMOR_TICKS) {
            this.armoringClock = -1;
         }
         if (this.mineClock >= 0) {
            this.mineClock++;
         }
         if (this.placeClock >= 0) {
            this.placeClock++;
         }
         if (this.spotClock >= 0 && ++this.spotClock > Tuning.STAGNATED_SPOT_TICKS) {
            this.spotClock = -1;
         }
         if (this.shieldClock >= 0 && ++this.shieldClock > Tuning.STAGNATED_SHIELD_TICKS) {
            this.shieldClock = -1;
         }
         if (this.spotArmedClock >= 0 && ++this.spotArmedClock > Tuning.STAGNATED_SPOT_ARMED_TICKS) {
            this.spotArmedClock = -1;
         }
         if (this.unarmClock >= 0 && ++this.unarmClock > Tuning.STAGNATED_UNARM_TICKS) {
            this.unarmClock = -1;
         }
         if (this.windClock >= 0 && ++this.windClock > Tuning.SWEATY_WIND_TICKS) {
            this.windClock = -1;
         }
         if (this.smashClock >= 0 && ++this.smashClock > Tuning.SWEATY_SMASH_TICKS) {
            this.smashClock = -1;
         }
         if (this.clutchClock >= 0 && ++this.clutchClock > Tuning.SWEATY_CLUTCH_TICKS) {
            this.clutchClock = -1;
         }
         if (this.flying && ++this.flyTicks > 8 && this.onGround()) {
            this.flying = false;
         }
      } else if (this.emergeTicks > 0) {
         this.emergeTicks--;
      } else {
         if (this.sweaty) {
            this.tickSweaty((ServerLevel)this.level());
         }
         if (this.armoringTicks > 0) {
            this.armoringTicks--;
            if (Tuning.STAGNATED_ARMOR_TICKS - this.armoringTicks == Tuning.STAGNATED_ARMOR_EQUIP_AT) {
               this.putOnArmor();
            }
         }
         if (this.action != 0) {
            this.tickAction((ServerLevel)this.level());
         } else if (this.tickCount % 5 == 0) {
            this.refreshReach();
            this.unstick();
            if (this.role == 2) {
               this.considerAction((ServerLevel)this.level());
            }
         }
         this.trackChase();
         if (this.shieldTicks > 0) {
            this.shieldTicks--;
            this.faceTarget();
            if (Tuning.STAGNATED_SHIELD_TICKS - this.shieldTicks == Tuning.STAGNATED_SHIELD_STRIKE_AT) {
               this.shieldStrike();
            }
         }
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
         if (this.drawPending && Tuning.STAGNATED_SPOT_ARMED_TICKS - this.spotTicks >= Tuning.STAGNATED_DRAW_AT) {
            this.drawPending = false;
            this.equipWeapon();
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
         }
         // Lost or finished with its target: it puts the weapon away.
         boolean hasTarget = this.getTarget() != null;
         if (this.hadTarget && !hasTarget && this.role == 1 && !this.sweaty && this.unarmTicks <= 0 && !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.unarmTicks = Tuning.STAGNATED_UNARM_TICKS;
            this.getNavigation().stop();
            this.level().broadcastEntityEvent(this, (byte)76);
         }
         this.hadTarget = hasTarget;
         if (this.unarmTicks > 0) {
            this.unarmTicks--;
            if (Tuning.STAGNATED_UNARM_TICKS - this.unarmTicks == Tuning.STAGNATED_UNARM_AT) {
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.8F, 0.8F);
               this.refreshStats();
            }
         }
      }
   }


   // ---------------------------------------------------------------- the sweaty variant

   /** For testing (/breathless sweaty): turns it into the sweaty one, it puts its gear on next tick. */
   public void makeSweaty() {
      this.role = 1;
      this.weapon = 0;
      this.sweaty = true;
      this.geared = false;
   }

   private void gear() {
      this.geared = true;
      this.armoredFlag = true;
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         this.setDropChance(slot, 0.0F);
      }
      this.setCustomName(Component.literal("xX_5L0TH_Xx"));
      this.refreshStats();
   }

   /** Items it holds for a moment (wind charge, water bucket) go in the off hand, which the renderer shows in the left hand instead of the mace. */
   private void holdTemp(net.minecraft.world.item.Item item) {
      this.setItemSlot(EquipmentSlot.OFFHAND, item == null ? ItemStack.EMPTY : new ItemStack(item));
      this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }

   @Override
   public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
      if (this.sweaty) {
         this.landed = true;
         this.landedFall = (float)distance;
         return false;
      }
      return super.causeFallDamage(distance, multiplier, source);
   }

   private boolean roomAbove() {
      BlockPos base = this.blockPosition();
      for (int i = 1; i <= 7; i++) {
         if (!this.level().getBlockState(base.above(i)).getCollisionShape(this.level(), base.above(i)).isEmpty()) {
            return false;
         }
      }
      return true;
   }

   /** Distance from its feet to the top of the first solid block below, or -1 if nothing within 12 blocks. Sets clutchPos to the air block above it. */
   private double groundBelow() {
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
      for (int i = 0; i < 12; i++) {
         if (solid((ServerLevel)this.level(), pos)) {
            this.clutchPos = pos.above().immutable();
            return this.getY() - (pos.getY() + 1.0);
         }
         pos.move(0, -1, 0);
      }
      return -1.0;
   }

   private void tickSweaty(ServerLevel level) {
      if (!this.geared) {
         this.gear();
      }
      LivingEntity target = this.getTarget();
      boolean mace = this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.MACE);

      // The swap between the sword and the mace (reuses the unarm animation: the item changes at tick 7).
      if (this.swapTicks > 0) {
         this.swapTicks--;
         this.faceTarget();
         if (Tuning.STAGNATED_UNARM_TICKS - this.swapTicks == Tuning.STAGNATED_UNARM_AT) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.swapToMace ? Items.MACE : Items.DIAMOND_SWORD));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.8F, 1.0F);
            this.refreshStats();
            this.swordTicks = 0;
            this.swordHits = 0;
            this.maceStuck = 0;
         }
      } else if (!this.diving && this.windTicks <= 0 && target != null && this.onGround() && this.spotTicks <= 0 && this.armoringTicks <= 0 && this.action == 0) {
         if (!mace) {
            // Sword phase: fight for a while, then go for the mace.
            if (++this.swordTicks >= Tuning.SWEATY_SWORD_TICKS || this.swordHits >= Tuning.SWEATY_SWORD_HITS) {
               this.startSwap(true);
            }
         } else {
            // Mace phase: launch right away (from any distance within reach). If it cannot (low ceiling, too far), go back to the sword.
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist <= Tuning.SWEATY_RANGE_MAX && Math.abs(target.getY() - this.getY()) < 6.0 && this.roomAbove()) {
               this.windTicks = Tuning.SWEATY_WIND_TICKS;
               this.getNavigation().stop();
               this.holdTemp(Items.WIND_CHARGE);
               this.level().broadcastEntityEvent(this, (byte)77);
            } else if (++this.maceStuck >= 40) {
               this.startSwap(false);
            }
         }
      }
      if (this.windTicks > 0) {
         this.windTicks--;
         this.faceTarget();
         if (Tuning.SWEATY_WIND_TICKS - this.windTicks == Tuning.SWEATY_LAUNCH_AT) {
            this.launch(target);
         }
      }

      if (!this.diving) {
         return;
      }
      this.airTicks++;
      this.faceTarget();
      Vec3 motion = this.getDeltaMovement();
      // A little steering toward the target on the way up and down.
      if (target != null && this.airTicks > 4 && !this.landed) {
         double dx = target.getX() - this.getX();
         double dz = target.getZ() - this.getZ();
         double len = Math.sqrt(dx * dx + dz * dz);
         if (len > 0.5) {
            double hx = motion.x + dx / len * 0.08;
            double hz = motion.z + dz / len * 0.08;
            double speed = Math.sqrt(hx * hx + hz * hz);
            double cap = Math.min(0.9, len / 4.0 + 0.15);
            if (speed > cap) {
               hx = hx / speed * cap;
               hz = hz / speed * cap;
            }
            this.setDeltaMovement(hx, motion.y, hz);
         }
      }
      // On the way down: the smash swing, or (with no one to smash) the water bucket clutch.
      if (motion.y < -0.2 && this.airTicks > 8 && !this.landed) {
         double ground = this.groundBelow();
         if (ground >= 0.0) {
            if (!this.decided && ground <= -motion.y * 4.2 + 1.0) {
               // Late decision: smash only if the target is under it, otherwise it is an MLG water clutch.
               this.decided = true;
               double ddx = target == null ? 99.0 : target.getX() - this.getX();
               double ddz = target == null ? 99.0 : target.getZ() - this.getZ();
               this.smashing = target != null && Math.sqrt(ddx * ddx + ddz * ddz) <= Tuning.SWEATY_SMASH_REACH;
            }
            if (!this.decided) {
               // not close to the ground yet
            } else if (this.smashing && target != null) {
               if (!this.smashAnim && ground <= -motion.y * 3.4 + 0.5) {
                  this.smashAnim = true;
                  this.level().broadcastEntityEvent(this, (byte)78);
               }
            } else if (this.clutchTicks <= 0 && !this.clutchPlaced && ground <= -motion.y * 4.2 + 1.0) {
               this.clutchTicks = Tuning.SWEATY_CLUTCH_TICKS;
               this.holdTemp(Items.WATER_BUCKET);
               this.level().broadcastEntityEvent(this, (byte)79);
            }
         }
      }
      if (this.clutchTicks > 0) {
         this.clutchTicks--;
         int at = Tuning.SWEATY_CLUTCH_TICKS - this.clutchTicks;
         if (at == Tuning.SWEATY_CLUTCH_PLACE_AT && this.clutchPos != null) {
            this.playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
            if (level.getGameRules().get(GameRules.MOB_GRIEFING) && level.getBlockState(this.clutchPos).isAir()) {
               level.setBlock(this.clutchPos, Blocks.WATER.defaultBlockState(), 3);
               this.clutchPlaced = true;
            }
            // The water is what catches it: no fall damage either way, and the fall counts as broken.
            this.fallDistance = 0.0;
         } else if (at == Tuning.SWEATY_CLUTCH_PICKUP_AT) {
            this.pickUpWater(level);
         }
      }
      boolean done = this.landed || (this.airTicks > 6 && this.isInWater()) || (this.airTicks > 6 && this.onGround()) || this.airTicks > 140;
      if (done && this.clutchTicks <= 0) {
         this.finishDive(level);
      } else if (done && this.landed) {
         // Landed in the middle of the clutch: carry the pickup through.
         this.landed = false;
      }
   }

   private void pickUpWater(ServerLevel level) {
      if (this.clutchPlaced && this.clutchPos != null && level.getBlockState(this.clutchPos).is(Blocks.WATER)) {
         level.setBlock(this.clutchPos, Blocks.AIR.defaultBlockState(), 3);
         this.playSound(SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
      }
      this.clutchPlaced = false;
   }

   /** Starts the swap animation: to the mace (true) or back to the sword (false). */
   private void startSwap(boolean toMace) {
      this.swapToMace = toMace;
      this.swapTicks = Tuning.STAGNATED_UNARM_TICKS;
      this.getNavigation().stop();
      this.level().broadcastEntityEvent(this, (byte)76);
   }

   private void launch(LivingEntity target) {
      this.holdTemp(null);
      this.diving = true;
      this.smashing = target != null;
      this.decided = false;
      this.smashAnim = false;
      this.landed = false;
      this.landedFall = 0.0F;
      this.airTicks = 0;
      this.clutchTicks = 0;
      this.clutchPlaced = false;
      this.clutchPos = null;
      double dx = target == null ? 0.0 : target.getX() - this.getX();
      double dz = target == null ? 0.0 : target.getZ() - this.getZ();
      double dist = Math.sqrt(dx * dx + dz * dz);
      double speed = Math.min(0.9, dist / 9.0);
      double hx = dist > 0.01 ? dx / dist * speed : 0.0;
      double hz = dist > 0.01 ? dz / dist * speed : 0.0;
      this.setDeltaMovement(hx, Tuning.SWEATY_LAUNCH_UP, hz);
      this.fallDistance = 0.0;
      this.playSound(SoundEvents.WIND_CHARGE_BURST.value(), 1.5F, 1.0F);
      ((ServerLevel)this.level()).sendParticles(ParticleTypes.GUST_EMITTER_SMALL, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
   }

   private void finishDive(ServerLevel level) {
      float fall = this.landedFall;
      boolean smash = this.smashing && this.smashAnim && fall >= 1.5F;
      if (this.smashing && !smash && this.landed && fall >= 1.5F) {
         smash = true;
      }
      this.diving = false;
      this.smashing = false;
      this.smashAnim = false;
      this.landed = false;
      this.airTicks = 0;
      this.pickUpWater(level);
      this.holdTemp(null);
      this.level().broadcastEntityEvent(this, (byte)80);
      this.decided = false;
      this.startSwap(false);
      if (smash) {
         this.smash(level, fall);
      }
   }

   /** The landing: damage grows with the fall (capped), everything close is thrown back. */
   private void smash(ServerLevel level, float fall) {
      float damage = Mth.clamp(Tuning.SWEATY_SMASH_BASE + fall * Tuning.SWEATY_SMASH_PER_BLOCK, 0.0F, Tuning.SWEATY_SMASH_MAX);
      this.playSound(fall > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND, 1.5F, 1.0F);
      level.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, this.getX(), this.getY() + 0.1, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0, 1.5, 3.0))) {
         if (e == this || e instanceof Stagnated || e instanceof Warden || !e.isAlive()) {
            continue;
         }
         if (e instanceof Player player && (player.isCreative() || player.isSpectator())) {
            continue;
         }
         double d = this.distanceTo(e);
         float amount = (e == this.getTarget() || d < 2.2) ? damage : damage * 0.5F;
         if (e.hurtServer(level, this.damageSources().mobAttack(this), amount)) {
            double kx = e.getX() - this.getX();
            double kz = e.getZ() - this.getZ();
            double kl = Math.max(0.01, Math.sqrt(kx * kx + kz * kz));
            e.setDeltaMovement(e.getDeltaMovement().add(kx / kl * 1.1, 0.4, kz / kl * 1.1));
            e.hurtMarked = true;
            if (e instanceof ServerPlayer player) {
               Hazard.addExposure(player, Tuning.STAGNATED_HIT_EXPOSURE);
            }
         }
      }
   }

   @Override
   protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
      super.dropCustomDeathLoot(level, source, killedByPlayer);
      if (!this.sweaty || !this.geared || !killedByPlayer) {
         return;
      }
      this.dropHalf(level, new ItemStack(Items.MACE), 1.0F);
      for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         this.dropHalf(level, this.getItemBySlot(slot), Tuning.SWEATY_ARMOR_DROP);
      }
   }

   /** Drops a copy worn down to about half its durability. */
   private void dropHalf(ServerLevel level, ItemStack stack, float chance) {
      if (stack.isEmpty() || this.random.nextFloat() >= chance) {
         return;
      }
      ItemStack copy = stack.copy();
      int max = copy.getMaxDamage();
      if (max > 4) {
         copy.setDamageValue(Mth.clamp(max / 2 + this.random.nextInt(Math.max(1, max / 5)) - max / 10, 1, max - 1));
      }
      this.spawnAtLocation(level, copy);
   }

   /** For testing (/breathless role): 0 none, 1 soldier, 2 excavator. */
   public void setRole(int role) {
      this.role = role;
   }

   /** For testing (/breathless weapon): makes it a soldier and gives it 0 nothing, 1 iron sword or 2 stone axe right away. */
   public void setWeapon(int weapon) {
      this.role = 1;
      this.weapon = weapon;
      this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      if (weapon == 0) {
         this.refreshStats();
      } else {
         this.equipWeapon();
      }
   }

   private static boolean solid(ServerLevel level, BlockPos pos) {
      return !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
   }

   /** Something it may break: a real solid block (not grass, flowers, veins or water), nothing with an inventory, nothing too hard. */
   private static boolean breakable(ServerLevel level, BlockPos pos) {
      var state = level.getBlockState(pos);
      if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity() || !solid(level, pos)) {
         return false;
      }
      float hardness = state.getDestroySpeed(level, pos);
      return hardness >= 0.0F && hardness <= Tuning.STAGNATED_MINE_MAX_HARDNESS;
   }

   private static boolean free(ServerLevel level, BlockPos pos) {
      return level.getBlockState(pos).isAir();
   }

   /** Holds this item in the left hand (it swaps between the pickaxe and the sculk block). */
   private void hold(net.minecraft.world.item.Item item) {
      if (!this.getItemBySlot(EquipmentSlot.OFFHAND).is(item)) {
         this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
         this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
      }
   }

   /**
    * The excavator. When its target cannot be walked to it, in this order:
    * 1) target above and the block it stands on is in reach: break that block (the target drops);
    * 2) target far below, straight under it: dig down;
    * 3) target above and close: pillar up with sculk, one block at a time;
    * 4) break the first solid block that is in the way on the straight line to the target (any direction);
    * 5) otherwise bridge a gap or lay a step toward it.
    * It only works when mob griefing is on.
    */
   private void considerAction(ServerLevel level) {
      LivingEntity target = this.getTarget();
      if (target == null || this.spotTicks > 0 || this.emergeTicks > 0 || this.armoringTicks > 0 || !this.onGround()) {
         return;
      }
      if (!level.getGameRules().get(GameRules.MOB_GRIEFING)) {
         return;
      }
      double dist = this.distanceTo(target);
      if (dist < 1.5 || dist > Tuning.STAGNATED_ACT_RANGE) {
         return;
      }
      if (!this.unreachable && this.stuckTicks < 20) {
         return;
      }
      double dx = target.getX() - this.getX();
      double dz = target.getZ() - this.getZ();
      double dy = target.getY() - this.getY();
      double h = Math.sqrt(dx * dx + dz * dz);
      BlockPos feet = this.blockPosition();

      // 1) Take the floor out from under a target that stands above it.
      if (dy >= 1.5) {
         BlockPos under = target.blockPosition().below();
         if (breakable(level, under) && this.inReach(under)) {
            this.startMining(level, under);
            return;
         }
      }
      // 2) Straight above a target that is below.
      if (dy <= -1.5 && h < 1.5 && breakable(level, feet.below())) {
         this.startMining(level, feet.below());
         return;
      }
      // 3) Pillar up toward a target above it.
      if (dy >= 1.5 && h <= Tuning.STAGNATED_PILLAR_RANGE && this.placed < Tuning.STAGNATED_MAX_BLOCKS && free(level, feet.above(2)) && !free(level, feet.below())) {
         this.hold(Items.SCULK);
         this.beginAction(3, feet, 0, (byte)72);
         this.setDeltaMovement(this.getDeltaMovement().x, 0.42, this.getDeltaMovement().z);
         return;
      }
      // 4) Break what is in the way.
      BlockPos blocker = this.firstBlocker(level, target, dy);
      if (blocker != null) {
         if (this.inReach(blocker)) {
            this.startMining(level, blocker);
         }
         return; // out of reach: it walks closer first
      }
      // 5) Bridge / step with sculk.
      if (this.placed >= Tuning.STAGNATED_MAX_BLOCKS) {
         return;
      }
      Direction dir = this.pickDirection(dx, dz);
      BlockPos ahead = feet.relative(dir);
      BlockPos pos = null;
      if (Math.abs(dy) < 1.5 && free(level, ahead.below()) && free(level, ahead) && free(level, ahead.above())) {
         pos = ahead.below(); // bridge over a gap
      } else if (dy >= 1.5 && free(level, ahead) && free(level, ahead.above()) && free(level, ahead.above(2))) {
         pos = ahead; // a step up
      }
      if (pos != null) {
         this.hold(Items.SCULK);
         this.beginAction(2, pos, Tuning.STAGNATED_PLACE_TICKS, (byte)72);
      }
   }

   /** Reach measured the way the game measures a player's: from the eyes to the nearest point of the block. */
   private boolean inReach(BlockPos pos) {
      Vec3 eye = this.getEyePosition();
      double dx = Math.max(Math.max(pos.getX() - eye.x, 0.0), eye.x - (pos.getX() + 1));
      double dy = Math.max(Math.max(pos.getY() - eye.y, 0.0), eye.y - (pos.getY() + 1));
      double dz = Math.max(Math.max(pos.getZ() - eye.z, 0.0), eye.z - (pos.getZ() + 1));
      return dx * dx + dy * dy + dz * dz <= Tuning.STAGNATED_REACH * Tuning.STAGNATED_REACH;
   }

   private void startMining(ServerLevel level, BlockPos pos) {
      float hardness = level.getBlockState(pos).getDestroySpeed(level, pos);
      int rounds = hardness <= 1.0F ? 1 : (hardness <= 3.0F ? 2 : 3);
      this.hold(Items.IRON_PICKAXE);
      this.beginAction(1, pos, Tuning.STAGNATED_MINE_TICKS * rounds, (byte)71);
   }

   /** Which way to build. It keeps its heading while the two axes are close, so a diagonal bridge does not zigzag and stall. */
   private Direction pickDirection(double dx, double dz) {
      Direction best = Math.abs(dx) > Math.abs(dz) ? (dx > 0 ? Direction.EAST : Direction.WEST) : (dz > 0 ? Direction.SOUTH : Direction.NORTH);
      if (this.heading != null && this.heading != best && Math.abs(Math.abs(dx) - Math.abs(dz)) < 1.5) {
         boolean stillToward = this.heading.getStepX() != 0 ? dx * this.heading.getStepX() > 0 : dz * this.heading.getStepZ() > 0;
         if (stillToward) {
            best = this.heading;
         }
      }
      this.heading = best;
      return best;
   }

   /**
    * Walks the straight line from this mob to its target and returns the first solid block that is in the way
    * (head height before foot height), in any direction. A foot-level block with room above it is a step it can climb,
    * so it is left alone. Returns null when nothing breakable is in the way (or something unbreakable is).
    */
   private BlockPos firstBlocker(ServerLevel level, LivingEntity target, double dy) {
      BlockPos feet = this.blockPosition();
      BlockPos targetFeet = target.blockPosition();
      Vec3 from = new Vec3(this.getX(), feet.getY() + 0.5, this.getZ());
      Vec3 to = new Vec3(target.getX(), target.getY() + 0.5, target.getZ());
      Vec3 line = to.subtract(from);
      double length = Math.min(line.length(), Tuning.STAGNATED_REACH + 1.5);
      if (length < 0.5) {
         return null;
      }
      Vec3 step = line.normalize().scale(0.4);
      BlockPos last = feet;
      for (int i = 1; i * 0.4 <= length; i++) {
         BlockPos v = BlockPos.containing(from.add(step.scale(i)));
         if (v.equals(last) || v.equals(feet) || v.equals(targetFeet) || v.equals(targetFeet.above())) {
            continue;
         }
         last = v;
         BlockPos[] candidates = {v.above(), v};
         for (BlockPos cand : candidates) {
            if (cand.getY() < feet.getY() && !(dy <= -1.5 && cand.getY() >= targetFeet.getY() - 1)) {
               continue; // never undermine itself unless it is going down after something
            }
            if (!solid(level, cand)) {
               continue;
            }
            boolean footLevel = cand.getY() == feet.getY() && cand.equals(v);
            if (footLevel && dy >= -0.5 && !solid(level, v.above()) && !solid(level, v.above(2))) {
               continue; // a one-block step it can simply climb
            }
            return breakable(level, cand) ? cand : null;
         }
      }
      return null;
   }

   /** It has a path and is not on a goal that moves it, yet it is standing still: re-path, then push straight at the target. */
   private void unstick() {
      LivingEntity target = this.getTarget();
      if (target == null || this.stuckTicks < 20 || this.unreachable || this.action != 0 || this.spotTicks > 0 || this.armoringTicks > 0 || this.emergeTicks > 0) {
         return;
      }
      this.getNavigation().moveTo(target, Tuning.STAGNATED_CHASE_MODIFIER);
      if (this.stuckTicks >= 40) {
         this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), Tuning.STAGNATED_CHASE_MODIFIER);
         if (this.horizontalCollision && this.onGround()) {
            this.getJumpControl().jump();
         }
      }
   }

   private void beginAction(int kind, BlockPos pos, int ticks, byte event) {
      this.action = kind;
      this.actionPos = pos;
      this.actionTicks = ticks;
      this.getNavigation().stop();
      this.level().broadcastEntityEvent(this, event);
   }

   private void stopAction() {
      this.action = 0;
      this.actionPos = null;
      this.level().broadcastEntityEvent(this, (byte)73);
   }

   private void tickAction(ServerLevel level) {
      if (this.getTarget() == null || !this.isAlive() || this.actionPos == null) {
         this.stopAction();
         return;
      }
      double dx = this.actionPos.getX() + 0.5 - this.getX();
      double dz = this.actionPos.getZ() + 0.5 - this.getZ();
      float yaw = (float)(Mth.atan2(dz, dx) * 180.0F / (float)Math.PI) - 90.0F;
      this.setYRot(yaw);
      this.setYBodyRot(yaw);
      this.setYHeadRot(yaw);
      if (this.action == 3) {
         this.faceTarget();
         this.actionTicks++;
         boolean high = this.getY() >= this.actionPos.getY() + 1.05;
         if (high) {
            this.placeSculk(level);
            this.pillared++;
            if (this.pillared >= 2 && this.getTarget() instanceof ServerPlayer player && this.distanceTo(player) <= 4.5F) {
               Achievements.award(player, "determination");
            }
         }
         if (high || this.actionTicks > 20) {
            this.stopAction();
         }
         return;
      }
      if (--this.actionTicks > 0) {
         if (this.action == 1 && this.actionTicks % 9 == 0) {
            var sound = level.getBlockState(this.actionPos).getSoundType().getHitSound();
            level.playSound(null, this.actionPos, sound, SoundSource.BLOCKS, 1.0F, 0.8F);
         }
         return;
      }
      if (this.action == 1) {
         if (breakable(level, this.actionPos)) {
            level.destroyBlock(this.actionPos, Tuning.STAGNATED_MINE_DROPS, this, 512);
            if (this.getTarget() instanceof ServerPlayer player && this.distanceTo(player) <= 4.0F) {
               Achievements.award(player, "determination");
            }
         }
      } else {
         this.placeSculk(level);
      }
      this.stopAction();
   }

   private void placeSculk(ServerLevel level) {
      if (free(level, this.actionPos) && level.getEntitiesOfClass(LivingEntity.class, new AABB(this.actionPos)).isEmpty()) {
         var sculk = Blocks.SCULK.defaultBlockState();
         level.setBlock(this.actionPos, sculk, 3);
         level.playSound(null, this.actionPos, sculk.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
         this.placed++;
      }
   }

   /** Can it walk to its target, and is it standing still while chasing? Run every 5 ticks. */
   private void refreshReach() {
      LivingEntity target = this.getTarget();
      if (target == null || this.spotTicks > 0 || this.emergeTicks > 0 || this.armoringTicks > 0) {
         this.unreachable = false;
         this.stuckTicks = 0;
         this.lastPos = this.position();
         return;
      }
      Path path = this.getNavigation().createPath(target, 0);
      this.unreachable = path == null || !path.canReach();
      if (this.distanceTo(target) > 2.5 && this.position().distanceToSqr(this.lastPos) < 0.01) {
         this.stuckTicks += 5;
      } else {
         this.stuckTicks = 0;
      }
      this.lastPos = this.position();
   }

   /** When the target cannot be walked to, walk straight at it anyway (up to the wall, the ledge, the pillar) instead of standing around. */
   private class ApproachGoal extends Goal {
      ApproachGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         LivingEntity target = Stagnated.this.getTarget();
         return target != null && Stagnated.this.unreachable && Stagnated.this.distanceTo(target) > 1.8;
      }

      @Override
      public boolean canContinueToUse() {
         return this.canUse();
      }

      @Override
      public void tick() {
         LivingEntity target = Stagnated.this.getTarget();
         if (target != null) {
            Stagnated.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            Stagnated.this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), Tuning.STAGNATED_CHASE_MODIFIER);
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
      Achievements.awardNear(this, 24.0, "my_mind_of_many_ears");
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
      } else if (event == 70) {
         this.armoringClock = 0;
      } else if (event == 71) {
         this.mineClock = 0;
         this.placeClock = -1;
      } else if (event == 72) {
         this.placeClock = 0;
         this.mineClock = -1;
      } else if (event == 73) {
         this.mineClock = -1;
         this.placeClock = -1;
         this.unarmClock = -1;
      } else if (event == 74) {
         this.shieldClock = 0;
      } else if (event == 75) {
         this.spotArmedClock = 0;
      } else if (event == 76) {
         this.unarmClock = 0;
      } else if (event == 77) {
         this.windClock = 0;
         this.flying = true;
         this.flyTicks = 0;
      } else if (event == 78) {
         this.smashClock = 0;
      } else if (event == 79) {
         this.clutchClock = 0;
         this.flying = false;
      } else if (event == 80) {
         this.flying = false;
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
      if (this.shieldTicks > 0) {
         return false;
      }
      if (this.sweaty) {
         // The mace is only used for the smash; on the ground and in the swaps it never melees.
         if (!this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.DIAMOND_SWORD) || this.swapTicks > 0 || this.diving) {
            return false;
         }
         boolean hit = this.strike(level, target);
         if (hit) {
            this.swordHits++;
         }
         return hit;
      }
      // With the axe in hand, a raised shield gets the clap: wind up, then break it (see shieldStrike).
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.STONE_AXE) && target instanceof Player player && player.isBlocking()
         && !player.getCooldowns().isOnCooldown(player.getUseItem())) {
         this.shieldTicks = Tuning.STAGNATED_SHIELD_TICKS;
         this.getNavigation().stop();
         this.level().broadcastEntityEvent(this, (byte)74);
         return false;
      }
      return this.strike(level, target);
   }

   /** The clap lands: the hit goes in, and a shield that is still up is knocked down for a few seconds. */
   private void shieldStrike() {
      if (!(this.getTarget() instanceof Player player) || !this.isAlive() || this.distanceTo(player) > 3.5F || !(this.level() instanceof ServerLevel level)) {
         return;
      }
      ItemStack shield = player.isBlocking() ? player.getUseItem().copy() : ItemStack.EMPTY;
      this.strike(level, player);
      if (!shield.isEmpty() && !player.getCooldowns().isOnCooldown(shield)) {
         player.getCooldowns().addCooldown(shield, Tuning.STAGNATED_SHIELD_DISABLE_TICKS);
         player.stopUsingItem();
         level.broadcastEntityEvent(player, (byte)30);
      }
   }

   private boolean strike(ServerLevel level, Entity target) {
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
