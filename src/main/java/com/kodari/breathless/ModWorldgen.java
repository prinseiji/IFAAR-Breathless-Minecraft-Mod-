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
 * Carrions and sculk splotches (SculkPatchFeature) are added here too.
 */
public final class ModWorldgen {
   private ModWorldgen() {
   }

   public static void register() {
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_pocket"), new NidusPocketFeature());
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_cave"), new NidusCaveFeature(false));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_cave_here"), new NidusCaveFeature(true));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("sculk_splotch"), new SculkPatchFeature(SculkPatchFeature.Kind.SPLOTCH));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("carrion_patch"), new SculkPatchFeature(SculkPatchFeature.Kind.CARRION_PATCH));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("carrion_deep"), new SculkPatchFeature(SculkPatchFeature.Kind.DEEP_CARRION));
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("odium_deep"), new SculkPatchFeature(SculkPatchFeature.Kind.ODIUM_DEEP));
      // The Nidus cave and the Carrions in the deep dark; the splotches and Carrion patches in every other overworld biome (they only generate below Y=0).
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DEEP_DARK), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, placed("nidus_cave"));
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DEEP_DARK), GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("carrion_deep"));
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DEEP_DARK), GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("odium_deep"));
      BiomeModifications.addFeature(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(Biomes.DEEP_DARK)), GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("sculk_splotch"));
      BiomeModifications.addFeature(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(Biomes.DEEP_DARK)), GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("carrion_patch"));
   }

   private static ResourceKey<PlacedFeature> placed(String name) {
      return ResourceKey.<PlacedFeature>create(Registries.PLACED_FEATURE, Breathless.id(name));
   }
}
