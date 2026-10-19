package com.kodari.breathless.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class StagnatedRenderState extends LivingEntityRenderState {
   public boolean aggressive;
   /** 0..1 progress of the swing, 0 when not swinging. */
   public float attack;
   /** 0..1 progress of the hurt flinch, -1 when not hurt. */
   public float hurt = -1.0F;
   /** Seconds into the "spots you" pose, -1 when not spotting. */
   public float spot = -1.0F;
}
