package com.kodari.breathless;

/** Elijah's phase two voicelines (5L0TH speaking through him): which file, what the on-screen caption says (null = no caption) and how long it lasts. */
public final class SlothLines {
   private SlothLines() {
   }

   // The index is the position in ModSounds.SLOTH_FILES.
   private static final String[] TEXT = {
      "JUST DIE", "STAY STILL", "FACE ME", "STOP HIDING", "Clever.", "You think you're safe?", "CHEAP TRICKS",
      "I WANT YOU DEAD", "CATCH THIS", "KEEP RUNNING", "YOU'RE A JOKE", null,
      "YOU'RE GETTING RUSTY, PLAYER", "KEEP SWINGING", "YOU'RE OUTCLASSED", "IS THIS WHAT HE LOST TO?", "IS THIS ALL YOU'VE GOT?",
      "DIE ALREADY",
      "JUST BURN",
      "WHY WON'T YOU DIE?",
      "GIVE UP",
      "ABOVE YOU",
      "EYES UP",
      "HEADS UP",
      "WHERE ARE YOU LOOKING?",
      "WATCH ME FLY",
      "DON'T MOVE",
      "DODGE THIS",
      "BANG",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
   };
   private static final float[] SECONDS = {
      1.14F, 1.02F, 0.74F, 1.35F, 0.74F, 1.25F, 1.11F, 1.30F, 0.98F, 1.25F, 1.28F, 1.97F, 1.95F, 1.04F, 1.25F, 2.86F, 1.44F, 1.58F, 1.58F, 1.56F, 1.9F, 2.28F, 1.49F, 1.05F, 1.74F, 1.44F, 1.51F, 1.42F, 1.02F, 1.49F, 1.44F, 2.21F, 2.14F, 2.81F, 1.25F, 2.21F, 2.48F, 1.83F
   };

   public static final int[] MACE = {0, 1};
   public static final int[] AXE = {2, 3, 4, 5, 6};
   public static final int[] STUN = {7, 8, 9, 10, 11};
   public static final int[] TAUNT = {12, 13, 14, 15, 16};
   /**
    * Crossbow lines (keep each group in ascending order). Phase one lines have no caption (null text); phase two and aerial lines do.
    * P1_SHOT / MULTI: phase one (MULTI any phase). P1_FAST: phase one aerial snipe (and phase one fast reloads, if he ever has them). P2_SHOT / RELOAD / AIR: phase two.
    */
   public static final int[] RELOAD = {17, 18, 19, 20};
   public static final int[] AIR = {21, 22, 23, 24, 25};
   public static final int[] P2_SHOT = {26, 27, 28};
   public static final int[] P1_FAST = {29, 30};
   public static final int[] P1_SHOT = {31, 32, 33, 36};
   public static final int[] MULTI = {34, 35, 37};

   public static String text(int index) {
      return TEXT[index];
   }

   public static int ticks(int index) {
      return Math.round(SECONDS[index] * 20.0F) + 8;
   }
}
