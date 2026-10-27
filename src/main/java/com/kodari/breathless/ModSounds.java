package com.kodari.breathless;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

/** Every sound event of the mod. The files live in assets/breathless/sounds and are listed in sounds.json. */
public final class ModSounds {
   private ModSounds() {
   }

   public static SoundEvent WHEEZE;
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
   public static final String[] SLOTH_FILES = {"justdie", "staystill", "faceme", "stophiding", "clever", "youthinkyouresafe", "cheaptricks", "iwantyoudead", "catchthis", "keeprunning", "youreajoke", "laugh", "youregettingrustyplayer", "keepswinging", "youreoutclassed", "isthiswhathelostto", "isthisallyouvegot"};
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
