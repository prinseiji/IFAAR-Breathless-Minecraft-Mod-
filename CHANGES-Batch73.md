# Batch 73: the Odium

Paste this at the top of CHANGES.md.

## Odium (new entity, the last piece of the sculk ecosystem)
- A hunched lesser Stagnated. 24 health, 1.0 x 1.7 blocks. Model, all 13 clips and the texture come from Odium.bbmodel (the texture is the one embedded in it). Never despawns.
- **States** (synced, the client picks the clip): DORMANT (buried, the first frame of the emerge clip), EMERGE (7.92 s), PASSIVE (idle and wander/walk), SPOT (0.83 s, roar), HUNT (chasing), ATTEMPT (trap_attempt), SUCCESS (trap_attempt_success), FAIL (trap_attempt_fail), TRAPPED (looping), ESCAPE (trap_escape). The hit and death clips play over any of them. The `attack` clip is converted but not used yet.
- **Passive:** slow wandering (speed x0.4). It locks onto the nearest player (not creative, not Silence + Invisibility) when a sculk sensor within 12 blocks is going off, when a player not sneaking and not under Silence is within 4 blocks, or when a player hits it.
- **Hunt:** very fast (speed x1.7), keeps going until the player is 56 blocks away, dead, or cannot be reached for 5 s.
- **Lunge:** from 3.4 blocks (with line of sight and a short cooldown) it freezes, springs at 0.33 s and decides at 0.63 s: if the player is within 2.5 blocks it is a catch, otherwise a miss (2 s before the next lunge).
- **Catch:** at 0.13 s it steps up to the player and holds them: heavy Slowness (level 10) plus their motion cancelled every tick, so they cannot walk, sprint or jump away. It does no damage. Its trapped sound (11.3 s) plays, with a shriek effect, and it calls a Stagnated out of the ground 4 to 7 blocks away at 1.5 s and another at 7.5 s (none if there are already `STAGNATED_MAX_NEAR` around).
- **Letting go:** deal 8 damage to it while held (about 3 sword hits) or wait for the 11.3 s to run out. The escape clip plays and the player is freed at 0.67 s, with 4 s before it can lunge again, then it goes back to chasing. Killing it, or it being removed, also frees the player.
- **Spawning:** the `odium_deep` feature puts a buried Odium on sculk floors in the deep dark (rare: about 1 chunk in 6 tries, at most one per chunk, and most tries find nothing). A buried one rises when a valid player comes within 8 blocks or a sensor goes off nearby; it cannot be hurt while buried or rising. `/summon breathless:odium` gives a normal one.
- Sounds: `odium_idle_1..3`, `odium_spot_1..2`, `odium_trap_attempt`, `odium_trap_success`, `odium_trap_fail`, `odium_trapped`, `odium_escape`. Hurt, death and rising reuse the Stagnated sounds. Lang: `entity.breathless.odium`.
- Odium is immune to the spores (`MobRules`). Stagnateds no longer try to attack an Odium or a Carrion (`Stagnated.canSense`, one line).
- Numbers to tune are at the top of `Odium.java`.
- New: `Odium`, `OdiumMarkerBlock` (placed by world generation, no item), client `OdiumModel`, `OdiumAnims`, `OdiumRenderer`, `OdiumRenderState`.
