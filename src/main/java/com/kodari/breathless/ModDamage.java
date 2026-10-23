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

   public static DamageSource sear(ServerLevel level) {
      return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SEAR));
   }
}
