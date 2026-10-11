package com.kodari.breathless;

/**
 * Every number you might want to tweak lives here. Shared by client and server.
 * Times are in seconds unless the name says TICKS (20 ticks = 1 second).
 */
public final class Tuning {
   private Tuning() {
   }

   // ---- Exposure meter (0..100) ----
   /** Seconds to go from 0 to 100 while standing still below Y=0 with no working mask. */
   public static final float FILL_SECONDS = 180.0F;
   /** Fill speed multiplier while sprinting or in the air (jumping). */
   public static final float ACTIVE_MULTIPLIER = 2.0F;
   /** Below this Y (overworld only) the spores are in the air. */
   public static final double HAZARD_Y = 0.0D;

   // ---- Critical state (meter at 100) ----
   /** Seconds for the screen to fade fully to black once critical. */
   public static final int FADE_SECONDS = 20;
   /** Suffocation damage (half-hearts) and how often (ticks). 1.0 every 25 ticks is about 0.8 hearts per 2 seconds. */
   public static final float DAMAGE_AMOUNT = 1.0F;
   public static final int DAMAGE_INTERVAL_TICKS = 25;
   /** How long the wheeze sound sample is, in ticks. The sound is replayed this often while critical. */
   public static final int WHEEZE_TICKS = 70;

   // ---- Cure ----
   /** How much a healing potion lowers the meter (needs a working mask). */
   public static final float PURGE_AMOUNT = 60.0F;
   /** If you heal first and put the mask on later, the cure is remembered this long. */
   public static final int ANTIDOTE_TICKS = 2400;

   // ---- Adrenaline trade-off ----
   /** 8.0 = four hearts. */
   public static final float ADRENALINE_DAMAGE = 8.0F;

   // ---- Gas mask / filter ----
   /** Filter durability. One point is used every DRAIN_INTERVAL_TICKS while wearing it below Y=0. */
   public static final int FILTER_MAX = 1200;
   public static final int DRAIN_INTERVAL_TICKS = 10;
   /** Filter drains this many times faster while sprinting. */
   public static final int SPRINT_DRAIN = 2;
   /** Visor overlay cracks appear when remaining filter life drops below these fractions. */
   public static final float CRACK_STAGE_1 = 0.50F;
   public static final float CRACK_STAGE_2 = 0.25F;

   // ---- Zombies ----
   /** 90%: remaining filter life is rolled in 3%..25%. 10%: rolled in 25%..50%. */
   public static final float ZOMBIE_MASK_LOW_CHANCE = 0.90F;
   /** Chance a masked zombie drops its mask (with filter inside). */
   public static final float ZOMBIE_DROP_MASK = 0.70F;
   /** Extra chance (after the mask roll) that only the loose filter drops. */
   public static final float ZOMBIE_DROP_FILTER = 0.20F;
   /** Mask durability lost per point of damage when a zombie is hit by an attacker. */
   public static final float ZOMBIE_MASK_WEAR_PER_DAMAGE = 6.0F;
   /** Spore damage to an unmasked zombie, every 20 ticks. */
   public static final float ZOMBIE_SPORE_DAMAGE = 1.0F;

   // ---- Skeletons ----
   /** Detection range multiplier inside the spores (0.6 = 40% shorter). */
   public static final double SKELETON_RANGE_FACTOR = 0.6D;
   /** Extra arrow spread. Vanilla skeleton spread is about 6 to 14, so 7 is roughly 40% worse on Normal. */
   public static final double SKELETON_EXTRA_INACCURACY = 7.0D;
}
