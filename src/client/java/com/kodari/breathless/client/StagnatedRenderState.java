package com.kodari.breathless.client;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.item.ItemStack;

public class StagnatedRenderState extends ArmedEntityRenderState {
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
   /** Seconds into the (looping) block-breaking / block-placing animations, -1 when not doing it. */
   public float mining = -1.0F;
   public float placing = -1.0F;
   /** Seconds into the axe's shield-break clap, -1 when not doing it. */
   public float shield = -1.0F;
   /** Seconds into the soldier's armed spot pose and the unarm, -1 when not doing them. */
   public float spotArmed = -1.0F;
   public float unarm = -1.0F;
   /** Sweaty variant: seconds into the wind charge throw, the mace smash and the water clutch (-1 when not doing it), and whether it is airborne. */
   public float wind = -1.0F;
   public float smash = -1.0F;
   public float clutch = -1.0F;
   public boolean flying;
   /** Shield: seconds into raising it, it being broken and a blocked hit (-1 when not playing), and whether it is held up. */
   public float shieldUp = -1.0F;
   public float shieldDis = -1.0F;
   public float shieldHit = -1.0F;
   public boolean guard;
   /** Elijah: own skin, no body strips, own spot pose. */
   public boolean elijah;
   /** Seconds into the enrage intro, -1 when not playing. */
   public float enrage = -1.0F;
   /** Worn armor: helmet, chestplate, leggings, boots. */
   public final ItemStack[] armor = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
}
