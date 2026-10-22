package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.equipment.Equippable;

public final class ModItems {
   private ModItems() {
   }

   /** What is inside a mask (two filter slots). Absent = no filters. */
   public static DataComponentType<MaskFilters> FILTERS;

   // Six masks. They differ only in which picture they show and (for the night vision ones) the night vision lens:
   //   plain / reinforced / empty, and the same three with the green night vision lens.
   // The game picks the right one for you whenever the filters inside change (see maskWith).
   /** Worn mask with ordinary filters inside (yellow band). */
   public static Item GAS_MASK;
   /** Worn mask with no filters. Does not protect. */
   public static Item GAS_MASK_EMPTY;
   /** Worn mask with only reinforced filters inside (purple band). */
   public static Item GAS_MASK_REINFORCED;
   public static Item GAS_MASK_NV;
   public static Item GAS_MASK_NV_EMPTY;
   public static Item GAS_MASK_NV_REINFORCED;

   /** Loose filter. Click it onto a mask (or right-click it while wearing a mask) to insert it. */
   public static Item GAS_MASK_FILTER;
   /** Reinforced filter: wears out half as fast. */
   public static Item GAS_MASK_FILTER_REINFORCED;

   public static Item STAGNATED_HEAD;
   public static Item AMETHYST_DUST;
   public static Item CHARCOAL_DUST;

   public static Item TONIC_LESSER;
   public static Item TONIC_STRONG;

   /** The lore documents, in the order of Lore.DOCS. */
   public static final Item[] LORE = new Item[Lore.DOCS.length];

   private static ResourceKey<Item> key(String path) {
      return ResourceKey.create(Registries.ITEM, Breathless.id(path));
   }

   /**
    * No equipment asset on purpose: with none set, the game draws the item's own 3D model on the head
    * (the "head" display transform in models/item/gas_mask_worn*.json), like a carved pumpkin.
    */
   private static Equippable headEquippable() {
      return Equippable.builder(EquipmentSlot.HEAD).setDamageOnHurt(false).build();
   }

   private static Item registerMask(String name) {
      ResourceKey<Item> maskKey = key(name);
      return Registry.register(
         BuiltInRegistries.ITEM,
         maskKey,
         new Item(new Item.Properties().setId(maskKey).stacksTo(1).component(DataComponents.EQUIPPABLE, headEquippable()))
      );
   }

   private static Item registerPlain(String name) {
      ResourceKey<Item> itemKey = key(name);
      return Registry.register(BuiltInRegistries.ITEM, itemKey, new Item(new Item.Properties().setId(itemKey)));
   }

   private static Item registerTonic(String name, float purge) {
      ResourceKey<Item> itemKey = key(name);
      Consumable drink = Consumable.builder()
         .consumeSeconds(1.6F)
         .animation(ItemUseAnimation.DRINK)
         .sound(SoundEvents.HONEY_DRINK)
         .hasConsumeParticles(false)
         .build();
      return Registry.register(
         BuiltInRegistries.ITEM,
         itemKey,
         new TonicItem(new Item.Properties().setId(itemKey).stacksTo(16).usingConvertsTo(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, drink), purge)
      );
   }

