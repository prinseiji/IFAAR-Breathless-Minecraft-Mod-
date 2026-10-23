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
   public static final float FILL_SECONDS = 90.0F;
   /** Fill speed multiplier while sprinting or in the air (jumping). */
   public static final float ACTIVE_MULTIPLIER = 2.0F;
   /** Below this Y (overworld only) the spores are in the air. */
   public static final double HAZARD_Y = 0.0D;

   // ---- Slowness that scales with exposure (meter value -> Slowness level) ----
   public static final float SLOW_TIER_1 = 25.0F;   // Slowness I
   public static final float SLOW_TIER_2 = 50.0F;   // Slowness II
   public static final float SLOW_TIER_3 = 75.0F;   // Slowness III
   public static final float SLOW_TIER_CRIT = 100.0F; // Slowness IV

   // ---- Critical state (meter at 100) ----
   /** Seconds for the screen to fade fully to black once critical. */
   public static final int FADE_SECONDS = 20;
   /** Suffocation damage (half-hearts) when the critical state starts, and how often (ticks). */
   public static final float DAMAGE_START = 1.0F;
   public static final int DAMAGE_INTERVAL_TICKS = 25;
   /** The damage climbs to this value the longer you stay critical, reaching it after DAMAGE_RAMP_SECONDS. */
   public static final float DAMAGE_MAX = 3.0F;
   public static final int DAMAGE_RAMP_SECONDS = 30;
   /** How long the wheeze sound sample is, in ticks. The sound is replayed this often while critical. */
   public static final int WHEEZE_TICKS = 70;

   // ---- Blur / haze pulses (client) ----
   /** The vision-haze pulses start at this exposure and get stronger toward 100. */
   public static final float BLUR_START = 40.0F;
   /** Ticks between pulses at 100% (longer at lower exposure). */
   public static final int BLUR_PERIOD_TICKS = 140;

   // ---- Cure ----
   /** How much an Instant Health potion lowers the meter (needs a working mask), by potion level: I, II, III or more. */
   public static final float PURGE_LEVEL_1 = 30.0F;
   public static final float PURGE_LEVEL_2 = 60.0F;
   public static final float PURGE_LEVEL_3 = 100.0F;
   /** If you heal first and put the mask on later, the cure is remembered this long. */
   public static final int ANTIDOTE_TICKS = 2400;

   // ---- Adrenaline trade-off (F13ND clears the spores) ----
   /** 8.0 = four hearts. */
   public static final float ADRENALINE_DAMAGE = 8.0F;
   /** Cooldown put on the Adrenaline Shot item after it is used as a cure. */
   public static final int ADRENALINE_COOLDOWN_TICKS = 100;

   // ---- F13ND pop-up sequence (all in ticks, counted from the injection; 20 ticks = 1 second) ----
   /** Master switch. false = no pop-ups, the cure still works. */
   public static final boolean POPUPS_ENABLED = true;
   /** Red flash on screen at the moment of the injection. */
   public static final int FLASH_TICKS = 9;
   /** The build-up sound and the loading pop-up start this many ticks after the injection. */
   public static final int BUILDUP_START_TICK = 10;
   /** Length of the build-up sound (6.3 s). The bar drains and the pop-ups pile on during exactly this time. */
   public static final int BUILDUP_TICKS = 126;
   /** How many cover pop-ups pile onto the bar. They start slow and speed up toward the end of the build-up. */
   public static final int POPUP_COUNT = 12;
   public static final int POPUP_FIRST_TICK = BUILDUP_START_TICK + 6;
   /** Tick when the last cover pop-up lands (the bar is completely covered). */
   public static final int POPUP_LAST_TICK = BUILDUP_START_TICK + BUILDUP_TICKS - 14;
   /** How strongly the pop-ups bunch up toward the end (1 = evenly spaced, higher = slower start, faster finish). */
   public static final float POPUP_SPEEDUP = 1.6F;
   /** Tick when the bar is empty: all cover pop-ups and the loading pop-up vanish at once. */
   public static final int POPUP_CLEAR_TICK = BUILDUP_START_TICK + BUILDUP_TICKS;
   /** The red vignette fades out over this many ticks, starting when the cover pop-ups vanish. */
   public static final int VIGNETTE_FADEOUT_TICKS = 12;
   /** The short pause (ticks) before the final pop-up. */
   public static final int POPUP_PAUSE_TICKS = 16;
   /** Tick when the final "event run unsuccessful" pop-up appears (with a red flash). */
   public static final int POPUP_FINAL_TICK = POPUP_CLEAR_TICK + POPUP_PAUSE_TICKS;
   /** Tick when the final pop-up (and the flash) is gone. The whole sequence ends here. */
   public static final int POPUP_END_TICK = POPUP_FINAL_TICK + 55;
   /** How far (in percent points) the % number warbles up and down while the bar drains. */
   public static final int WARBLE_PERCENT = 10;
   public static final float BUILDUP_VOLUME = 0.9F;
   public static final float INJECT_VOLUME = 1.0F;
   public static final float POPUP_SPAM_VOLUME = 0.6F;

   /** The tick cover pop-up number i (0-based) lands on. Shared by the sounds (server) and the drawing (client). */
   public static int popupBornTick(int i) {
      float u = POPUP_COUNT <= 1 ? 0.0F : i / (float)(POPUP_COUNT - 1);
      float eased = 1.0F - (float)Math.pow(1.0F - u, POPUP_SPEEDUP);
      return POPUP_FIRST_TICK + Math.round((POPUP_LAST_TICK - POPUP_FIRST_TICK) * eased);
   }

   // ---- Gas mask / filter ----
   /** Filter life units. One unit is used every DRAIN_INTERVAL_TICKS * 2 ticks (the two filters take turns). */
   public static final int FILTER_MAX = 1200;
   public static final int DRAIN_INTERVAL_TICKS = 10;
   /** How many filters the mask needs to work (the mask holds 2). */
   public static final int FILTERS_REQUIRED = 2;
   /** Filter drains this many times faster while sprinting. */
   public static final int SPRINT_DRAIN = 2;
   /** Visor overlay cracks appear when remaining filter life drops below these fractions. */
   public static final float CRACK_STAGE_1 = 0.50F;
   public static final float CRACK_STAGE_2 = 0.25F;

   // ---- Zombies ----
   /** 90%: remaining filter life is rolled in 3%..25%. 10%: rolled in 25%..50%. */
   public static final float ZOMBIE_MASK_LOW_CHANCE = 0.90F;
   /** Chance a masked zombie drops its mask (with its filters inside). */
   public static final float ZOMBIE_DROP_MASK = 0.12F;
   /** Extra chance (after the mask roll) that only one loose filter drops. */
   public static final float ZOMBIE_DROP_FILTER = 0.12F;
   /** Filter life lost per point of damage when a zombie is hit by an attacker. */
   public static final float ZOMBIE_MASK_WEAR_PER_DAMAGE = 6.0F;
   /** Spore damage to an unmasked zombie, every 20 ticks. */
   public static final float ZOMBIE_SPORE_DAMAGE = 1.0F;

   // ---- Skeletons ----
   /** Detection range multiplier inside the spores (0.6 = 40% shorter). */
   public static final double SKELETON_RANGE_FACTOR = 0.6D;
   /** Extra arrow spread. Vanilla skeleton spread is about 6 to 14, so 7 is roughly 40% worse on Normal. */
   public static final double SKELETON_EXTRA_INACCURACY = 7.0D;

   // ---- Ambience (server plays an 8 second chunk this often while below Y=0) ----
   public static final int AMBIENCE_TICKS = 154;
   /** Open-air (no mask) is loud, behind a working mask it is muffled and quieter. */
   public static final float AMBIENCE_VOLUME_OPEN = 1.0F;
   public static final float AMBIENCE_VOLUME_MASKED = 0.55F;

   // ---- Sound volumes ----
   public static final float SIGH_VOLUME = 0.35F;
   public static final float DAMAGE_SOUND_VOLUME = 0.6F;

   /** How many ticks the unmasked spore streams take to reach your head (lower = faster, tighter following). */
   public static final int SPORE_STREAM_TICKS = 14;

   // ---- 5-L0 (SLOTH) text: silent, stacked letters that only appear while there is exposure in your head ----
   /** Ticks a line takes to fade in, stay, and fade out. */
   public static final int SLOTH_FADE_IN = 30;
   public static final int SLOTH_HOLD = 55;
   public static final int SLOTH_FADE_OUT = 40;
   /** Minimum ticks between any two (non-purge) lines, so it never gets annoying. */
   public static final int SLOTH_COOLDOWN_TICKS = 20 * 25;
   /** Chance (0..1) that a trigger actually speaks. */
   public static final float SLOTH_CHANCE_FIRST_CONTACT = 0.70F;
   public static final float SLOTH_CHANCE_STAGE = 0.65F;
   public static final float SLOTH_CHANCE_CRITICAL = 0.90F;
   public static final float SLOTH_CHANCE_MASK_ON = 0.60F;
   /** Per second, while sprinting with exposure. */
   public static final float SLOTH_CHANCE_SPRINT = 0.06F;
   /** Per second, while wearing a working mask with exposure. */
   public static final float SLOTH_CHANCE_MASKED_IDLE = 0.015F;
   /** Chance for a second line during a purge. The first one always comes. */
   public static final float SLOTH_CHANCE_PURGE_SECOND = 0.40F;

   // ---- Nearby sculk (counted only where the spores can actually reach: through open air, never through walls) ----
   /** How far (blocks) from your head the scan reaches. */
   public static final int SCULK_RADIUS = 6;
   /** Most open-air cells the scan visits (keeps it cheap). */
   public static final int SCULK_MAX_CELLS = 4000;
   /** Sculk score needed for each tier. A sculk block or vein counts 1; a catalyst, shrieker or sensor counts 4. */
   public static final int SCULK_TIER_1 = 8;
   public static final int SCULK_TIER_2 = 30;
   /** Tier 3 is the "Moribund" tier. */
   public static final int SCULK_TIER_3 = 80;
   /** Exposure fill multiplier per tier (0 = no sculk nearby). */
   public static final float[] SCULK_FILL = {1.0F, 1.5F, 2.0F, 3.0F};
   /** Filter wear multiplier per tier. */
   public static final int[] SCULK_DRAIN = {1, 1, 2, 3};

   // ---- Reinforced filters ----
   /** A reinforced filter only wears down on 1 of every this-many wear ticks. */
   public static final int REINFORCED_WEAR_DIVISOR = 2;

   // ---- Spore tonics (purge amounts, meter points) ----
   public static final float TONIC_LESSER_PURGE = 10.0F;
   public static final float TONIC_STRONG_PURGE = 25.0F;

   // ---- Stagnated (5-L0's puppet) ----
   public static final float STAGNATED_HEALTH = 20.0F;
   /** About sprint speed: agile but fragile (a husk). */
   public static final double STAGNATED_SPEED = 0.35;
   public static final double STAGNATED_CHASE_MODIFIER = 1.2;
   public static final double STAGNATED_DAMAGE = 3.0;
   public static final float STAGNATED_HIT_EXPOSURE = 15.0F;
   public static final int STAGNATED_SPOT_TICKS = 17;
   public static final double STAGNATED_MAX_SIGHT = 128.0;
   public static final double STAGNATED_UNDEAD_SIGHT = 6.0;
   public static final double STAGNATED_OTHER_SIGHT = 12.0;
   public static final int STAGNATED_MIN_TIER = 3;
   public static final int STAGNATED_SPAWN_ODDS = 45;
   public static final int STAGNATED_COOLDOWN_SECONDS = 90;
   public static final int STAGNATED_MAX_NEAR = 2;
   /** Length of the emerge animation (7.92 s). */
   public static final int STAGNATED_EMERGE_TICKS = 159;
   /** Sideways reach beyond the sight radius for a sprinting player (it hears the noise). */
   public static final double STAGNATED_NOISE_FACTOR = 2.0;
   /** It looks this far up and down, ignoring height otherwise (it is oblivious, not precise). */
   /** Echolocation: how far it hears an active sculk sensor (blocks sideways / up and down). */
   public static final int STAGNATED_LISTEN_RADIUS = 20;
   public static final int STAGNATED_LISTEN_HEIGHT = 10;
   /** Armoring animation: 1.04 s long, the armor goes on at 0.67 s. */
   public static final int STAGNATED_ARMOR_TICKS = 21;
   public static final int STAGNATED_ARMOR_EQUIP_AT = 13;
   /** Soldier weapons: 50% iron sword, 25% stone axe, 25% bare-handed. */
   public static final float STAGNATED_SWORD_CHANCE = 0.50F;
   public static final float STAGNATED_AXE_CHANCE = 0.25F;
   /** A held weapon adds its own damage (iron sword +5, stone axe +8), so the base drops: sword hit = 6, axe hit = 9, bare hands = STAGNATED_DAMAGE. */
   public static final double STAGNATED_ARMED_BASE_DAMAGE = 1.0;
   /** How much slower it gets once the iron is on, and again while it holds the axe (they multiply). */
   public static final double STAGNATED_ARMOR_SLOW = 0.20;
   public static final double STAGNATED_AXE_SLOW = 0.15;
   /** The axe's shield-break move: whole animation in ticks (0.71 s), the clap lands at tick 10 (0.5 s), and how long the shield stays disabled. */
   public static final int STAGNATED_SHIELD_TICKS = 15;
   public static final int STAGNATED_SHIELD_STRIKE_AT = 10;
   public static final int STAGNATED_SHIELD_DISABLE_TICKS = 100;
   /** Soldier with a weapon: the longer "spots" pose (1.83 s) where it draws the weapon at 1.04 s (tick 21), and the unarm (0.875 s) where it stashes it at 0.33 s (tick 7). */
   public static final int STAGNATED_SPOT_ARMED_TICKS = 37;
   public static final int STAGNATED_DRAW_AT = 21;
   public static final int STAGNATED_UNARM_TICKS = 18;
   public static final int STAGNATED_UNARM_AT = 7;
   /** How far sideways a target may be before an excavator starts to pillar up toward it. */
   public static final double STAGNATED_PILLAR_RANGE = 3.0;
   // Pickaxe and builder roles
   /** One round of three swings (1.29 s). Harder blocks take more rounds. */
   public static final int STAGNATED_MINE_TICKS = 26;
   /** The "place" animation is 0.63 s. */
   public static final int STAGNATED_PLACE_TICKS = 13;
   /** A builder gives up after this many blocks. */
   public static final int STAGNATED_MAX_BLOCKS = 32;
   public static final boolean STAGNATED_MINE_DROPS = false;
   /** It won't mine anything tougher than this (stone is 1.5, deepslate 3, obsidian 50). */
   public static final float STAGNATED_MINE_MAX_HARDNESS = 8.0F;
   /** It only digs or builds when the target is this close but cannot be reached. */
   public static final double STAGNATED_ACT_RANGE = 14.0;
   /** How far its arm reaches when it breaks a block (the floor under a target above it, or a blocker on the line). */
   public static final double STAGNATED_REACH = 4.5;
   public static final double STAGNATED_VERTICAL = 40.0;
   /** It gives up on a player it can no longer sense after this many ticks (so you can outrun it). */
   public static final int STAGNATED_GIVE_UP_TICKS = 100;

   // Advancements: "A Quiet Place" (5 minutes below Y=0 without sprinting) and "Breathless" (10 minutes below Y=0 with a clean meter).
   public static final int ACH_QUIET_TICKS = 5 * 60 * 20;
   public static final int ACH_CLEAN_TICKS = 10 * 60 * 20;

   /** How far it notices a player: about 16 blocks clean, up to 48 at full exposure; a masked player with a clean meter is nearly invisible. */
   public static double stagnatedSight(float exposure, boolean maskOk) {
      if (exposure <= 0.0F) {
         return maskOk ? 10.0 : 24.0;
      }
      return Math.min(STAGNATED_MAX_SIGHT, 24.0 + 48.0 * Math.min(1.0F, exposure / 100.0F));
   }

   // ---- The sweaty Stagnated (rare diamond mace variant)
   /** Chance that a new soldier is the sweaty one. */
   public static final float SWEATY_CHANCE = 1.0F / 150.0F;
   /** Wind-up (windburst, 0.54 s) with the burst at tick 6 (0.29 s), the smash swing (0.33 s), the clutch (0.63 s) with water placed at tick 4 and picked up at tick 8. */
   public static final int SWEATY_WIND_TICKS = 11;
   public static final int SWEATY_LAUNCH_AT = 6;
   public static final int SWEATY_SMASH_TICKS = 7;
   public static final int SWEATY_CLUTCH_TICKS = 13;
   public static final int SWEATY_CLUTCH_PLACE_AT = 4;
   public static final int SWEATY_CLUTCH_PICKUP_AT = 8;
   /** Upward speed of the wind-charge launch, and how far away the target may be (blocks) for it to launch. */
   public static final double SWEATY_LAUNCH_UP = 1.1;
   public static final double SWEATY_RANGE_MAX = 14.0;
   /** Smash damage = base + fall * per block, capped (12 = 6 hearts). */
   public static final float SWEATY_SMASH_BASE = 4.0F;
   public static final float SWEATY_SMASH_PER_BLOCK = 1.6F;
   public static final float SWEATY_SMASH_MAX = 12.0F;
   /** The cycle: fights with the diamond sword for this many ticks or hits (whichever first), then swaps to the mace for a launch + smash, then swaps back. */
   public static final int SWEATY_SWORD_TICKS = 120;
   public static final int SWEATY_SWORD_HITS = 4;
   /** At the end of the fall he smashes only if the target is within this many blocks (horizontally), otherwise he does the water clutch. */
   public static final double SWEATY_SMASH_REACH = 3.5;
   /** Chance each armor piece drops (the mace always does). Everything drops at about half durability. */
   public static final float SWEATY_ARMOR_DROP = 0.85F;
   /** Chance that a launch is a stun slam (axe down, mace on landing, breaks a shield): normally, and when the target is holding its shield up. */
   public static final float SWEATY_STUN_CHANCE = 0.35F;
   public static final float SWEATY_STUN_CHANCE_SHIELD = 0.7F;
   /** Ethan eats a golden apple (standing still) when his health drops to this fraction, at most this many times per fight, taking this many ticks. */
   public static final float SWEATY_EAT_BELOW = 0.4F;
   public static final int SWEATY_APPLES = 2;
   public static final int SWEATY_EAT_TICKS = 32;
   /** Every Stagnated except the sweaty one withers away above the spore layer: grace period before it starts, then damage once a second. */
   public static final int STAGNATED_SURFACE_GRACE_TICKS = 60;
   public static final float STAGNATED_SURFACE_DAMAGE = 2.0F;
   /** The sweaty one also turns up on the surface: each second a player is above ground the odds are 1 in this, then a long cooldown (seconds). */
   public static final int SWEATY_SURFACE_ODDS = 1800;
   public static final int SWEATY_SURFACE_COOLDOWN_SECONDS = 600;
   /** Breach II on his mace: the smash counts the victim's armor as this much weaker (0.3 = 30%). */
   public static final double SWEATY_BREACH = 0.3;
   /** Boss balance: Ethan's own max health (the other Stagnated stay at STAGNATED_HEALTH). */
   public static final double SWEATY_HEALTH = 80.0;
   /** Stun slam: launches higher (more time to get out of the way) and may hit harder (cap 16 = 8 hearts). */
   public static final double SWEATY_STUN_LAUNCH_UP = 1.5;
   public static final float SWEATY_STUN_MAX = 16.0F;
   /** Steering toward the target in the air: acceleration per tick and top speed (lower = easier to dodge). */
   public static final double SWEATY_STEER = 0.045;
   public static final double SWEATY_STEER_CAP = 0.7;
   /** Before eating he hops away (wind charge) if you are closer than this many blocks; the hop's horizontal speed. */
   public static final double SWEATY_EAT_SAFE = 8.0;
   public static final double SWEATY_FLEE_SPEED = 0.8;
   /** The smash hits things within about this many blocks of him (smaller = a near miss is a miss). */
   public static final double SWEATY_SMASH_RADIUS = 2.2;
   /** After he wins ("gg ez") he waits this many ticks, then leaves the game and despawns. */
   public static final int SWEATY_LEAVE_TICKS = 30;
}
