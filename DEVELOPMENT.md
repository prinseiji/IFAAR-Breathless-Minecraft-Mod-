# IFAAR: Breathless — technical notes

Expansion for IFAAR (mod id `combatinjuries`). Mod id `breathless`, package `com.kodari.breathless`.
Standalone jar, requires IFAAR at runtime. Built with the same versions as IFAAR (MC 26.2, Loom 1.18.2, Fabric API 0.161.0+26.2, Java 25).

## How it hooks into IFAAR
Only `CombatInjuriesApi.RUSH_STARTED` is used (adrenaline while critical clears the meter and deals 4 hearts).
`src/main/java/com/kodari/combatinjuries/` holds a compile-time copy of the API plus a stub `CombatInjuries` class.
`build.gradle` excludes it from the jar, so the real IFAAR classes are used at runtime.
**If IFAAR's `CombatInjuriesApi.java` ever changes, copy the new version over the one in this project.**

## Files
- `Tuning.java` — every number (fill time, fade time, damage, filter life, drop chances, skeleton penalties).
- `Hazard.java` — exposure meter, critical state, cure rules, adrenaline hook. Meter is a persistent player attachment (`Breathless.EXPOSURE`), reset on death.
- `MobRules.java` — spawn restriction below Y=0 (`ServerEntityEvents.ALLOW_LOAD`), zombie masks + drops, zombie spore damage, skeleton detection range, arrow trails.
- `Masks.java`, `ModItems.java`, `FilterItem.java` — items and helpers.
- `ExposurePayload.java` — server to client meter sync.
- `mixin/HealMixin`, `mixin/SkeletonAimMixin`, `client/mixin/FogRendererMixin` — all OPTIONAL (require 0). Copied from the working Sculkbreath jar's targets.
- `client/SporeHud.java`, `Tendrils.java`, `ClientState.java` — haze, visor overlay, meter, critical visuals.

## Rules as built
- Spore zone: overworld, player Y < 0. Meter rises only without a working mask (mask on AND filter life left). 180 s from 0 to 100 standing, 2x when sprinting or airborne. Never falls on its own.
- Filter: 1200 points, 1 per 10 ticks below Y=0 while worn (2x sprinting) = 10 min. At 0 the mask turns into "Gas Mask (No Filter)".
- Critical (100): wheeze loop (every 70 ticks), 1 drowning-type damage every 25 ticks, screen fades to black over 20 s, veins + vignette.
- Cure needs mask with filter AND Instant Health. Mask first then heal: meter drops 60 at once. Heal first: remembered 2 min, applied when a working mask goes on. Heal with mask when not critical also drops the meter by 60.
- Adrenaline shot while critical: meter to 0, 8.0 genericKill damage (not in creative).
- Zombies (exact `minecraft:zombie`, fresh spawns below Y=0) get a mask: 90% of the time 3-25% filter left, 10% 25-50%. Hits wear the mask; at 0 it breaks. Zombies without a working mask take 1 spore damage per second. Drops: 70% mask, 20% loose filter, 10% nothing.
- Skeletons: immune, detection range x0.6 while below Y=0, +7 arrow spread (mixin), white/cyan particle trail on their arrows.
- Natural/chunk-gen spawns of hostile mobs below Y=0 other than zombie/skeleton are cancelled. Spawners, commands, reinforcements are untouched.

## API names: where they came from
VERIFIED against Fabric API source (branch 26.3, close to 26.2): HudElementRegistry/VanillaHudElements, GuiGraphicsExtractor (fill, blit, text), PayloadTypeRegistry.clientboundPlay, ClientPlayNetworking.registerGlobalReceiver, ServerPlayNetworking.send, ServerEntityEvents (ALLOW_LOAD with spawn reason), ServerLivingEntityEvents, ServerPlayerEvents, AttachmentRegistry.createPersistent, CreativeModeTabEvents, CommandRegistrationCallback, Item.Properties.setId, Equippable.builder, hurtServer, InteractionResult.
VERIFIED from the working Sculkbreath jar: FogRenderer.setupFog / FogData fields, HealOrHarmMobEffect.applyInstantaneousEffect, AbstractSkeleton.performRangedAttack + spawnProjectileUsingShoot arg 7, MobEffects.INSTANT_HEALTH, Item.Properties.durability.
NOT verified (best guesses, first suspects if the build fails): `DamageSources.drown()/genericKill()`, `ServerPlayer.displayClientMessage`, `playNotifySound`, `Entity.isLoadedFromDisk()`, `EntitySpawnReason.NATURAL/CHUNK_GENERATION`, `PotionContents.getAllEffects()`, `ServerLevel.sendParticles`, `ParticleTypes.WARPED_SPORE/END_ROD/SOUL_FIRE_FLAME`, `MinecraftServer.getTickCount()`, `Entity.addTag/removeTag/getTags`, `CameraType.isFirstPerson()`, `SoundEvents.GLASS_BREAK`, `Level.getRandom()`, `Equippable.Builder.setDamageOnHurt`.
Nothing here has been compiled against Minecraft or played. Only a syntax-level javac check was run.
