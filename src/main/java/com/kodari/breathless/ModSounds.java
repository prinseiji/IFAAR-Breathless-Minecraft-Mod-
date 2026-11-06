package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

/** Every sound event of the mod. The files live in assets/breathless/sounds and are listed in sounds.json. */
public final class ModSounds {
   private ModSounds() {
   }

   public static SoundEvent WHEEZE;
   /** The Sculk Nidus heartbeat (0.76 s, repeated every 15 ticks). Louder the closer you are. */
   public static SoundEvent NIDUS_HEARTBEAT;
   /** The clang of Elijah parrying a mace attack. */
   public static SoundEvent ELI_PARRY;
   /** Hard difficulty: the self-stab, the syringe, the glitch, and the three themes (Elijah, SLOTH, adrenaline). */
   public static SoundEvent SLOTH_STAB;
   public static SoundEvent SLOTH_INJECT;
   public static SoundEvent SLOTH_GLITCH;
   /** SLOTH's voicelines in the hard fight: the stab, the start of his phase, the adrenaline shout (one of each group is picked). */
   /** The stun slam burst: Elijah's lines (incoming and exhausted, phase one and two), the warning, the alert on every slam and the burnout. */
   public static SoundEvent[] BURST_IN_P1 = new SoundEvent[2];
   public static SoundEvent[] BURST_IN_P2 = new SoundEvent[2];
   public static SoundEvent[] BURST_EX_P1 = new SoundEvent[2];
   public static SoundEvent[] BURST_EX_P2 = new SoundEvent[2];
   public static SoundEvent BURST_WARNING;
   public static SoundEvent BURST_ALERT;
   public static SoundEvent BURST_BURNOUT;
   public static SoundEvent[] HARD_VOICE_STAB = new SoundEvent[3];
   public static SoundEvent[] HARD_VOICE_START = new SoundEvent[4];
   public static SoundEvent[] HARD_VOICE_RAGE = new SoundEvent[3];
   public static SoundEvent HARD_MUSIC_1;
   public static SoundEvent HARD_MUSIC_2;
   public static SoundEvent HARD_MUSIC_3;
   public static SoundEvent MASK_ON;
   public static SoundEvent FILTER_ON;
   public static SoundEvent FILTER_OFF;
   public static SoundEvent FILTER_RUNOUT;
   public static SoundEvent CRACK_50;
   public static SoundEvent CRACK_25;
   public static SoundEvent TENDRILS;
   public static SoundEvent SPORE_DAMAGE;
   public static SoundEvent SIGH_RELIEF;
   public static SoundEvent AMBIENCE_MUFFLED;
   public static SoundEvent AMBIENCE_OPEN;
   public static SoundEvent POPUP_SPAM;
   public static SoundEvent POPUP_FINAL;
   public static SoundEvent INJECT;
   public static SoundEvent STAGNATED_SPOT;
   public static SoundEvent STAGNATED_HURT;
   public static SoundEvent STAGNATED_IDLE;
   public static SoundEvent STAGNATED_DEATH;
   public static SoundEvent STAGNATED_GROWL;
   public static SoundEvent STAGNATED_HIT;
   public static SoundEvent STAGNATED_EMERGE;
   public static SoundEvent BUILDUP;
   public static SoundEvent ETHAN_SPOT;
   public static SoundEvent ETHAN_WIND;
   public static SoundEvent ETHAN_SMASH;
   public static SoundEvent ETHAN_STUN;
   public static SoundEvent ETHAN_SUMMON;
   public static SoundEvent FRUSTRATED;
   /** Phase two voicelines, in the order of SlothLines. */
   public static final String[] SLOTH_FILES = {"justdie", "staystill", "faceme", "stophiding", "clever", "youthinkyouresafe", "cheaptricks", "iwantyoudead", "catchthis", "keeprunning", "youreajoke", "laugh", "youregettingrustyplayer", "keepswinging", "youreoutclassed", "isthiswhathelostto", "isthisallyouvegot", "diealready", "justburn", "whywontyoudie", "giveup", "aboveyou", "eyesup", "headsup", "whereareyoulooking", "watchmefly", "dontmove", "dodgethis", "bang", "thinkfast", "noscope", "eatlead", "saycheese", "letslightthiscandlehuh", "playfair", "respectthe1v1", "imbeingsubjectedtopeerpressuregetback", "justmorekillsforme", "thatmakesfiveofthem", "itsabouttogetbrightinhere", "takecover", "itsgettinghotinhere", "heyidmoveifiwereyou", "hey", "stepoff", "heycounttheseforme", "watchthis", "heyoverhere", "toomuchcoffee", "coldfeethuh", "jumpy", "ihaveyounow", "nomoretricks", "illmakethishurt", "scorchtheearth", "letthesunfall", "nowheretorun", "getoff", "youthinkthisisajoke", "iwillmakeitrain", "tryparryingthese", "wheredoyouthinkyourgoing", "watchyourground", "youllgettheresomeday", "youthinkiwasgonnastandthereandtakeit", "myowntrickagainstme", "nooneisbetterthanme"};
   public static final SoundEvent[] SLOTH = new SoundEvent[SLOTH_FILES.length];
   public static SoundEvent PARRY;
   public static SoundEvent ELI_PHASE_ONE;
   public static SoundEvent ELI_TENEBRE;
   public static SoundEvent ELI_TENEBRE_START;
   public static SoundEvent ELI_DEATH_SFX;
   public static SoundEvent HEART_DESTROYED;
   public static SoundEvent STUN_WARNING;
   public static SoundEvent COMBO_WARNING;
   public static SoundEvent XB_FIRE;
   public static SoundEvent ETHAN_CLUTCH;
   public static SoundEvent ETHAN_DEATH;
   public static SoundEvent ETHAN_ENRAGE;
   public static SoundEvent ETHAN_ENRAGED_SFX;

