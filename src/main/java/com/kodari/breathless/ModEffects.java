package com.kodari.breathless;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * "Stagnation": a marker effect shown in the effect list whenever the exposure meter is above 0,
 * even after you leave the spores. "Moribund" is the same marker, shown instead while heavy sculk is around you.
 * They do nothing by themselves; Hazard keeps them in sync with the meter.
 * Icons: textures/mob_effect/spore_exposure.png and moribund.png (and the same files under textures/gui/sprites/mob_effect/).
 * (The effect id stays "spore_exposure" so saved games keep working; only the name changed.)
 */
public final class ModEffects {
   private ModEffects() {
   }

   public static Holder<MobEffect> SPORE_EXPOSURE;
   public static Holder<MobEffect> MORIBUND;
   public static Holder<MobEffect> HEMORRHAGE;

   private static final class Marker extends MobEffect {
      Marker(int color) {
         super(MobEffectCategory.HARMFUL, color);
      }
   }

   /** Bleeding from Eli's Sword: a little damage now and then that ignores armor. */
   private static final class Bleed extends MobEffect {
      Bleed() {
         super(MobEffectCategory.HARMFUL, 0x8E0F18);
      }

      @Override
      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return duration % Tuning.HEMORRHAGE_INTERVAL == 0;
      }

      @Override
      public boolean applyEffectTick(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.LivingEntity entity, int amplifier) {
         entity.hurtServer(level, level.damageSources().magic(), Tuning.HEMORRHAGE_DAMAGE * (amplifier + 1));
         return true;
      }
   }

   public static void register() {
      HEMORRHAGE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("hemorrhage"), new Bleed()));
      MobEffect effect = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("spore_exposure"), new Marker(0x2FB4D8));
      SPORE_EXPOSURE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
      MobEffect moribund = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("moribund"), new Marker(0x1B4F63));
      MORIBUND = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(moribund);
   }
}
