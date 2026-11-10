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

/**
 * World generation. The Nidus cave (NidusCaveFeature) is added to the deep dark biome, which is also where ancient cities are.
 * Test it with /place feature breathless:nidus_cave_here while standing in a deep dark cavern (try a few spots; it needs a cavern wall about 16 blocks from the middle of the chunk).
 * The old jigsaw pockets (data/breathless/worldgen/structure/anathema_pocket.json and the three .nbt files) are switched off by an empty biome tag.
 * The older feature version, nidus_pocket, is still registered but not added to any biome.
 */
public final class ModWorldgen {
   private ModWorldgen() {
   }

   public static void register() {
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_pocket"), new NidusPocketFeature());
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_cave"), new NidusCaveFeature(false));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_cave_here"), new NidusCaveFeature(true));
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DEEP_DARK), GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
         ResourceKey.<PlacedFeature>create(Registries.PLACED_FEATURE, Breathless.id("nidus_cave")));
   }
}
