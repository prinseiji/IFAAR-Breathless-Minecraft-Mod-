package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Stagnated;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class StagnatedRenderer extends MobRenderer<Stagnated, StagnatedRenderState, StagnatedModel> {
   private static final Identifier TEXTURE = Breathless.id("textures/entity/stagnated.png");
   private static final Identifier ELIJAH = Breathless.id("textures/entity/elijah.png");

   public StagnatedRenderer(EntityRendererProvider.Context context) {
      super(context, new StagnatedModel(context.bakeLayer(StagnatedModel.LAYER)), 0.5F);
      StagnatedModel[] armorModels = new StagnatedModel[4];
      for (int i = 0; i < 4; i++) {
         armorModels[i] = new StagnatedModel(context.bakeLayer(StagnatedModel.ARMOR_LAYERS[i]));
      }
      this.addLayer(new ItemInHandLayer<>(this));
      this.addLayer(new StagnatedArmorLayer(this, armorModels, context.getEquipmentRenderer()));
   }

   @Override
   public StagnatedRenderState createRenderState() {
      return new StagnatedRenderState();
   }

   @Override
   public void extractRenderState(Stagnated entity, StagnatedRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTick);
      // Everything it carries is held in the LEFT hand (its dominant hand): the soldier's weapon (main hand) or the excavator's tool (off hand).
      ItemStack held = entity.getOffhandItem().isEmpty() ? entity.getMainHandItem() : entity.getOffhandItem();
      if (entity.getOffhandItem().is(net.minecraft.world.item.Items.SHIELD)) {
         // His shield is in the right hand, the sword stays in the left.
         ItemStack shield = entity.getOffhandItem();
         held = entity.getMainHandItem();
         state.rightHandItemStack = shield;
         this.itemModelResolver.updateForLiving(state.rightHandItemState, shield, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
      } else {
         state.rightHandItemStack = ItemStack.EMPTY;
         state.rightHandItemState.clear();
      }
      state.leftHandItemStack = held;
      this.itemModelResolver.updateForLiving(state.leftHandItemState, held, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
      state.aggressive = entity.isAggressive();
      state.attack = entity.getAttackAnim(partialTick);
      state.hurt = entity.hurtTime > 0 ? 1.0F - Math.max(0.0F, (entity.hurtTime - partialTick) / 10.0F) : -1.0F;
      state.emerge = entity.emergeClock >= 0 ? (entity.emergeClock + partialTick) / 20.0F : -1.0F;
      state.armoring = entity.armoringClock >= 0 ? (entity.armoringClock + partialTick) / 20.0F : -1.0F;
      state.armor[0] = entity.getItemBySlot(EquipmentSlot.HEAD).copy();
      state.armor[1] = entity.getItemBySlot(EquipmentSlot.CHEST).copy();
      state.armor[2] = entity.getItemBySlot(EquipmentSlot.LEGS).copy();
      state.armor[3] = entity.getItemBySlot(EquipmentSlot.FEET).copy();
      state.mining = entity.mineClock >= 0 ? (entity.mineClock + partialTick) / 20.0F : -1.0F;
      state.placing = entity.placeClock >= 0 ? (entity.placeClock + partialTick) / 20.0F : -1.0F;
      state.shield = entity.shieldClock >= 0 ? (entity.shieldClock + partialTick) / 20.0F : -1.0F;
      state.spotArmed = entity.spotArmedClock >= 0 ? (entity.spotArmedClock + partialTick) / 20.0F : -1.0F;
      state.unarm = entity.unarmClock >= 0 ? (entity.unarmClock + partialTick) / 20.0F : -1.0F;
      state.wind = entity.windClock >= 0 ? (entity.windClock + partialTick) / 20.0F : -1.0F;
      state.smash = entity.smashClock >= 0 ? (entity.smashClock + partialTick) / 20.0F : -1.0F;
      state.clutch = entity.clutchClock >= 0 ? (entity.clutchClock + partialTick) / 20.0F : -1.0F;
      state.flying = entity.flying;
      state.enrage = entity.enrageClock >= 0 ? (entity.enrageClock + partialTick) / 20.0F : -1.0F;
      state.shieldUp = entity.shieldUpClock >= 0 ? (entity.shieldUpClock + partialTick) / 20.0F : -1.0F;
      state.shieldDis = entity.shieldDisClock >= 0 ? (entity.shieldDisClock + partialTick) / 20.0F : -1.0F;
      state.shieldHit = entity.shieldHitClock >= 0 ? (entity.shieldHitClock + partialTick) / 20.0F : -1.0F;
      state.guard = entity.getOffhandItem().is(net.minecraft.world.item.Items.SHIELD);
      state.elijah = entity.isElijah();
      state.spot = entity.spotClock >= 0 ? (entity.spotClock + partialTick) / 20.0F : -1.0F;
   }

   @Override
   public Identifier getTextureLocation(StagnatedRenderState state) {
      return state.elijah ? ELIJAH : TEXTURE;
   }
}
