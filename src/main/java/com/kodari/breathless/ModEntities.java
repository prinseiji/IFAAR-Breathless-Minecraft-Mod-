package com.kodari.breathless;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
   public static EntityType<Stagnated> STAGNATED;
   public static EntityType<Anathema> ANATHEMA;
   public static EntityType<Carrion> CARRION;
   public static EntityType<Odium> ODIUM;
   public static EntityType<OdiumHusk> ODIUM_HUSK;

   private ModEntities() {
   }

   public static void register() {
      ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Breathless.id("stagnated"));
      STAGNATED = Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
         EntityType.Builder.of(Stagnated::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.7F).clientTrackingRange(8).build(key));
      FabricDefaultAttributeRegistry.register(STAGNATED, Stagnated.createAttributes());
      ResourceKey<EntityType<?>> anathemaKey = ResourceKey.create(Registries.ENTITY_TYPE, Breathless.id("anathema"));
      ANATHEMA = Registry.register(BuiltInRegistries.ENTITY_TYPE, anathemaKey,
         EntityType.Builder.of(Anathema::new, MobCategory.MISC).sized(1.8F, 2.4F).eyeHeight(1.9F).clientTrackingRange(10).build(anathemaKey));
      FabricDefaultAttributeRegistry.register(ANATHEMA, Anathema.createAttributes());
      ResourceKey<EntityType<?>> carrionKey = ResourceKey.create(Registries.ENTITY_TYPE, Breathless.id("carrion"));
      CARRION = Registry.register(BuiltInRegistries.ENTITY_TYPE, carrionKey,
         EntityType.Builder.of(Carrion::new, MobCategory.MISC).sized(1.4F, 1.7F).eyeHeight(1.2F).clientTrackingRange(10).build(carrionKey));
      FabricDefaultAttributeRegistry.register(CARRION, Carrion.createAttributes());
      ResourceKey<EntityType<?>> odiumKey = ResourceKey.create(Registries.ENTITY_TYPE, Breathless.id("odium"));
      ODIUM = Registry.register(BuiltInRegistries.ENTITY_TYPE, odiumKey,
         EntityType.Builder.of(Odium::new, MobCategory.MONSTER).sized(1.0F, 1.7F).eyeHeight(1.4F).clientTrackingRange(10).build(odiumKey));
      FabricDefaultAttributeRegistry.register(ODIUM, Odium.createAttributes());
      ResourceKey<EntityType<?>> huskKey = ResourceKey.create(Registries.ENTITY_TYPE, Breathless.id("odium_husk"));
      ODIUM_HUSK = Registry.register(BuiltInRegistries.ENTITY_TYPE, huskKey,
         EntityType.Builder.of(OdiumHusk::new, MobCategory.MISC).sized(1.0F, 1.1F).eyeHeight(0.9F).clientTrackingRange(10).build(huskKey));
      FabricDefaultAttributeRegistry.register(ODIUM_HUSK, OdiumHusk.createAttributes());
      // Tapping the jump key while held by an Odium (client to server).
      PayloadTypeRegistry.serverboundPlay().register(StrugglePayload.TYPE, StrugglePayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(StrugglePayload.TYPE, (payload, context) -> Odium.struggle(context.player()));
      // Nobody stays "held" across a server stopping.
      ServerLifecycleEvents.SERVER_STOPPED.register(server -> Odium.HELD.clear());
      // The gas clouds and sculk spread that Carrions leave behind tick on the server.
      GasClouds.init();
   }
}
