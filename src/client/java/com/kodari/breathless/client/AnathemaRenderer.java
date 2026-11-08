package com.kodari.breathless.client;

import com.kodari.breathless.Anathema;
import com.kodari.breathless.Breathless;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class AnathemaRenderer extends MobRenderer<Anathema, AnathemaRenderState, AnathemaModel> {
   private static final Identifier ALIVE = Breathless.id("textures/entity/anathema.png");
   private static final Identifier DEAD = Breathless.id("textures/entity/anathema_dead.png");

   public AnathemaRenderer(EntityRendererProvider.Context context) {
      super(context, new AnathemaModel(context.bakeLayer(AnathemaModel.LAYER)), 0.0F);
   }

   @Override
   public AnathemaRenderState createRenderState() {
      return new AnathemaRenderState();
   }

   @Override
   public void extractRenderState(Anathema entity, AnathemaRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.hit = entity.hurtTime > 0 && entity.deathTime <= 0 ? (entity.hurtDuration - entity.hurtTime + partialTick) / 20.0F : -1.0F;
      state.dying = entity.deathTime > 0 ? (entity.deathTime + partialTick) / 20.0F : -1.0F;
      // It does not fall over when it dies: the death clip does the dying.
      state.deathTime = 0.0F;
   }

   @Override
   public Identifier getTextureLocation(AnathemaRenderState state) {
      return state.dying >= 2.67F ? DEAD : ALIVE;
   }
}
