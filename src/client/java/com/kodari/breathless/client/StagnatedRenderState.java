package com.kodari.breathless.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;

public class StagnatedRenderState extends LivingEntityRenderState {
   public boolean aggressive;
   /** 0..1 progress of the swing, 0 when not swinging. */
   public float attack;
   /** 0..1 progress of the hurt flinch, -1 when not hurt. */
   public float hurt = -1.0F;
   /** Seconds into the "spots you" pose, -1 when not spotting. */
   public float spot = -1.0F;
   /** Seconds into the emerge animation, -1 when not emerging. */
   public float emerge = -1.0F;
   /** Seconds into the armoring animation, -1 when not armoring. */
   public float armoring = -1.0F;
   /** Worn armor: helmet, chestplate, leggings, boots. */
   public final ItemStack[] armor = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
}
