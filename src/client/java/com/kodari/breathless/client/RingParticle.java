package com.kodari.breathless.client;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** The ring of thorns around Elijah while he is enraged by parries: a flat picture that always faces you and slowly turns. Sent again every couple of ticks by the server. */
final class RingParticle extends SingleQuadParticle {
   private final int owner;

   private RingParticle(ClientLevel level, double x, double y, double z, int owner, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.owner = owner;
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = 1.45F;
      this.lifetime = 2;
      this.alpha = 0.95F;
      this.roll = (float)(Math.random() * Math.PI * 2.0);
      this.oRoll = this.roll;
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
      this.roll += 0.12F;
      // Stay on him: move to wherever he is now (the old position becomes the interpolation start).
      net.minecraft.world.entity.Entity owner = this.level.getEntity(this.owner);
      if (owner != null) {
         this.x = owner.getX();
         this.y = owner.getY() + 0.95;
         this.z = owner.getZ();
      }
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
         return new RingParticle(level, x, y, z, (int)Math.round(xd), this.sprites.get(random));
      }
   }
}
