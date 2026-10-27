package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** The shine at Eli's crossbow while he aims: small, faces you, turns slowly. The server sends it again every tick. */
final class GlintParticle extends SingleQuadParticle {
   private GlintParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = 0.55F;
      this.lifetime = 2;
      this.alpha = 0.95F;
      this.roll = level.getGameTime() * 0.09F;
      this.oRoll = this.roll - 0.09F;
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
      this.roll = this.level.getGameTime() * 0.09F;
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
         return new GlintParticle(level, x, y, z, this.sprites.get(random));
      }
   }
}
