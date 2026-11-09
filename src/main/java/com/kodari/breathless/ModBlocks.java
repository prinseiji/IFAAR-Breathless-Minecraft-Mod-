package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** The blocks of the mod. Registered before the items so the block items can point at them. */
public final class ModBlocks {
   private ModBlocks() {
   }

   /** The Sculk Nidus: wakes Stagnateds nearby, and drops Rotting Hearts when broken. */
   public static Block SCULK_NIDUS;

   public static void register() {
      ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Breathless.id("sculk_nidus"));
      SCULK_NIDUS = Registry.register(BuiltInRegistries.BLOCK, blockKey, new SculkNidusBlock(BlockBehaviour.Properties.of().setId(blockKey).strength(3.0F, 3.0F).sound(SoundType.SCULK_CATALYST)));
      ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Breathless.id("sculk_nidus"));
      Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(SCULK_NIDUS, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
   }
}
