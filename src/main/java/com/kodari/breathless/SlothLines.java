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
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "I HAVE YOU NOW",
      "NO MORE TRICKS",
      "I'LL MAKE THIS HURT",
      "SCORCH THE EARTH!",
      "LET THE SUN FALL",
      "NOWHERE TO RUN",
      "GET OFF",
      "YOU THINK THIS IS A JOKE?",
      "I WILL MAKE IT RAIN",
      "TRY PARRYING THESE",
      "WHERE DO YOU THINK YOU'RE GOING?",
      "WATCH YOUR GROUND"
   };
   private static final float[] SECONDS = {
      1.14F, 1.02F, 0.74F, 1.35F, 0.74F, 1.25F, 1.11F, 1.30F, 0.98F, 1.25F, 1.28F, 1.97F, 1.95F, 1.04F, 1.25F, 2.86F, 1.44F, 1.58F, 1.58F, 1.56F, 1.9F, 2.28F, 1.49F, 1.05F, 1.74F, 1.44F, 1.51F, 1.42F, 1.02F, 1.49F, 1.44F, 2.21F, 2.14F, 2.81F, 1.25F, 2.21F, 2.48F, 1.83F,
      1.88F, 1.81F, 1.25F, 1.65F, 2.35F, 0.67F, 0.95F, 2.11F, 1.05F, 1.49F, 1.83F, 1.37F, 1.25F, 1.3F, 1.35F, 1.58F, 1.21F, 2.25F, 1.35F, 1.97F, 1.6F, 1.67F, 1.74F, 1.83F, 1.83F
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
   /** The rocket barrage. Phase one lines have no caption, phase two lines do. FIVE: the fifth rocket is stocked; VOLLEY: the barrage from the ground; AERIAL_VOLLEY: from the air (the sword one only when the player holds Eli's Sword); CANCEL: you caught him reloading; FEINT: a stocking reload that is not the fifth. */
   public static final int[] FIVE_P1 = {38, 39};
   public static final int[] VOLLEY_P1 = {40, 41, 42};
   public static final int[] CANCEL_P1 = {43, 44};
   public static final int[] AERIAL_VOLLEY_P1 = {45, 46, 47};
   public static final int[] FEINT_P1 = {48, 49, 50};
   public static final int[] FIVE_P2 = {51, 52, 53};
   public static final int[] VOLLEY_P2 = {54, 55, 56};
   public static final int[] CANCEL_P2 = {57, 58};
   public static final int[] AERIAL_VOLLEY_P2 = {59, 60, 61, 62};
   public static final int[] AERIAL_VOLLEY_P2_NO_SWORD = {59, 61, 62};

   public static String text(int index) {
      return TEXT[index];
   }

   public static int ticks(int index) {
      return Math.round(SECONDS[index] * 20.0F) + 8;
   }
}
