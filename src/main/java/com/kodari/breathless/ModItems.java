package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;

public final class ModItems {
   private ModItems() {
   }

   /** Worn mask WITH a filter inside. Its durability bar is the filter's life. */
   public static Item GAS_MASK;
   /** Worn mask with no filter. Does not protect. */
   public static Item GAS_MASK_EMPTY;
   /** Loose filter. Right-click while wearing the empty mask to install. */
   public static Item GAS_MASK_FILTER;

   private static ResourceKey<Item> key(String path) {
      return ResourceKey.create(Registries.ITEM, Breathless.id(path));
   }

   /**
    * No equipment asset on purpose: with none set, the game draws the item's own 3D model on the head
    * (the "head" display transform in models/item/gas_mask_worn.json), like a carved pumpkin.
    */
   private static Equippable headEquippable() {
      return Equippable.builder(EquipmentSlot.HEAD).setDamageOnHurt(false).build();
   }

   public static void register() {
      ResourceKey<Item> maskKey = key("gas_mask");
      GAS_MASK = Registry.register(
         BuiltInRegistries.ITEM,
         maskKey,
         new Item(new Item.Properties().setId(maskKey).durability(Tuning.FILTER_MAX).component(DataComponents.EQUIPPABLE, headEquippable()))
      );

      ResourceKey<Item> emptyKey = key("gas_mask_empty");
      GAS_MASK_EMPTY = Registry.register(
         BuiltInRegistries.ITEM,
         emptyKey,
         new Item(new Item.Properties().setId(emptyKey).stacksTo(1).component(DataComponents.EQUIPPABLE, headEquippable()))
      );

      ResourceKey<Item> filterKey = key("gas_mask_filter");
      GAS_MASK_FILTER = Registry.register(
         BuiltInRegistries.ITEM,
         filterKey,
         new FilterItem(new Item.Properties().setId(filterKey).durability(Tuning.FILTER_MAX))
      );
   }
}
