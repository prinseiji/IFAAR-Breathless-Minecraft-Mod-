package com.kodari.breathless;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;

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
   /** Silence: nothing you do makes a noise (see Silence.java). Brewed from a Rotting Heart. */
   public static Holder<MobEffect> SILENCE;
   public static Holder<Potion> SILENCE_POTION;
   public static Holder<Potion> LONG_SILENCE_POTION;

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

   private static final class Hush extends MobEffect {
      Hush() {
         super(MobEffectCategory.BENEFICIAL, 0x2C8F94);
      }
   }

   public static void register() {
      SILENCE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("silence"), new Hush()));
      // Both potions share the name "silence" (that is what the lang keys use). There is no strong version on purpose.
      ResourceKey<Potion> silenceKey = ResourceKey.create(Registries.POTION, Breathless.id("silence"));
      ResourceKey<Potion> longKey = ResourceKey.create(Registries.POTION, Breathless.id("long_silence"));
      SILENCE_POTION = Registry.registerForHolder(BuiltInRegistries.POTION, silenceKey, new Potion("silence", new MobEffectInstance(SILENCE, Tuning.SILENCE_TICKS, 0)));
      LONG_SILENCE_POTION = Registry.registerForHolder(BuiltInRegistries.POTION, longKey, new Potion("silence", new MobEffectInstance(SILENCE, Tuning.SILENCE_LONG_TICKS, 0)));
      FabricPotionBrewingBuilder.BUILD.register(builder -> {
         builder.addMix(Potions.AWKWARD, ModItems.ROTTING_HEART, SILENCE_POTION);
         builder.addMix(SILENCE_POTION, Items.REDSTONE, LONG_SILENCE_POTION);
      });
      HEMORRHAGE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("hemorrhage"), new Bleed()));
      MobEffect effect = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("spore_exposure"), new Marker(0x2FB4D8));
      SPORE_EXPOSURE = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
      MobEffect moribund = Registry.register(BuiltInRegistries.MOB_EFFECT, Breathless.id("moribund"), new Marker(0x1B4F63));
      MORIBUND = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(moribund);
   }
}
