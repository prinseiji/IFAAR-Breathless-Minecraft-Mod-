package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * World generation. The Anathema pocket is now a structure (data/breathless/worldgen/structure/anathema_pocket.json), so there is nothing to hook here for it:
 * it is found with /locate structure breathless:anathema_pocket. The older feature version is still registered, because its json files are still in the mod,
 * but it is no longer added to any biome. It can still be placed by hand with /place feature breathless:nidus_pocket.
 */
public final class ModWorldgen {
   private ModWorldgen() {
   }

   public static void register() {
      Registry.register(BuiltInRegistries.FEATURE, Breathless.id("nidus_pocket"), new NidusPocketFeature());
   }
}
