package com.kodari.breathless.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.ExposurePayload;

public class BreathlessClient implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      ClientPlayNetworking.registerGlobalReceiver(ExposurePayload.TYPE, (payload, context) -> ClientState.exposure = payload.exposure());
      ClientTickEvents.END_CLIENT_TICK.register(ClientState::tick);

      // Under the vanilla overlays: spore haze, then the mask visor.
      HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, Breathless.id("fog_and_mask"), SporeHud::renderFogAndMask);
      // The visible exposure meter.
      HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, Breathless.id("spore_meter"), SporeHud::renderMeter);
      // Critical state: black fade, pulsing vignette, creeping veins. Drawn over the HUD, under chat.
      HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Breathless.id("spore_critical"), SporeHud::renderCritical);
   }
}
