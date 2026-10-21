package com.kodari.breathless;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** A lore document. Right-click to read it; it is not used up. */
public class LoreItem extends Item {
   private final int doc;

   public LoreItem(Item.Properties properties, int doc) {
      super(properties);
      this.doc = doc;
   }

   @Override
   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
         Lore.read(serverPlayer, this.doc);
      }
      return InteractionResult.SUCCESS;
   }
}
