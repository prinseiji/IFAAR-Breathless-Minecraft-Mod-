package com.kodari.breathless;

/** Something that holds a player (an Odium or an Ire): the client reads these to draw the struggle bar and vignette. */
public interface StruggleHolder {
   /** Entity id of the player held, or -1. */
   int getHeldId();

   /** How far along the bar is (0 to 1). */
   float getStruggle();

   /** True while tapping the jump key counts (an Ire only lets you struggle while it is still pulling you in). */
   boolean tapsCount();
}
