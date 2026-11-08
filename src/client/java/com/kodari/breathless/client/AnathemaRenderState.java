package com.kodari.breathless.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AnathemaRenderState extends LivingEntityRenderState {
   /** Seconds into the hit clip, or -1. */
   public float hit = -1.0F;
   /** Seconds since it died, or -1 while it lives. */
   public float dying = -1.0F;
}
