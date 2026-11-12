package com.kodari.breathless.client;

import com.kodari.breathless.Odium;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class OdiumRenderState extends LivingEntityRenderState {
   public int state = Odium.PASSIVE;
   /** Seconds since the state last changed. */
   public float stateSeconds;
   /** Seconds since it was last hit, or -1. */
   public float hitSeconds = -1.0F;
   /** Seconds into the death clip, or -1 while alive. */
   public float deathSeconds = -1.0F;
}