   private static SoundEvent reg(String name) {
      return Registry.register(BuiltInRegistries.SOUND_EVENT, Breathless.id(name), SoundEvent.createVariableRangeEvent(Breathless.id(name)));
   }

   public static void register() {
      WHEEZE = reg("wheeze");
      NIDUS_HEARTBEAT = reg("nidus_heartbeat");
      ELI_PARRY = reg("eli_parry");
      SLOTH_STAB = reg("sloth_stab");
      SLOTH_INJECT = reg("sloth_inject");
      SLOTH_GLITCH = reg("sloth_glitch");
      for (int i = 0; i < 2; i++) {
         BURST_IN_P1[i] = reg("eli_burst_in_p1_" + (i + 1));
         BURST_IN_P2[i] = reg("eli_burst_in_p2_" + (i + 1));
         BURST_EX_P1[i] = reg("eli_burst_ex_p1_" + (i + 1));
         BURST_EX_P2[i] = reg("eli_burst_ex_p2_" + (i + 1));
      }
      BURST_WARNING = reg("eli_burst_warning");
      BURST_ALERT = reg("eli_burst_alert");
      BURST_BURNOUT = reg("eli_burst_burnout");
      for (int i = 0; i < HARD_VOICE_STAB.length; i++) {
         HARD_VOICE_STAB[i] = reg("sloth_hard_stab_" + (i + 1));
      }
      for (int i = 0; i < HARD_VOICE_START.length; i++) {
         HARD_VOICE_START[i] = reg("sloth_hard_start_" + (i + 1));
      }
      for (int i = 0; i < HARD_VOICE_RAGE.length; i++) {
         HARD_VOICE_RAGE[i] = reg("sloth_hard_rage_" + (i + 1));
      }
      HARD_MUSIC_1 = reg("sloth_hard_1");
      HARD_MUSIC_2 = reg("sloth_hard_2");
      HARD_MUSIC_3 = reg("sloth_hard_3");
      MASK_ON = reg("mask_on");
      FILTER_ON = reg("filter_on");
      FILTER_OFF = reg("filter_off");
      FILTER_RUNOUT = reg("filter_runout");
      CRACK_50 = reg("crack_50");
      CRACK_25 = reg("crack_25");
      TENDRILS = reg("tendrils");
      SPORE_DAMAGE = reg("spore_damage");
      SIGH_RELIEF = reg("sigh_relief");
      AMBIENCE_MUFFLED = reg("ambience_muffled");
      AMBIENCE_OPEN = reg("ambience_open");
      POPUP_SPAM = reg("popup_spam");
      POPUP_FINAL = reg("popup_final");
      INJECT = reg("inject");
      BUILDUP = reg("buildup");
      STAGNATED_SPOT = reg("stagnated_spot");
      STAGNATED_HURT = reg("stagnated_hurt");
      STAGNATED_IDLE = reg("stagnated_idle");
      STAGNATED_DEATH = reg("stagnated_death");
      STAGNATED_GROWL = reg("stagnated_growl");
      STAGNATED_HIT = reg("stagnated_hit");
      STAGNATED_EMERGE = reg("stagnated_emerge");
      ETHAN_SPOT = reg("ethan_spot");
      ETHAN_WIND = reg("ethan_wind");
      ETHAN_SMASH = reg("ethan_smash");
      ETHAN_STUN = reg("ethan_stun");
      ETHAN_SUMMON = reg("ethan_summon");
      FRUSTRATED = reg("frustrated");
      for (int i = 0; i < SLOTH_FILES.length; i++) {
         SLOTH[i] = reg("sloth_" + SLOTH_FILES[i]);
      }
      PARRY = reg("parry");
      ELI_PHASE_ONE = reg("eli_phase_one");
      ELI_TENEBRE = reg("eli_tenebre");
      ELI_TENEBRE_START = reg("eli_tenebre_start");
      ELI_DEATH_SFX = reg("eli_death_sfx");
      HEART_DESTROYED = reg("heart_destroyed");
      STUN_WARNING = reg("stun_warning");
      COMBO_WARNING = reg("combo_warning");
      XB_FIRE = reg("crossbow_fire");
      ETHAN_CLUTCH = reg("ethan_clutch");
      ETHAN_DEATH = reg("ethan_death");
      ETHAN_ENRAGE = reg("ethan_enrage");
      ETHAN_ENRAGED_SFX = reg("ethan_enraged_sfx");
   }
}
