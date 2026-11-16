# Batch 77: the Ire

Paste this at the top of CHANGES.md.

## Ire (new entity, an Odium variant)
- Lies half buried to its waist and never moves. 30 health, 1.2 x 1.0 blocks. Model, 11 clips and texture from Ire.bbmodel; ten sounds converted (death reuses the Stagnated one). Immune to the spores. Stagnateds do not attack it. It calls no reinforcements.
- **States:** IDLE (waiting, idle clip loops), AMBUSH (1 s, the grab lands at 0.17 s), PULL (pulling), TRAPPED (trapped loop), REACH (reaching loop), TRAP_ATTEMPT (0.54 s, grab at 0.25 s), ATTACK1, ATTACK2, ESCAPE (0.5 s). The hit and death clips play over any of them.
- **Ambush:** springs when a player comes within 4.5 blocks (3 if they are sneaking or silenced; Silence + Invisibility is ignored), or when it is hit from within 8 blocks. If the player is within 3.4 blocks when the grab lands, it pulls them in. If it misses, it is REACH.
- **Pull:** drags the player toward it (slow, 0.09 blocks per tick, and slower as the bar fills) with Slowness so they cannot just walk away. **Tap jump to fight it:** the bar above the hotbar fills 0.13 per tap and drains 0.008 per tick; full breaks you free and it goes back to reaching. Get more than 8 blocks away and it lets go.
- **Trapped** (pulled to within 1.4 blocks): held in place, sculk souls and spore eyes pour off it, and **exposure fills at 0.9 per tick (full in about 5.5 s) whatever mask you wear** (`Hazard.addExposure`). Tapping jump does nothing now; the bar is the damage you do to it, and 12 damage breaks you free (ESCAPE clip, you are let go at 0.2 s). The hint under the bar reads "Break free by hitting it." instead of the tapping text.
- **Reaching:** with a player within 4 blocks it acts every 1.5 s or so: 40% another trap attempt, 30% a one-swing attack, 30% a two-swing attack. The attacks add up to the same damage (`ATTACK_DAMAGE` 6, so the two-swing attack does 3 twice). Swings only land if the player is within 3.2 blocks. With nobody within 14 blocks for 8 s it settles back to idle.
- **Fakes (`IreHusk`):** lie in the Ire's idle pose (a single frozen frame), never move, and burst into the small gas puff when hit, like the Odium husk.
- **Spawning:** the Odium marker block now becomes an Ire 35% of the time (a fake 65% of those), otherwise an Odium or its fake as before. `/summon breathless:ire` and `/summon breathless:ire_husk` to test.
- New: `Ire`, `IreHusk`, `StruggleHolder` (shared by Odium and Ire for the bar and vignette), client `IreModel`, `IreAnims`, `IreRenderer`, `IreHuskRenderer`, `IreRenderState`. Numbers to tune are at the top of `Ire.java`.
