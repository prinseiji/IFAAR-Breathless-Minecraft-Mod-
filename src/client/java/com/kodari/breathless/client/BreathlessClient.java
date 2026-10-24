package com.kodari.breathless.client;

import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.ExposurePayload;
import com.kodari.breathless.FiendPayload;
import com.kodari.breathless.MaskFilters;
import com.kodari.breathless.SpotPayload;
import com.kodari.breathless.OpenLorePayload;
import com.kodari.breathless.Masks;
import com.kodari.breathless.ModEntities;
import com.kodari.breathless.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import com.kodari.breathless.ModParticles;
import com.kodari.breathless.Tuning;

public class BreathlessClient implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      ModelLayerRegistry.registerModelLayer(StagnatedModel.LAYER, StagnatedModel::createBodyLayer);
      ModelLayerRegistry.registerModelLayer(StagnatedModel.ARMOR_LAYERS[0], () -> StagnatedModel.createArmorLayer(0));
      ModelLayerRegistry.registerModelLayer(StagnatedModel.ARMOR_LAYERS[1], () -> StagnatedModel.createArmorLayer(1));
      ModelLayerRegistry.registerModelLayer(StagnatedModel.ARMOR_LAYERS[2], () -> StagnatedModel.createArmorLayer(2));
      ModelLayerRegistry.registerModelLayer(StagnatedModel.ARMOR_LAYERS[3], () -> StagnatedModel.createArmorLayer(3));
      EntityRendererRegistry.register(ModEntities.STAGNATED, StagnatedRenderer::new);
      ClientPlayNetworking.registerGlobalReceiver(ExposurePayload.TYPE, (payload, context) -> ClientState.exposure = payload.exposure());
      ClientPlayNetworking.registerGlobalReceiver(FiendPayload.TYPE, (payload, context) -> ClientState.startFiend(payload.seed()));
      ClientPlayNetworking.registerGlobalReceiver(com.kodari.breathless.EliPayload.TYPE, (payload, context) -> EliBoss.ping(payload.phase(), payload.health()));
      ClientPlayNetworking.registerGlobalReceiver(SpotPayload.TYPE, (payload, context) -> SlothText.spotted());
      ClientPlayNetworking.registerGlobalReceiver(OpenLorePayload.TYPE, (payload, context) -> net.minecraft.client.Minecraft.getInstance().setScreenAndShow(new LoreScreen(payload.doc())));
      ClientTickEvents.END_CLIENT_TICK.register(ClientState::tick);
      ClientTickEvents.END_CLIENT_TICK.register(EliBoss::tick);
      HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Breathless.id("eli_bar"), EliBoss::render);

      // Under the vanilla overlays: spore haze, vision pulses, floating spores, then the mask visor.
      HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, Breathless.id("fog_and_mask"), SporeHud::renderFogAndMask);
      // The visible exposure gauge, with the F13ND pop-ups on top of it.
      HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, Breathless.id("spore_meter"), SporeHud::renderMeter);
      // Durability of the two filters, under the gauge.
      HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, Breathless.id("filter_meter"), SporeHud::renderFilters);
      // Critical state and the F13ND vignette: black fade, pulsing vignette, creeping veins. Drawn over the HUD, under chat.
      HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Breathless.id("spore_critical"), SporeHud::renderCritical);

      // 5-L0's silent, stacked text. Over the black fade so it can still be seen.
      HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Breathless.id("sloth_text"), SlothText::render);

      // The teal eyes (hanging in the air, and the ones that rush at your face).
      ParticleProviderRegistry.getInstance().register(ModParticles.SPORE_EYE, EyeParticle.SuspendProvider::new);
      ParticleProviderRegistry.getInstance().register(ModParticles.SPORE_EYE_SURGE, EyeParticle.SurgeProvider::new);

      // The flat, see-through sheet of gas at Y=0.
      LevelRenderEvents.BEFORE_GIZMOS.register(LevelSheet::render);

      // Percentage lines under the item name.
      ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> addTooltip(stack, lines));
   }

   private static ChatFormatting colorFor(int percent) {
      if (percent > 50) {
         return ChatFormatting.GREEN;
      }
      return percent > 25 ? ChatFormatting.YELLOW : ChatFormatting.RED;
   }

   private static void addTooltip(ItemStack stack, List<Component> lines) {
      int at = Math.min(1, lines.size());
      if (ModItems.isFilter(stack)) {
         int percent = Masks.percent(Tuning.FILTER_MAX - stack.getDamageValue());
         lines.add(at++, Component.translatable("breathless.tooltip.filter_life", percent).withStyle(colorFor(percent)));
         if (stack.is(ModItems.GAS_MASK_FILTER_REINFORCED)) {
            lines.add(at, Component.translatable("breathless.tooltip.filter_reinforced").withStyle(ChatFormatting.LIGHT_PURPLE));
         }
      } else if (stack.is(ModItems.TONIC_LESSER) || stack.is(ModItems.TONIC_STRONG)) {
         lines.add(at, Component.translatable("breathless.tooltip.tonic").withStyle(ChatFormatting.DARK_GRAY));
      } else if (Masks.isMask(stack)) {
         if (ModItems.isNightVision(stack)) {
            lines.add(at++, Component.translatable("breathless.tooltip.nv").withStyle(ChatFormatting.GREEN));
         }
         MaskFilters filters = Masks.getFilters(stack);
         Component header = Component.translatable("breathless.tooltip.filters", filters.count(), 2)
            .withStyle(filters.working() ? ChatFormatting.GRAY : ChatFormatting.RED);
         lines.add(at++, header);
         for (int slot = 0; slot < 2; slot++) {
            int life = slot == 0 ? filters.first() : filters.second();
            if (life > 0) {
               int percent = Masks.percent(life);
               lines.add(at++, Component.translatable("breathless.tooltip.filter_slot", slot + 1, percent).withStyle(filters.reinforced(slot) ? ChatFormatting.LIGHT_PURPLE : colorFor(percent)));
            } else {
               lines.add(at++, Component.translatable("breathless.tooltip.filter_empty", slot + 1).withStyle(ChatFormatting.DARK_GRAY));
            }
         }
         lines.add(at++, Component.translatable("breathless.tooltip.insert_hint").withStyle(ChatFormatting.DARK_GRAY));
         if (filters.count() > 0) {
            lines.add(at, Component.translatable("breathless.tooltip.remove_hint").withStyle(ChatFormatting.DARK_GRAY));
         }
      }
   }
}
