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
 * One star of Elijah's rocket stock: turns around above his head, one star for every stocked rocket. It is ONE particle that lives as long as the rocket
 * is stocked and moves with him by itself every tick (no copies are sent along his path). It ends when the stock drops below its number or he is gone.
 * The server sends it once (the entity id travels as xd, the star's number as yd).
 */
final class StockStarParticle extends SingleQuadParticle {
   private static final double RADIUS = 0.7;
   private final int owner;
   private final int index;
   /** Where on the circle this star sits relative to the others; it eases toward its place when the count changes. */
   private double offset;
   private double orbX;
   private double orbZ;
   private double oOrbX;
   private double oOrbZ;
   private double bob;
   private double oBob;
   /** His position on the previous tick (kept here, so the star's old position is his old position). */
   private double prevX;
   private double prevY;
   private double prevZ;
   private boolean hasPrev;

   private StockStarParticle(ClientLevel level, double x, double y, double z, int owner, int index, TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.owner = owner;
      this.index = index;
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize = 0.24F;
      // It ends itself (see tick); this is only a very long safety limit.
      this.lifetime = 1000000;
      this.alpha = 1.0F;
      this.roll = 0.0F;
      this.oRoll = 0.0F;
      this.offset = index * Math.PI * 2.0 / 5.0;
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
      Entity found = this.level.getEntity(this.owner);
      if (!(found instanceof Stagnated eli) || eli.isRemoved() || !eli.isAlive() || eli.stockShown <= this.index) {
         this.remove();
         return;
      }
      // Tell him this star is alive (a missing one gets made again when the count is sent).
      eli.starSeen[this.index] = this.level.getGameTime();
      int count = Math.max(1, eli.stockShown);
      // Ease toward this star's even place on the circle, so the others slide over when one is added or fired.
      double want = this.index * Math.PI * 2.0 / count;
      double diff = want - this.offset;
      diff -= Math.PI * 2.0 * Math.floor((diff + Math.PI) / (Math.PI * 2.0));
      this.offset += diff * 0.15;
      double spin = this.level.getGameTime() * 0.09;
      this.oOrbX = this.orbX;
      this.oOrbZ = this.orbZ;
      this.oBob = this.bob;
      this.orbX = Math.cos(spin + this.offset) * RADIUS;
      this.orbZ = Math.sin(spin + this.offset) * RADIUS;
      this.bob = Math.sin(this.level.getGameTime() * 0.12 + this.index) * 0.06;
      double top = eli.getBbHeight() + 0.6;
      // The old position is HIS old position too, so the star moves exactly as smoothly as he does (no trail of copies).
      double nx = eli.getX();
      double ny = eli.getY();
      double nz = eli.getZ();
      this.xo = (this.hasPrev ? this.prevX : nx) + this.oOrbX;
      this.yo = (this.hasPrev ? this.prevY : ny) + top + this.oBob;
      this.zo = (this.hasPrev ? this.prevZ : nz) + this.oOrbZ;
      this.x = nx + this.orbX;
      this.y = ny + top + this.bob;
      this.z = nz + this.orbZ;
      this.prevX = nx;
      this.prevY = ny;
      this.prevZ = nz;
      this.hasPrev = true;
      this.roll += 0.06F;
   }

   static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final FabricSpriteSet sprites;

      Provider(FabricSpriteSet sprites) {
         this.sprites = sprites;
      }

      @Override
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
         return new StockStarParticle(level, x, y, z, (int)Math.round(xd), (int)Math.round(yd), this.sprites.get(random));
      }
   }
}
