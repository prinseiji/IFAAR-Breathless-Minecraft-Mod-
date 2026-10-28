package com.kodari.breathless;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

/**
 * IFAAR: Breathless. An expansion for IFAAR (mod id "combatinjuries").
 * Below Y=0 the air is full of sculk spores. See DEVELOPMENT.md.
 */
public class Breathless implements ModInitializer {
   public static final String MOD_ID = "breathless";

   /** The player's spore exposure, 0..100. Saved with the player. */
   public static final AttachmentType<Float> EXPOSURE = AttachmentRegistry.createPersistent(id("spore_exposure"), Codec.FLOAT);

   public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath(MOD_ID, path);
   }

   @Override
   public void onInitialize() {
      ModSounds.register();
      ModEffects.register();
      ModEntities.register();
      ModParticles.register();
      ModBlocks.register();
      ModWorldgen.register();
      ModItems.register();
      PayloadTypeRegistry.clientboundPlay().register(ExposurePayload.TYPE, ExposurePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(FiendPayload.TYPE, FiendPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(SpotPayload.TYPE, SpotPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(EliPayload.TYPE, EliPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ParryPayload.TYPE, ParryPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CaptionPayload.TYPE, CaptionPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(OpenLorePayload.TYPE, OpenLorePayload.CODEC);
      FilterClicks.init();

      Hazard.init();
      Achievements.init();
      Lore.init();
      MobRules.init();
      EliSummon.init();

      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> entries.accept(ModBlocks.SCULK_NIDUS));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
         entries.accept(ModItems.GAS_MASK_EMPTY);
         entries.accept(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX)));
         entries.accept(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX, true, true)));
         entries.accept(ModItems.GAS_MASK_NV_EMPTY);
         entries.accept(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX), true));
         entries.accept(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX, true, true), true));
         entries.accept(ModItems.GAS_MASK_FILTER);
         entries.accept(ModItems.GAS_MASK_FILTER_REINFORCED);
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> entries.accept(ModItems.ELI_SWORD));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(ModItems.ROTTING_HEART));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(ModItems.STAGNATED_HEAD));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
         entries.accept(ModItems.AMETHYST_DUST);
         entries.accept(ModItems.CHARCOAL_DUST);
         for (net.minecraft.world.item.Item doc : ModItems.LORE) {
            entries.accept(doc);
         }
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
         entries.accept(ModItems.TONIC_LESSER);
         entries.accept(ModItems.TONIC_STRONG);
      });

      registerCommands();
   }

   /**
    * Test commands:
    *   /breathless exposure <0-100>   set your meter
    *   /breathless role <0-2>         set the role of Stagnated within 16 blocks: 0 none, 1 soldier, 2 excavator
    *   /breathless weapon <0-2>       make the Stagnated within 16 blocks soldiers with a weapon: 0 none, 1 iron sword, 2 stone axe
   *   /breathless give               masks (plain, nearly dead, night vision reinforced), loose filters (plain and reinforced) and tonics
    *   /breathless lorereset          forget every lore document you read (locks their recipes again)
    *   /breathless fiend              play the F13ND pop-up sequence (no damage, no reset)
    */
   private static void registerCommands() {
      CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> dispatcher.register(
         Commands.literal("breathless")
            .then(Commands.literal("exposure").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 100.0F)).executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               float value = FloatArgumentType.getFloat(context, "value");
               Hazard.setExposure(player, value);
               Hazard.forceSync(player);
               context.getSource().sendSuccess(() -> Component.literal("Spore exposure set to " + value), false);
               return 1;
            })))
            .then(Commands.literal("role").then(Commands.argument("value", IntegerArgumentType.integer(0, 2)).executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               int value = IntegerArgumentType.getInteger(context, "value");
               var list = ((net.minecraft.server.level.ServerLevel)player.level()).getEntitiesOfClass(Stagnated.class, player.getBoundingBox().inflate(16.0));
               list.forEach(s -> s.setRole(value));
               context.getSource().sendSuccess(() -> Component.literal("Set the role of " + list.size() + " Stagnated nearby to " + value + " (0 none, 1 soldier, 2 excavator)"), false);
               return list.size();
            })))
            .then(Commands.literal("weapon").then(Commands.argument("value", IntegerArgumentType.integer(0, 2)).executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               int value = IntegerArgumentType.getInteger(context, "value");
               var list = ((net.minecraft.server.level.ServerLevel)player.level()).getEntitiesOfClass(Stagnated.class, player.getBoundingBox().inflate(16.0));
               list.forEach(s -> s.setWeapon(value));
               context.getSource().sendSuccess(() -> Component.literal("Made " + list.size() + " Stagnated nearby soldiers with weapon " + value + " (0 none, 1 iron sword, 2 stone axe)"), false);
               return list.size();
            })))
            .then(Commands.literal("sweaty").executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               var level = (net.minecraft.server.level.ServerLevel)player.level();
               Stagnated mob = ModEntities.STAGNATED.spawn(level, player.blockPosition().relative(player.getDirection(), 6), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
               if (mob == null) {
                  return 0;
               }
               mob.makeSweaty();
               context.getSource().sendSuccess(() -> Component.literal("Spawned the sweaty Stagnated."), false);
               return 1;
            }))
            .then(Commands.literal("enrage").executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               var list = ((net.minecraft.server.level.ServerLevel)player.level()).getEntitiesOfClass(Stagnated.class, player.getBoundingBox().inflate(32.0));
               list.forEach(Stagnated::forceEnrage);
               context.getSource().sendSuccess(() -> Component.literal("Sent " + list.size() + " Stagnated nearby into phase two (only sweaty ones react)."), false);
               return list.size();
            }))
            .then(Commands.literal("give").executes(context -> {
               ServerPlayer player = context.getSource().getPlayerOrException();
               player.getInventory().add(new ItemStack(ModItems.GAS_MASK_EMPTY));
               player.getInventory().add(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX)));
               player.getInventory().add(ModItems.maskWith(new MaskFilters(150, 150)));
               player.getInventory().add(ModItems.filterWith(Tuning.FILTER_MAX));
               player.getInventory().add(ModItems.filterWith(Tuning.FILTER_MAX));
               player.getInventory().add(ModItems.filterWith(150));
               player.getInventory().add(ModItems.maskWith(new MaskFilters(Tuning.FILTER_MAX, Tuning.FILTER_MAX, true, true), true));
               player.getInventory().add(ModItems.filterWith(Tuning.FILTER_MAX, true));
               player.getInventory().add(ModItems.filterWith(Tuning.FILTER_MAX, true));
               player.getInventory().add(new ItemStack(ModItems.STAGNATED_HEAD, 4));
               player.getInventory().add(new ItemStack(ModItems.TONIC_LESSER, 4));
               player.getInventory().add(new ItemStack(ModItems.TONIC_STRONG, 4));
               for (net.minecraft.world.item.Item doc : ModItems.LORE) {
                  player.getInventory().add(new ItemStack(doc));
               }
               context.getSource().sendSuccess(() -> Component.literal("Gave gas masks, filters and tonics."), false);
               return 1;
            }))
            .then(Commands.literal("lorereset").executes(context -> {
               Lore.reset(context.getSource().getPlayerOrException());
               context.getSource().sendSuccess(() -> Component.literal("Forgot all lore documents (the recipes are locked again, and the documents can be read as new)."), false);
               return 1;
            }))
            .then(Commands.literal("fiend").executes(context -> {
               Hazard.testFiend(context.getSource().getPlayerOrException());
               context.getSource().sendSuccess(() -> Component.literal("F13ND sequence started (test)."), false);
               return 1;
            }))
      ));
   }
}
