package com.kodari.breathless;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** World generation: registers the Nidus cave pocket and adds it to the overworld (not the deep dark, which has its own city). How often it is tried is in data/breathless/worldgen/placed_feature/nidus_pocket.json. */
public final class ModWorldgen {
   private ModWorldgen() {
   }

   public static void register() {
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_pocket"), new NidusPocketFeature());
      ResourceKey<PlacedFeature> key = ResourceKey.create(Registries.PLACED_FEATURE, Breathless.id("nidus_pocket"));
      BiomeModifications.addFeature(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(Biomes.DEEP_DARK)), GenerationStep.Decoration.UNDERGROUND_DECORATION, key);
   }
}
