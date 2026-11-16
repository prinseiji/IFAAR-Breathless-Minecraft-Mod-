package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Ire;
import com.kodari.breathless.IreHusk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class IreHuskRenderer extends MobRenderer<IreHusk, IreRenderState, IreModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/ire.png");

   public IreHuskRenderer(EntityRendererProvider.Context context) {
      super(context, new IreModel(context.bakeLayer(IreModel.LAYER)), 0.5F);
   }

   @Override
   public IreRenderState createRenderState() {
      return new IreRenderState();
   }

   @Override
   public void extractRenderState(IreHusk entity, IreRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.state = Ire.HUSK;
      // It never topples over: the death clip does the falling.
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(IreRenderState state) {
      return TEXTURE;
   }
}
