package com.kodari.breathless.client;

import com.kodari.breathless.Tuning;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/** The star that turns in Elijah's left eye during the stun slam burst. It is spawned on the client every tick with the entity id as its x velocity and the size as its y velocity, and it moves with the entity. */
final class EyeMarkParticle extends SingleQuadParticle {
   private final int entityId;

   private EyeMarkParticle(ClientLevel level, double x, double y, double z, int entityId, float size, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.entityId = entityId;
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

   /** The turn comes from the world clock so copies spawned on different ticks line up. */
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
         return;
      }
      Entity entity = this.level.getEntity(this.entityId);
      if (entity == null || entity.isRemoved()) {
         this.remove();
         return;
      }
      double hr = Math.toRadians(entity.getYHeadRot());
      this.x = entity.getX() + Math.cos(hr) * Tuning.EYE_MARK_SIDE - Math.sin(hr) * Tuning.EYE_MARK_FORWARD;
      this.z = entity.getZ() + Math.sin(hr) * Tuning.EYE_MARK_SIDE + Math.cos(hr) * Tuning.EYE_MARK_FORWARD;
      this.y = entity.getY() + Tuning.EYE_MARK_UP;
   }

   static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      Provider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new EyeMarkParticle(level, x, y, z, (int)xd, (float)yd, this.sprites.get(random));
      }
   }
}
