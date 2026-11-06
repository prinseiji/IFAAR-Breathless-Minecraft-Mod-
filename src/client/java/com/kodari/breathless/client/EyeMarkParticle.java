package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** The mark that turns in Elijah's left eye during the stun slam burst (the stock star picture). The server sends it again every tick; the size comes in as the x speed. */
final class EyeMarkParticle extends SingleQuadParticle {
   private EyeMarkParticle(ClientLevel level, double x, double y, double z, float size, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = size;
      this.lifetime = 2;
      this.alpha = 1.0F;
      this.roll = spin(level);
      this.oRoll = this.roll - 0.3F;
   }

   /** The turn comes from the world clock so copies sent on different ticks line up. */
   private static float spin(ClientLevel level) {
      return level.getGameTime() * 0.3F;
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
         return new EyeMarkParticle(level, x, y, z, (float)xd, this.sprites.get(random));
      }
   }
}