   public static void register() {
      FILTERS = Registry.register(
         BuiltInRegistries.DATA_COMPONENT_TYPE,
         Breathless.id("filters"),
         DataComponentType.<MaskFilters>builder().persistent(MaskFilters.CODEC).networkSynchronized(MaskFilters.STREAM_CODEC).build()
      );

      GAS_MASK = registerMask("gas_mask");
      GAS_MASK_EMPTY = registerMask("gas_mask_empty");
      GAS_MASK_REINFORCED = registerMask("gas_mask_reinforced");
      GAS_MASK_NV = registerMask("gas_mask_nv");
      GAS_MASK_NV_EMPTY = registerMask("gas_mask_nv_empty");
      GAS_MASK_NV_REINFORCED = registerMask("gas_mask_nv_reinforced");

      ResourceKey<Item> filterKey = key("gas_mask_filter");
      GAS_MASK_FILTER = Registry.register(
         BuiltInRegistries.ITEM,
         filterKey,
         new FilterItem(new Item.Properties().setId(filterKey).durability(Tuning.FILTER_MAX), false)
      );
      ResourceKey<Item> reinforcedKey = key("gas_mask_filter_reinforced");
      GAS_MASK_FILTER_REINFORCED = Registry.register(
         BuiltInRegistries.ITEM,
         reinforcedKey,
         new FilterItem(new Item.Properties().setId(reinforcedKey).durability(Tuning.FILTER_MAX), true)
      );

      ResourceKey<Item> headKey = key("stagnated_head");
      STAGNATED_HEAD = Registry.register(BuiltInRegistries.ITEM, headKey, new StagnatedHeadItem(new Item.Properties().setId(headKey)));
      AMETHYST_DUST = registerPlain("amethyst_dust");
      CHARCOAL_DUST = registerPlain("charcoal_dust");

      TONIC_LESSER = registerTonic("tonic_lesser", Tuning.TONIC_LESSER_PURGE);
      TONIC_STRONG = registerTonic("tonic_strong", Tuning.TONIC_STRONG_PURGE);
      for (int i = 0; i < Lore.DOCS.length; i++) {
         ResourceKey<Item> loreKey = key("lore_" + Lore.DOCS[i].id());
         Item.Properties props = new Item.Properties().setId(loreKey).stacksTo(1);
         if (Lore.DOCS[i].id().equals("howard")) {
            // The recovered letter has a red name; the others use the epic (purple) rarity, like the mace.
            props.component(DataComponents.ITEM_NAME, net.minecraft.network.chat.Component.translatable("item.breathless.lore_howard").withStyle(net.minecraft.ChatFormatting.RED));
         } else {
            props.rarity(net.minecraft.world.item.Rarity.EPIC);
         }
         LORE[i] = Registry.register(BuiltInRegistries.ITEM, loreKey, new LoreItem(props, i));
      }
   }

   /** True for the three night vision masks. */
   public static boolean isNightVision(ItemStack stack) {
      return stack.is(GAS_MASK_NV) || stack.is(GAS_MASK_NV_EMPTY) || stack.is(GAS_MASK_NV_REINFORCED);
   }

   /** True for either kind of loose filter. */
   public static boolean isFilter(ItemStack stack) {
      return stack.is(GAS_MASK_FILTER) || stack.is(GAS_MASK_FILTER_REINFORCED);
   }

   /** A mask holding these filters, with or without the night vision lens (the right item and the component set). */
   public static ItemStack maskWith(MaskFilters filters, boolean nightVision) {
      if (filters.count() <= 0) {
         return new ItemStack(nightVision ? GAS_MASK_NV_EMPTY : GAS_MASK_EMPTY);
      }
      Item item;
      if (filters.allReinforced()) {
         item = nightVision ? GAS_MASK_NV_REINFORCED : GAS_MASK_REINFORCED;
      } else {
         item = nightVision ? GAS_MASK_NV : GAS_MASK;
      }
      ItemStack stack = new ItemStack(item);
      stack.set(FILTERS, filters);
      return stack;
   }

   /** A plain (no night vision) mask holding these filters. */
   public static ItemStack maskWith(MaskFilters filters) {
      return maskWith(filters, false);
   }

   /** A mask of the same lens type as the old one, now holding these filters. */
   public static ItemStack maskLike(ItemStack old, MaskFilters filters) {
      return maskWith(filters, isNightVision(old));
   }

   /** A loose ordinary filter with this much life left. */
   public static ItemStack filterWith(int life) {
      return filterWith(life, false);
   }

   /** A loose filter (ordinary or reinforced) with this much life left. */
   public static ItemStack filterWith(int life, boolean reinforced) {
      ItemStack stack = new ItemStack(reinforced ? GAS_MASK_FILTER_REINFORCED : GAS_MASK_FILTER);
      stack.setDamageValue(Math.max(0, Tuning.FILTER_MAX - Math.max(1, life)));
      return stack;
   }
}
