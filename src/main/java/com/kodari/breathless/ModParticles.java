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

   /** The target on the ground for the stun slam (flat, turns slowly). */
   public static SimpleParticleType STUN_MARK;

   /** The small shine that turns at his crossbow while he aims. */
   public static SimpleParticleType XB_GLINT;

   /** One star per stocked rocket, turning above his head (a single particle that follows him, see StockStarParticle). */
   public static SimpleParticleType STOCK_STAR;

   /** The blood wings behind Elijah on Hard (flat pictures that turn with his body, see WingsParticle). */
   public static SimpleParticleType BLOOD_WINGS;

   public static void register() {
      SPORE_EYE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("spore_eye"), FabricParticleTypes.simple());
      SPORE_EYE_SURGE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("spore_eye_surge"), FabricParticleTypes.simple());
      STUN_MARK = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("stun_mark"), FabricParticleTypes.simple());
      XB_GLINT = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("xb_glint"), FabricParticleTypes.simple());
      STOCK_STAR = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("stock_star"), FabricParticleTypes.simple());
      BLOOD_WINGS = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("blood_wings"), FabricParticleTypes.simple());
      RAGE_RING = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Breathless.id("rage_ring"), FabricParticleTypes.simple());
   }
}
