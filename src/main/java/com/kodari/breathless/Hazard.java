package com.kodari.breathless;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import com.kodari.combatinjuries.CombatInjuriesApi;

/** Server side of the spore hazard: the exposure meter, the critical state and the cure. */
public final class Hazard {
   private Hazard() {
   }

   /** Per-player scratch values that do not need to survive a restart. */
   private static final class Rt {
      int antidoteTicks;
      int healCooldown;
      int damageTimer;
      int wheezeTimer;
      float lastHealth = -1.0F;
      float lastSent = -1.0F;
      boolean drinkingHeal;
      boolean pendingHeal;
      int drinkRemaining;
   }

   private static final Map<UUID, Rt> RUNTIME = new HashMap<>();

   public static void init() {
      ServerTickEvents.END_SERVER_TICK.register(Hazard::tick);
      ServerPlayerEvents.JOIN.register(player -> RUNTIME.remove(player.getUUID()));
      ServerPlayerEvents.LEAVE.register(player -> RUNTIME.remove(player.getUUID()));
      ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
         RUNTIME.remove(newPlayer.getUUID());
         newPlayer.removeAttached(Breathless.EXPOSURE);
      });
      // IFAAR hook: an adrenaline shot while critical wipes the infection, at a price.
      CombatInjuriesApi.RUSH_STARTED.register(Hazard::onRushStarted);
   }

   // ---- meter access ----

   public static float getExposure(Player player) {
      Float value = player.getAttached(Breathless.EXPOSURE);
      return value == null ? 0.0F : value;
   }

   public static void setExposure(ServerPlayer player, float value) {
      player.setAttached(Breathless.EXPOSURE, Math.max(0.0F, Math.min(100.0F, value)));
   }

   public static boolean isCritical(Player player) {
      return getExposure(player) >= 100.0F - 0.001F;
   }

   private static void sync(ServerPlayer player, Rt rt, float value) {
      ServerPlayNetworking.send(player, new ExposurePayload(value));
      rt.lastSent = value;
   }

   /** Called by the /breathless command so the HUD updates right away. */
   public static void forceSync(ServerPlayer player) {
      Rt rt = RUNTIME.computeIfAbsent(player.getUUID(), k -> new Rt());
      sync(player, rt, getExposure(player));
   }

   /** Called by HealMixin the moment an Instant Health effect lands on a player (drunk or splashed). */
   public static void notifyInstantHealth(ServerPlayer player) {
      RUNTIME.computeIfAbsent(player.getUUID(), k -> new Rt()).pendingHeal = true;
   }

   // ---- main tick ----

   private static void tick(MinecraftServer server) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         tickPlayer(player);
      }
   }

   private static void tickPlayer(ServerPlayer player) {
      if (!player.isAlive()) {
         return;
      }
      Rt rt = RUNTIME.computeIfAbsent(player.getUUID(), k -> new Rt());
      ServerLevel level = (ServerLevel)player.level();
      boolean exempt = player.isCreative() || player.isSpectator();
      boolean underground = Masks.isUnderground(player);
      boolean maskOk = Masks.hasWorkingMask(player);
      float start = getExposure(player);
      float exposure = start;

      // 1) Did the player just get healed by an Instant Health potion?
      if (rt.healCooldown > 0) {
         rt.healCooldown--;
      }
      boolean healed = false;
      float health = player.getHealth();
      if (rt.lastHealth >= 0.0F && health - rt.lastHealth >= 1.99F) {
         healed = true;
      }
      rt.lastHealth = health;
      if (player.isUsingItem()) {
         rt.drinkingHeal = isHealingPotion(player.getUseItem());
         rt.drinkRemaining = player.getUseItemRemainingTicks();
      } else {
         if (rt.drinkingHeal && rt.drinkRemaining <= 2) {
            healed = true;
         }
         rt.drinkingHeal = false;
      }
      if (rt.pendingHeal) {
         healed = true;
         rt.pendingHeal = false;
      }
      if (healed && rt.healCooldown <= 0) {
         rt.healCooldown = 10;
         exposure = onHealed(player, rt, exposure, maskOk);
      }
      if (rt.antidoteTicks > 0) {
         rt.antidoteTicks--;
      }

      // 2) Rising (only below Y=0 with no working mask)
      if (!exempt && underground && !maskOk && exposure < 100.0F) {
         float rate = 100.0F / (Tuning.FILL_SECONDS * 20.0F);
         if (player.isSprinting() || !player.onGround()) {
            rate *= Tuning.ACTIVE_MULTIPLIER;
         }
         exposure = Math.min(100.0F, exposure + rate);
      }

      // 3) The filter wears down while the mask is doing its job
      if (!exempt && underground && maskOk && player.tickCount % Tuning.DRAIN_INTERVAL_TICKS == 0) {
         Masks.drainFilter(player, player.isSprinting() ? Tuning.SPRINT_DRAIN : 1);
         maskOk = Masks.hasWorkingMask(player);
      }

      // 4) Critical state
      boolean critical = exposure >= 100.0F - 0.001F;
      if (critical && !exempt) {
         if (maskOk && rt.antidoteTicks > 0) {
            // Mask is on and the cure is already in the blood: purge now.
            exposure = Math.max(0.0F, exposure - Tuning.PURGE_AMOUNT);
            rt.antidoteTicks = 0;
            player.sendOverlayMessage(Component.literal("The spores loosen their grip."));
         } else {
            if (++rt.damageTimer >= Tuning.DAMAGE_INTERVAL_TICKS) {
               rt.damageTimer = 0;
               player.hurtServer(level, level.damageSources().drown(), Tuning.DAMAGE_AMOUNT);
            }
            if (--rt.wheezeTimer <= 0) {
               rt.wheezeTimer = Tuning.WHEEZE_TICKS;
               level.playSound(null, player.blockPosition(), Breathless.WHEEZE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
         }
      } else {
         rt.damageTimer = 0;
         rt.wheezeTimer = 0;
      }

      // 5) Save + tell the client
      if (exposure != start) {
         setExposure(player, exposure);
      }
      float shown = getExposure(player);
      if (rt.lastSent < 0.0F || Math.abs(shown - rt.lastSent) >= 0.25F || (player.tickCount % 20 == 0 && shown != rt.lastSent)) {
         sync(player, rt, shown);
      }
   }

   // ---- healing / cure ----

   private static String effectPath(MobEffectInstance instance) {
      return instance.getEffect().unwrapKey().map(key -> key.identifier().getPath()).orElse("");
   }

   private static boolean isHealingPotion(ItemStack stack) {
      if (!stack.is(Items.POTION)) {
         return false;
      }
      PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
      if (contents == null) {
         return false;
      }
      for (MobEffectInstance instance : contents.getAllEffects()) {
         if ("instant_health".equals(effectPath(instance))) {
            return true;
         }
      }
      return false;
   }

   /**
    * Both halves are needed to cure the critical state: a working mask AND a healing event.
    * Mask first, then heal: purge right now. Heal first, then mask: the cure is remembered for a while.
    */
   private static float onHealed(ServerPlayer player, Rt rt, float exposure, boolean maskOk) {
      if (maskOk) {
         if (exposure > 0.0F) {
            player.sendOverlayMessage(Component.literal("The spores loosen their grip."));
         }
         rt.antidoteTicks = 0;
         return Math.max(0.0F, exposure - Tuning.PURGE_AMOUNT);
      }
      if (exposure >= 100.0F - 0.001F) {
         rt.antidoteTicks = Tuning.ANTIDOTE_TICKS;
         player.sendOverlayMessage(Component.literal("The cure is in your blood. Get a gas mask on!"));
      }
      return exposure;
   }

   // ---- IFAAR adrenaline hook ----

   private static void onRushStarted(ServerPlayer player) {
      if (!isCritical(player)) {
         return;
      }
      setExposure(player, 0.0F);
      Rt rt = RUNTIME.computeIfAbsent(player.getUUID(), k -> new Rt());
      rt.antidoteTicks = 0;
      rt.damageTimer = 0;
      rt.wheezeTimer = 0;
      sync(player, rt, 0.0F);
      player.sendOverlayMessage(Component.literal("The adrenaline burns the spores out, and you pay for it."));
      if (!player.isCreative() && !player.isSpectator()) {
         ServerLevel level = (ServerLevel)player.level();
         player.hurtServer(level, level.damageSources().genericKill(), Tuning.ADRENALINE_DAMAGE);
      }
   }
}
