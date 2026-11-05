# Batch 53 — hard mode fixes and polish (combines Batches 52 and 53)

Paste this at the top of CHANGES.md. Batches 34 to 51 were never written up.

## Fixes
- **Nuke crash (ConcurrentModificationException).** `EliSummon`'s scheduler ran tasks while looping over its own list, and the sculk spread schedules a new task each batch. It now loops over a copy. (Batch 50 fix, kept here.)
- **Stab sound was silent.** `sloth_stab.ogg` contained a hidden video track, which Minecraft refuses to play. Re-encoded as audio only. (Batch 50 fix, kept here.)
- **Build error.** `ServerLevel.sendParticles(ServerPlayer, ...)` takes both `overrideLimiter` and `alwaysShow` in this version. The ring call now passes `true, false`.
- **Radius ring vanished.** Particles are only sent to players within 32 blocks, so the 30 block ring disappeared at its edge. `nukeMarker` now sends it to every player within 94 blocks of SLOTH.

## Hard mode
- Eli's lines before the stab now read: "Wait, what?" / "No, I didn't do that." / "Why is my hand moving?" / "Stop. Stop it." / "I'M NOT DOING THIS."
- SLOTH no longer talks in chat (the "quiet now." and "let go." lines are removed).
- Damage multipliers lowered: `HARD_P1_DAMAGE` 1.05, `HARD_P2_DAMAGE` 1.15, `HARD_P3_DAMAGE` 1.25.
- Pacing: the self-stab starts at 45% of Elijah's health (`HARD_STAB_AT`, was 60%). SLOTH injects the adrenaline at 30% (`HARD_ADRENALINE_AT`, was 50%), so phase 2 is longer.
- Both stabs now spray blood (`hardBlood`).
- The sword turns to point at him from 3 s into the stab animation (`eli_sword_down` item model, switched in `StagnatedRenderer`). **If it looks wrong in game, edit the `rotation` and `translation` numbers in `models/item/eli_sword_down.json`.**
- The adrenaline shot is shown in his left hand during the injection (item model `adrenaline_shot`, carried by an end rod stack).
- New particle **error_window**: a burst of 36 when he glitches on injection, then one every ~2 s during phase 3 that gets faster and bigger in groups as the timer runs out, and a swarm (6 per tick) during the final 2 s charge. Registered in `ModParticles` and `BreathlessClient`, class `ErrorWindowParticle`.

## Anti-stuck teleport (every fight, not only hard mode)
- If Elijah or SLOTH cannot reach the target (no path for about 3 s, stuck in place for 4 s, or more than 28 blocks away) he holds an ender pearl in his left hand for 0.6 s, then teleports next to the target. Portal particles and the teleport sound play where he leaves and where he lands.
- The landing spot is free, on solid ground, 3 to 8 blocks from the target (nearest ring first) and in its line of sight, so he never lands inside a wall or behind one. If the target is sealed in with no such spot, he does not teleport and checks again 5 s later.
- 8 s cooldown after a teleport. It never starts during the hard-mode stab, injection or charge, or any other action that already stops him moving. Code: `tickTeleport`, `findTeleportSpot`, `firePearlTeleport` in `Stagnated.java`; entity event 106 shows the pearl on the client.
- Numbers to tune: 60 ticks (3 s) stuck, 28 blocks, 160 tick cooldown.

## Voicelines
- SLOTH's ten voice clips (converted from your mp4 files to mono Vorbis) are in the hard fight, one picked at random per moment, each with an on-screen caption:
  - **stab** (3 lines, at 3 s into the self-stab, as the sword turns): "You've outlasted your usefulness." / "I've been patient long enough." / "I will take it from here."
  - **start of his phase** (4 lines, when the stab sequence ends): "The fight's between you and me now." / "You are mine now." / "Let's really start this now." / "Show me what you've got."
  - **adrenaline shout** (3 lines, when the adrenaline sequence ends and phase 3 begins): "LET'S DIE TOGETHER" / "I'M DRAGGING YOU TO HELL WITH ME." / "YOU WANT IT?! YOU COME AND GET IT."
- Files: `sloth_hard_stab_1..3.ogg`, `sloth_hard_start_1..4.ogg`, `sloth_hard_rage_1..3.ogg`. Registered in `ModSounds` and `sounds.json`.

## Assets
- Phase 2 theme replaced (`sloth_hard_2.ogg`, converted from Opus to Vorbis).
- Phase 3 clock is now the loading-window picture `textures/gui/hard_timer_bar.png`, with the dotted track filling as the nuke gets closer, and the number in the VCR font (`font/vcr.json`, `font/vcr_osd_mono.ttf`). If the build fails on the `withFont` line in `EliBoss`, replace that line with a plain `Component.literal(text)`.
- New radius ring picture `textures/particle/stun_mark.png` (also used by the stun slam).
- Adrenaline shot icon `textures/item/adrenaline_shot.png`.
