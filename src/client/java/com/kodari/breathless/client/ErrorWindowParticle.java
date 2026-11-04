package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** A small error pop-up window that appears around SLOTH while he glitches: it jitters, blinks, drifts a little and disappears. Always faces the camera. */
final class ErrorWindowParticle extends SingleQuadParticle {
   private final float baseSize;

   private ErrorWindowParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, RandomSource random) {
      super(level, x, y, z, sprite);
      this.hasPhysics = false;
      this.baseSize = 0.28F + random.nextFloat() * 0.3F;
      this.quadSize = this.baseSize;
      this.lifetime = 14 + random.nextInt(14);
      this.xd = (random.nextFloat() - 0.5F) * 0.02D;
      this.yd = (random.nextFloat() - 0.2F) * 0.02D;
      this.zd = (random.nextFloat() - 0.5F) * 0.02D;
      this.alpha = 1.0F;
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
      if (this.age++ >= this.lifetime) {
         this.remove();
         return;
      }
      this.x += this.xd;
      this.y += this.yd;
      this.z += this.zd;
      // Glitchy: blinks every other tick and the size jumps a little. Fades over the last quarter of its life.
      float fade = Math.min(1.0F, (this.lifetime - this.age) / (this.lifetime * 0.25F));
      this.alpha = ((this.age / 2) % 2 == 0 ? 1.0F : 0.55F) * fade;
      this.quadSize = this.baseSize * (this.age % 5 == 0 ? 1.15F : 1.0F);
   }

   static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      Provider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new ErrorWindowParticle(level, x, y, z, this.sprites.get(random), random);
      }
   }
}
