package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * The teal eyes. Two flavours that share the three textures:
 * - SUSPEND: small, hanging in the air, drifting a hair, fading in and out.
 * - SURGE: rush toward a point (just in front of your face), ease in, then fade right before arriving.
 * Both are drawn at full brightness so they glow in the dark.
 */
final class EyeParticle extends SingleQuadParticle {
   private final boolean surge;
   /** Surge only: the point it flies to, and where it started relative to that point. */
   private final double targetX;
   private final double targetY;
   private final double targetZ;
   private final double offsetX;
   private final double offsetY;
   private final double offsetZ;

   private EyeParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite, boolean surge) {
      super(level, surge ? x + xd : x, surge ? y + yd : y, surge ? z + zd : z, sprite);
      this.surge = surge;
      this.targetX = x;
      this.targetY = y;
      this.targetZ = z;
      this.offsetX = surge ? xd : 0.0D;
      this.offsetY = surge ? yd : 0.0D;
      this.offsetZ = surge ? zd : 0.0D;
      this.hasPhysics = false;
      this.alpha = 0.0F;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (surge) {
         this.xd = 0.0D;
         this.yd = 0.0D;
         this.zd = 0.0D;
         this.quadSize = 0.07F + this.random.nextFloat() * 0.04F;
         this.lifetime = 14;
      } else {
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.quadSize = 0.03F + this.random.nextFloat() * 0.025F;
         this.lifetime = 50 + this.random.nextInt(60);
      }
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
      float t = this.age / (float)this.lifetime;
      if (this.surge) {
         // Fast at first, slowing as it nears the target (the remaining distance shrinks with the 4th power).
         float f = 1.0F - t;
         double k = (double)f * f * f * f;
         this.x = this.targetX + this.offsetX * k;
         this.y = this.targetY + this.offsetY * k;
         this.z = this.targetZ + this.offsetZ * k;
         this.alpha = Math.min(1.0F, this.age / 3.0F) * (t < 0.8F ? 1.0F : Math.max(0.0F, (1.0F - t) / 0.2F));
      } else {
         this.x += this.xd;
         this.y += this.yd;
         this.z += this.zd;
         // fade in, hang, fade out
         this.alpha = Mth.clamp(Math.min(t / 0.2F, (1.0F - t) / 0.3F), 0.0F, 1.0F) * 0.85F;
      }
   }

   /** Small eyes hanging in the air. */
   static final class SuspendProvider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      SuspendProvider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new EyeParticle(level, x, y, z, xd, yd, zd, this.sprites.get(random), false);
      }
   }

   /** Eyes that rush toward the target point; the velocity arguments are the starting offset from that point. */
   static final class SurgeProvider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      SurgeProvider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new EyeParticle(level, x, y, z, xd, yd, zd, this.sprites.get(random), true);
      }
   }
}
