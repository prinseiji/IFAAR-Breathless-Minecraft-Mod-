package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Odium;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class OdiumRenderer extends MobRenderer<Odium, OdiumRenderState, OdiumModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/odium.png");

   public OdiumRenderer(EntityRendererProvider.Context context) {
      super(context, new OdiumModel(context.bakeLayer(OdiumModel.LAYER)), 0.5F);
   }

   @Override
   public OdiumRenderState createRenderState() {
      return new OdiumRenderState();
   }

   @Override
   public void extractRenderState(Odium entity, OdiumRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.state = entity.getState();
      state.stateSeconds = entity.stateSeconds(partialTick);
      state.hitSeconds = entity.hitSeconds(partialTick);
      state.deathSeconds = entity.deathTime > 0 ? (entity.deathTime + partialTick) / 20.0F : -1.0F;
      // It never topples over: the death clip does the falling.
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(OdiumRenderState state) {
      return TEXTURE;
   }
}
