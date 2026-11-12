package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Odium;
import com.kodari.breathless.OdiumHusk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

/** The fake Odium: the Odium model and texture, stuck half in the ground (OdiumModel's HUSK pose). */
public class OdiumHuskRenderer extends MobRenderer<OdiumHusk, OdiumRenderState, OdiumModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/odium.png");

   public OdiumHuskRenderer(EntityRendererProvider.Context context) {
      super(context, new OdiumModel(context.bakeLayer(OdiumModel.LAYER)), 0.5F);
   }

   @Override
   public OdiumRenderState createRenderState() {
      return new OdiumRenderState();
   }

   @Override
   public void extractRenderState(OdiumHusk entity, OdiumRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.state = Odium.HUSK;
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(OdiumRenderState state) {
      return TEXTURE;
   }
}
