package com.kodari.breathless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;

/**
 * Everything about what lives below Y=0:
 * - only zombies and skeletons may spawn naturally
 * - zombies wear gas masks (low filter life) and drop them
 * - skeletons see less far and shoot worse, and their arrows glow so you can dodge them
 */
public final class MobRules {
   private MobRules() {
   }

   /** Vanilla skeleton detection range. We set absolute values (not multiply), so reloads can never stack the penalty. */
   private static final double SKELETON_NORMAL_RANGE = 16.0D;
   private static final Random RANDOM = new Random();

   private static final Set<Mob> ZOMBIES = Collections.newSetFromMap(new IdentityHashMap<>());
   private static final Set<Mob> SKELETONS = Collections.newSetFromMap(new IdentityHashMap<>());
   private static final List<Entity> ARROWS = new ArrayList<>();

   public static void init() {
      // 1) Natural spawning below Y=0: zombies and skeletons only.
      ServerEntityEvents.ALLOW_LOAD.register((entity, level, reason, loadedFromDisk) -> {
         if (loadedFromDisk || !(entity instanceof Monster)) {
            return true;
         }
         if (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION) {
            return true;
         }
         if (!Masks.isHazardLevel(level) || entity.getY() >= Tuning.HAZARD_Y) {
            return true;
         }
         String type = Masks.typeId(entity);
         return type.equals("minecraft:zombie") || type.equals("minecraft:skeleton");
      });

      ServerEntityEvents.ENTITY_LOAD.register(MobRules::onLoad);
      ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
         if (entity instanceof Mob mob) {
            ZOMBIES.remove(mob);
            SKELETONS.remove(mob);
         }
      });

      ServerTickEvents.END_SERVER_TICK.register(MobRules::tick);

      // 2) Zombie loot: mask / filter drops, and the mask wearing out when they are hit.
      ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
         if (entity instanceof Mob mob && Masks.typeId(mob).equals("minecraft:zombie")) {
            dropMask(mob);
         }
         return true;
      });
      ServerLivingEntityEvents.AFTER_DEATH.register(MobRules::sporeDeath);
      ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
         if (source.getEntity() == null || !(entity instanceof Mob mob) || !Masks.typeId(mob).equals("minecraft:zombie")) {
            return;
         }
         ItemStack head = mob.getItemBySlot(EquipmentSlot.HEAD);
         if (!Masks.isMask(head)) {
            return;
         }
         MaskFilters filters = Masks.getFilters(head);
         int wear = Math.max(1, Math.round(damageTaken * Tuning.ZOMBIE_MASK_WEAR_PER_DAMAGE));
         MaskFilters worn = filters.withLife(Math.max(0, filters.first() - wear), Math.max(0, filters.second() - wear));
         if (!worn.working()) {
            // The mask gives out: from now on this zombie breathes spores like everyone else.
            mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            mob.level().playSound(null, mob.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.8F, 1.1F);
         } else {
            head.set(ModItems.FILTERS, worn);
         }
      });
   }

   // ---- everything else breathes the spores too ----

   /** Every other mob in the overworld: hurt by the spores below Y=0 (skeleton kinds, the Stagnated, the Warden and bosses are immune). */
   private static final java.util.Set<Mob> BREATHERS = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());
   /** Mobs the spores hurt in the last two seconds (a death then leaves a sculk splotch). */
   private static final java.util.Map<Mob, Integer> SPORE_HURT = new java.util.WeakHashMap<>();

   private static boolean immune(String type, Entity entity) {
      return entity instanceof Stagnated || type.equals("minecraft:skeleton") || type.equals("minecraft:stray") || type.equals("minecraft:wither_skeleton")
         || type.equals("minecraft:bogged") || type.equals("minecraft:skeleton_horse") || type.equals("minecraft:warden") || type.equals("minecraft:ender_dragon")
         || type.equals("minecraft:wither") || type.equals("minecraft:creaking");
   }

   /** One tick-second of spores for a mob without a mask: damage scaled to its health so big mobs do not shrug it off. */
   private static void sporeDamage(MinecraftServer server, Mob mob, ServerLevel level) {
      float damage = Math.max(Tuning.ZOMBIE_SPORE_DAMAGE, mob.getMaxHealth() / 20.0F);
      SPORE_HURT.put(mob, server.getTickCount());
      mob.hurtServer(level, level.damageSources().drown(), damage);
   }

   /** A mob that died of the spores becomes sculk: a splotch with as many blocks as it was worth in XP, and a burst of sculk particles. */
   private static void sporeDeath(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
      if (!(entity instanceof Mob mob) || !(mob.level() instanceof ServerLevel level) || !source.is(net.minecraft.world.damagesource.DamageTypes.DROWN)) {
         return;
      }
      Integer when = SPORE_HURT.remove(mob);
      if (when == null || level.getServer().getTickCount() - when > 40) {
         return;
      }
      int xp = Math.max(1, Math.min(40, mob.getExperienceReward(level, null)));
      level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.SCULK.defaultBlockState()),
         mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 30 + xp * 3, mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.4, mob.getBbWidth() * 0.5, 0.1);
      EliSummon.splotch(level, mob.blockPosition(), xp);
   }

   // ---- loading ----

   private static void onLoad(Entity entity, ServerLevel level) {
      if (!Masks.isHazardLevel(level)) {
         return;
      }
      String type = Masks.typeId(entity);
      if (entity instanceof Mob other && !type.equals("minecraft:zombie") && !immune(type, entity)) {
         BREATHERS.add(other);
      }
      if (type.equals("minecraft:zombie") && entity instanceof Mob zombie) {
         ZOMBIES.add(zombie);
         if (!entity.isLoadedFromDisk() && entity.getY() < Tuning.HAZARD_Y && zombie.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            zombie.setItemSlot(EquipmentSlot.HEAD, rollZombieMask());
         }
      } else if (type.equals("minecraft:skeleton") && entity instanceof Mob skeleton) {
         SKELETONS.add(skeleton);
      } else if (type.equals("minecraft:arrow") && !entity.isLoadedFromDisk()) {
         onArrow(entity);
      }
   }

   /** Remaining life of one zombie filter: 90% of the time 3-25% left, 10% of the time 25-50%. */
   private static int rollFilterLife() {
      float remaining = RANDOM.nextFloat() < Tuning.ZOMBIE_MASK_LOW_CHANCE
         ? 0.03F + RANDOM.nextFloat() * 0.22F
         : 0.25F + RANDOM.nextFloat() * 0.25F;
      return Math.max(3, Math.round(remaining * Tuning.FILTER_MAX));
   }

   /** A gas mask with two nearly dead filters. */
   private static ItemStack rollZombieMask() {
      return ModItems.maskWith(new MaskFilters(rollFilterLife(), rollFilterLife()));
   }

   private static void dropMask(Mob zombie) {
      ItemStack head = zombie.getItemBySlot(EquipmentSlot.HEAD);
      if (!Masks.isMask(head)) {
         return;
      }
      ItemStack drop = ItemStack.EMPTY;
      float roll = RANDOM.nextFloat();
      MaskFilters filters = Masks.getFilters(head);
      if (roll < Tuning.ZOMBIE_DROP_MASK) {
         drop = head.copy();
      } else if (roll < Tuning.ZOMBIE_DROP_MASK + Tuning.ZOMBIE_DROP_FILTER && filters.count() > 0) {
         drop = ModItems.filterWith(filters.lastLife(), filters.lastReinforced());
      }
      // Clear the slot so vanilla's own equipment drop cannot give a second copy.
      zombie.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
      if (!drop.isEmpty() && zombie.level() instanceof ServerLevel level) {
         level.addFreshEntity(new ItemEntity(level, zombie.getX(), zombie.getY() + 0.3D, zombie.getZ(), drop));
      }
   }

   // ---- skeleton arrows ----
   // (The worse aim itself is done by SkeletonAimMixin; here we only mark arrows for the trail.)

   private static void onArrow(Entity arrow) {
      for (Mob skeleton : SKELETONS) {
         if (skeleton.getY() < Tuning.HAZARD_Y && skeleton.distanceToSqr(arrow) < 2.25D) {
            ARROWS.add(arrow);
            return;
         }
      }
   }

   // ---- ticking ----

   private static void tick(MinecraftServer server) {
      // Faint white/cyan streak behind skeleton arrows (counter-play).
      Iterator<Entity> arrows = ARROWS.iterator();
      while (arrows.hasNext()) {
         Entity arrow = arrows.next();
         if (arrow.isRemoved() || arrow.getDeltaMovement().lengthSqr() < 0.0025D || !(arrow.level() instanceof ServerLevel level)) {
            arrows.remove();
            continue;
         }
         level.sendParticles(ParticleTypes.END_ROD, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0.02D, 0.02D, 0.02D, 0.0D);
         level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0.03D, 0.03D, 0.03D, 0.0D);
      }

      if (server.getTickCount() % 20 != 0) {
         return;
      }

      // Zombies without a working mask are hurt by the spores.
      Iterator<Mob> zombies = ZOMBIES.iterator();
      while (zombies.hasNext()) {
         Mob zombie = zombies.next();
         if (zombie.isRemoved() || !(zombie.level() instanceof ServerLevel level)) {
            zombies.remove();
            continue;
         }
         if (zombie.isAlive() && Masks.isUnderground(zombie) && !Masks.hasWorkingMask(zombie)) {
            sporeDamage(server, zombie, level);
         }
      }
      // Every other mob without a mask.
      for (Mob mob : new java.util.ArrayList<>(BREATHERS)) {
         if (mob.isRemoved() || !mob.isAlive() || !(mob.level() instanceof ServerLevel level)) {
            continue;
         }
         if (Masks.isUnderground(mob) && !Masks.hasWorkingMask(mob)) {
            sporeDamage(server, mob, level);
         }
      }

      // Skeletons are immune to the spores but see less far while inside them.
      Iterator<Mob> skeletons = SKELETONS.iterator();
      while (skeletons.hasNext()) {
         Mob skeleton = skeletons.next();
         if (skeleton.isRemoved()) {
            skeletons.remove();
            continue;
         }
         AttributeInstance range = skeleton.getAttribute(Attributes.FOLLOW_RANGE);
         if (range == null) {
            continue;
         }
         double target = Masks.isUnderground(skeleton) ? SKELETON_NORMAL_RANGE * Tuning.SKELETON_RANGE_FACTOR : SKELETON_NORMAL_RANGE;
         if (Math.abs(range.getBaseValue() - target) > 0.01D) {
            range.setBaseValue(target);
         }
      }
   }
}
