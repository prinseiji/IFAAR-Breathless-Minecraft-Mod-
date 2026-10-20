package com.kodari.breathless.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/** Draws the Stagnated's armor on shells that share its (hunched, animated) skeleton. */
public class StagnatedArmorLayer extends RenderLayer<StagnatedRenderState, StagnatedModel> {
   private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

   private final StagnatedModel[] models;
   private final EquipmentLayerRenderer renderer;

   public StagnatedArmorLayer(RenderLayerParent<StagnatedRenderState, StagnatedModel> parent, StagnatedModel[] models, EquipmentLayerRenderer renderer) {
      super(parent);
      this.models = models;
      this.renderer = renderer;
   }

   @Override
   public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, StagnatedRenderState state, float yRot, float xRot) {
      for (int i = 0; i < 4; i++) {
         ItemStack stack = state.armor[i];
         if (stack.isEmpty()) {
            continue;
         }
         Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
         if (equippable == null || equippable.slot() != SLOTS[i] || equippable.assetId().isEmpty()) {
            continue;
         }
         StagnatedModel model = this.models[i];
         model.setupAnim(state);
         EquipmentClientInfo.LayerType type = i == 2 ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
         this.renderer.renderLayers(type, equippable.assetId().get(), model, state, stack, poseStack, collector, light, state.outlineColor);
      }
   }
}
