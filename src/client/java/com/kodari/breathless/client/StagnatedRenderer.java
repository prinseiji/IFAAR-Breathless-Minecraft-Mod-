package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Stagnated;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

public class StagnatedRenderer extends MobRenderer<Stagnated, StagnatedRenderState, StagnatedModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/stagnated.png");

   public StagnatedRenderer(EntityRendererProvider.Context context) {
      super(context, new StagnatedModel(context.bakeLayer(StagnatedModel.LAYER)), 0.5F);
      StagnatedModel[] armorModels = new StagnatedModel[4];
      for (int i = 0; i < 4; i++) {
         armorModels[i] = new StagnatedModel(context.bakeLayer(StagnatedModel.ARMOR_LAYERS[i]));
      }
      this.addLayer(new StagnatedArmorLayer(this, armorModels, context.getEquipmentRenderer()));
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
      state.armoring = entity.armoringClock >= 0 ? (entity.armoringClock + partialTick) / 20.0F : -1.0F;
      state.armor[0] = entity.getItemBySlot(EquipmentSlot.HEAD).copy();
      state.armor[1] = entity.getItemBySlot(EquipmentSlot.CHEST).copy();
      state.armor[2] = entity.getItemBySlot(EquipmentSlot.LEGS).copy();
      state.armor[3] = entity.getItemBySlot(EquipmentSlot.FEET).copy();
      state.spot = entity.spotClock >= 0 ? (entity.spotClock + partialTick) / 20.0F : -1.0F;
   }

   @Override
   public Identifier getTextureLocation(StagnatedRenderState state) {
      return TEXTURE;
   }
}
