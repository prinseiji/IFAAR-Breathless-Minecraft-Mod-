package com.kodari.breathless.client;

import com.kodari.breathless.Stagnated;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/**
 * The ring of thorns around Elijah while he is enraged by parries: a flat picture that always faces you and slowly turns.
 * It is ONE particle that lives as long as the rage lasts and moves with him by itself every tick (the client makes it when the rage starts, nothing is sent along his path),
 * so there are no copies trailing behind him. The entity id travels as xd.
 */
final class RingParticle extends SingleQuadParticle {
   private final int owner;
   /** His position on the previous tick (kept here, so the ring's old position is his old position). */
   private double prevX;
   private double prevY;
   private double prevZ;
   private boolean hasPrev;

   private RingParticle(ClientLevel level, double x, double y, double z, int owner, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.owner = owner;
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = 1.45F;
      // It ends itself (see tick); this is only a very long safety limit.
      this.lifetime = 1000000;
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
      Entity found = this.level.getEntity(this.owner);
      if (!(found instanceof Stagnated eli) || eli.isRemoved() || !eli.isAlive() || !eli.isRaging()) {
         this.remove();
         return;
      }
      // Tell him the ring is alive, so a new one is made only if this one is gone.
      eli.ringSeen = this.level.getGameTime();
      // Both ends of the movement are HIS (his old and new position), so the ring moves exactly as smoothly as he does.
      double nx = eli.getX();
      double ny = eli.getY();
      double nz = eli.getZ();
      this.xo = this.hasPrev ? this.prevX : nx;
      this.yo = (this.hasPrev ? this.prevY : ny) + 0.95;
      this.zo = this.hasPrev ? this.prevZ : nz;
      this.x = nx;
      this.y = ny + 0.95;
      this.z = nz;
      this.prevX = nx;
      this.prevY = ny;
      this.prevZ = nz;
      this.hasPrev = true;
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
