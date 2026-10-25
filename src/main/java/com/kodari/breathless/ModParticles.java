package com.kodari.breathless;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/** The little teal eyes. Both use the three textures in textures/particle/spore_eye_*.png (see assets/breathless/particles). */
public final class ModParticles {
   private ModParticles() {
   }

   /** Small eyes hanging in the air. */
   public static SimpleParticleType SPORE_EYE;
   /** Eyes that rush toward your face when you have no working mask. */
   public static SimpleParticleType SPORE_EYE_SURGE;
   /** The ring of thorns that circles Elijah while he is enraged by parries (billboard, always faces you). */
   public static SimpleParticleType RAGE_RING;

   public static void register() {
      SPORE_EYE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("spore_eye"), FabricParticleTypes.simple());
      SPORE_EYE_SURGE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("spore_eye_surge"), FabricParticleTypes.simple());
      RAGE_RING = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("rage_ring"), FabricParticleTypes.simple());
   }
}
