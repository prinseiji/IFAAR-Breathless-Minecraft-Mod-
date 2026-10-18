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

   private static final class Marker extends MobEffect {
      Marker(int color) {
         super(MobEffectCategory.HARMFUL, color);
      }
   }

   public static void register() {
      MobEffect effect = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("spore_exposure"), new Marker(0x2FB4D8));
      SPORE_EXPOSURE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
      MobEffect moribund = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("moribund"), new Marker(0x1B4F63));
      MORIBUND = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(moribund);
   }
}
