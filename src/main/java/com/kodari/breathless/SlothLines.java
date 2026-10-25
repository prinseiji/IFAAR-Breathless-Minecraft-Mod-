package com.kodari.breathless;

/** Elijah's phase two voicelines (5L0TH speaking through him): which file, what the on-screen caption says (null = no caption) and how long it lasts. */
public final class SlothLines {
   private SlothLines() {
   }

   // The index is the position in ModSounds.SLOTH_FILES.
   private static final String[] TEXT = {
      "JUST DIE", "STAY STILL", "FACE ME", "STOP HIDING", "Clever.", "You think you're safe?", "CHEAP TRICKS",
      "I WANT YOU DEAD", "CATCH THIS", "KEEP RUNNING", "YOU'RE A JOKE", null,
      "YOU'RE GETTING RUSTY, PLAYER", "KEEP SWINGING", "YOU'RE OUTCLASSED", "IS THIS WHAT HE LOST TO?", "IS THIS ALL YOU'VE GOT?"
   };
   private static final float[] SECONDS = {
      1.14F, 1.02F, 0.74F, 1.35F, 0.74F, 1.25F, 1.11F, 1.30F, 0.98F, 1.25F, 1.28F, 1.97F, 1.95F, 1.04F, 1.25F, 2.86F, 1.44F
   };

   public static final int[] MACE = {0, 1};
   public static final int[] AXE = {2, 3, 4, 5, 6};
   public static final int[] STUN = {7, 8, 9, 10, 11};
   public static final int[] TAUNT = {12, 13, 14, 15, 16};

   public static String text(int index) {
      return TEXT[index];
   }

   public static int ticks(int index) {
      return Math.round(SECONDS[index] * 20.0F) + 8;
   }
}
