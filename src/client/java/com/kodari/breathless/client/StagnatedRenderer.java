package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Stagnated;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class StagnatedRenderer extends MobRenderer<Stagnated, StagnatedRenderState, StagnatedModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/stagnated.png");

   public StagnatedRenderer(EntityRendererProvider.Context context) {
      super(context, new StagnatedModel(context.bakeLayer(StagnatedModel.LAYER)), 0.5F);
   }

   @Override
   public StagnatedRenderState createRenderState() {
      return new StagnatedRenderState();
   }

   @Override
   public void extractRenderState(Stagnated entity, StagnatedRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.aggressive = entity.isAggressive();
      state.attack = entity.getAttackAnim(partialTick);
      state.hurt = entity.hurtTime > 0 ? 1.0F - Math.max(0.0F, (entity.hurtTime - partialTick) / 10.0F) : -1.0F;
      state.emerge = entity.emergeClock >= 0 ? (entity.emergeClock + partialTick) / 20.0F : -1.0F;
      state.spot = entity.spotClock >= 0 ? (entity.spotClock + partialTick) / 20.0F : -1.0F;
   }

   @Override
   public Identifier getTextureLocation(StagnatedRenderState state) {
      return TEXTURE;
   }
}
