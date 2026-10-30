package com.kodari.breathless.client;

import com.kodari.breathless.Stagnated;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/**
 * Elijah's blood wings on Hard: a flat picture standing behind his back, turning with his body. There are two of them back to back (side 0 faces out behind him,
 * side 1 faces the other way), so the wings can be seen from the front as well. Each is ONE particle that follows him by itself every tick and ends with the fight.
 * The entity id travels as xd, the side as yd.
 */
final class WingsParticle extends SingleQuadParticle {
   private static final double BEHIND = 0.32;
   private final int owner;
   private final boolean front;
   private double prevX;
   private double prevY;
   private double prevZ;
   private boolean hasPrev;
   /** His body's turn (degrees) on this tick and the one before. */
   private float yaw;
   private float oYaw;

   private WingsParticle(ClientLevel level, double x, double y, double z, int owner, boolean front, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.owner = owner;
      this.front = front;
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = 1.9F;
      // It ends itself (see tick); this is only a very long safety limit.
      this.lifetime = 1000000;
      this.alpha = 0.92F;
   }

   @Override
   public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
      // Standing upright, turned the way his back faces (and the twin the other way), not toward the camera.
      return (rotation, camera, partialTick) -> {
         float deg = Mth.rotLerp((float)partialTick, this.oYaw, this.yaw);
         rotation.rotationY((float)(Math.PI - Math.toRadians(deg) + (this.front ? Math.PI : 0.0)));
      };
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
      Entity found = this.level.getEntity(this.owner);
      if (!(found instanceof Stagnated eli) || eli.isRemoved() || !eli.wantsBloodWings()) {
         this.remove();
         return;
      }
      // Tell him the wings are alive, so new ones are made only if these are gone.
      eli.wingsSeen = this.level.getGameTime();
      this.oYaw = this.hasPrev ? this.yaw : eli.getYRot();
      this.yaw = eli.getYRot();
      double rad = Math.toRadians(this.yaw);
      // Behind his back (he faces (-sin, cos)); the twin sits a hair further out so the two never share a plane.
      double back = BEHIND + (this.front ? 0.01 : 0.0);
      double bx = Math.sin(rad) * back;
      double bz = -Math.cos(rad) * back;
      double nx = eli.getX();
      double ny = eli.getY() + 1.45;
      double nz = eli.getZ();
      this.xo = this.hasPrev ? this.prevX : nx + bx;
      this.yo = this.hasPrev ? this.prevY : ny;
      this.zo = this.hasPrev ? this.prevZ : nz + bz;
      this.x = nx + bx;
      this.y = ny;
      this.z = nz + bz;
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.hasPrev = true;
   }

   static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      Provider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new WingsParticle(level, x, y, z, (int)Math.round(xd), Math.round(yd) == 1L, this.sprites.get(random));
      }
   }
}
