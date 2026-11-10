package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Carrion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class CarrionRenderer extends MobRenderer<Carrion, CarrionRenderState, CarrionModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/carrion.png");

   public CarrionRenderer(EntityRendererProvider.Context context) {
      super(context, new CarrionModel(context.bakeLayer(CarrionModel.LAYER)), 0.4F);
   }

   @Override
   public CarrionRenderState createRenderState() {
      return new CarrionRenderState();
   }

   @Override
   public void extractRenderState(Carrion entity, CarrionRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.swell = entity.swellSeconds(partialTick);
      // It never falls over: the clip does the swelling.
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(CarrionRenderState state) {
      return TEXTURE;
   }
}
