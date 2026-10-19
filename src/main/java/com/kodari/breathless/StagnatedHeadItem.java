package com.kodari.breathless;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * The Stagnated Head: works like a spawn egg. It climbs out of the ground with its emerge animation;
 * sneak while placing to skip the animation (handy for testing).
 */
public class StagnatedHeadItem extends Item {
   public StagnatedHeadItem(Item.Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      if (!(level instanceof ServerLevel server)) {
         return InteractionResult.SUCCESS;
      }
      BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
      Stagnated mob = ModEntities.STAGNATED.spawn(server, pos, EntitySpawnReason.SPAWN_ITEM_USE);
      if (mob == null) {
         return InteractionResult.FAIL;
      }
      Player player = context.getPlayer();
      if (player == null || !player.isSecondaryUseActive()) {
         mob.startEmerging();
      }
      if (player == null || !player.isCreative()) {
         context.getItemInHand().shrink(1);
      }
      return InteractionResult.SUCCESS;
   }
}
