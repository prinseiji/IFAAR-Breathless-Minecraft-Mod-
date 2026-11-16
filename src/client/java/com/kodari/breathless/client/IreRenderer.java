package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Ire;
import com.kodari.breathless.Ire;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class IreRenderer extends MobRenderer<Ire, IreRenderState, IreModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/ire.png");

   public IreRenderer(EntityRendererProvider.Context context) {
      super(context, new IreModel(context.bakeLayer(IreModel.LAYER)), 0.5F);
   }

   @Override
   public IreRenderState createRenderState() {
      return new IreRenderState();
   }

   @Override
   public void extractRenderState(Ire entity, IreRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.state = entity.getState();
      state.stateSeconds = entity.stateSeconds(partialTick);
      state.hitSeconds = entity.hitSeconds(partialTick);
      state.deathSeconds = entity.deathTime > 0 ? (entity.deathTime + partialTick) / 20.0F : -1.0F;
      // It never topples over: the death clip does the falling.
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(IreRenderState state) {
      return TEXTURE;
   }
}
