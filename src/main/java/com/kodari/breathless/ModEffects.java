package com.kodari.breathless;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * "Spore Exposure": a marker effect shown in the effect list whenever the exposure meter is above 0,
 * even after you leave the spores. It does nothing by itself; Hazard keeps it in sync with the meter.
 * Its icon is textures/mob_effect/spore_exposure.png (and the same file under textures/gui/sprites/mob_effect/).
 */
public final class ModEffects {
   private ModEffects() {
   }

   public static Holder<MobEffect> SPORE_EXPOSURE;

   private static final class SporeExposure extends MobEffect {
      SporeExposure() {
         super(MobEffectCategory.HARMFUL, 0x2FB4D8);
      }
   }

   public static void register() {
      MobEffect effect = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("spore_exposure"), new SporeExposure());
      SPORE_EXPOSURE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
   }
}
