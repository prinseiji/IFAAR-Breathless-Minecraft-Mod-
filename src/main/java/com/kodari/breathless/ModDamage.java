package com.kodari.breathless;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

/** Breathless' own damage types (defined in data/breathless/damage_type) so they get their own death messages. */
public final class ModDamage {
   public static final ResourceKey<DamageType> SPORES = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("spores"));
   public static final ResourceKey<DamageType> SEAR = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("sear"));

   public static final ResourceKey<DamageType> ETHAN_MACE = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("ethan_mace"));
   public static final ResourceKey<DamageType> ETHAN_SWORD = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("ethan_sword"));

   public static final ResourceKey<DamageType> PARRY_REFLECT = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("parry_reflect"));
   public static final ResourceKey<DamageType> ELI_BARRAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("eli_barrage"));
   public static final ResourceKey<DamageType> SLOTH_BLAST = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("sloth_blast"));
   public static final ResourceKey<DamageType> ELI_ROCKET = ResourceKey.create(Registries.DAMAGE_TYPE, Breathless.id("eli_rocket"));

   private ModDamage() {
   }

   public static DamageSource spores(ServerLevel level) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SPORES));
   }

   public static DamageSource ethanMace(ServerLevel level, net.minecraft.world.entity.Entity attacker) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ETHAN_MACE), attacker);
   }

   public static DamageSource ethanSword(ServerLevel level, net.minecraft.world.entity.Entity attacker) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ETHAN_SWORD), attacker);
   }

   /** His firework rocket: armor still works, enchantment protection does not (see the bypasses_enchantments tag). */
   /** Damage sent back by a parry. Not scaled by difficulty, and neither blocked nor parried (it is in the bypasses_shield tag). */
   public static DamageSource parryReflect(ServerLevel level, net.minecraft.world.entity.Entity parrier) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(PARRY_REFLECT), parrier);
   }

   public static DamageSource eliBarrage(ServerLevel level, net.minecraft.world.entity.Entity attacker) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ELI_BARRAGE), attacker);
   }

   /** The blast at the end of the Hard fight: nothing stops it (see the bypasses_* tags). */
   public static DamageSource slothBlast(ServerLevel level, net.minecraft.world.entity.Entity attacker) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SLOTH_BLAST), attacker);
   }

   public static DamageSource eliRocket(ServerLevel level, net.minecraft.world.entity.Entity attacker) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ELI_ROCKET), attacker);
   }

   public static DamageSource sear(ServerLevel level) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SEAR));
   }
}
