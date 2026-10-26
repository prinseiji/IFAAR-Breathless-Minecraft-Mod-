package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** The stun slam's target: a picture lying flat on the ground that turns slowly. The server sends it again every tick or two, so it stays where the slam will land. */
final class StunMarkParticle extends SingleQuadParticle {
   private StunMarkParticle(ClientLevel level, double x, double y, double z, float size, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = size;
      this.lifetime = 3;
      this.alpha = 0.9F;
      this.roll = spin(level);
      this.oRoll = this.roll - 0.08F;
   }

   /** The turn comes from the world clock, so the copies sent on different ticks always line up (no double image). */
   private static float spin(ClientLevel level) {
      return level.getGameTime() * 0.08F;
   }

   @Override
   public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
      // Lying flat, facing up.
      return (rotation, camera, partialTick) -> rotation.rotationX((float)(-Math.PI / 2.0));
   }

   @Override
   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @Override
   public int getLightCoords(float partialTick) {
      return 0xF000F0;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.oRoll = this.roll;
      this.roll = spin(this.level);
      if (this.age++ >= this.lifetime) {
         this.remove();
      }
   }

   static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      Provider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new StunMarkParticle(level, x, y, z, (float)xd, this.sprites.get(random));
      }
   }
}
