# Batch 74: photosensitivity fix, Odium fixes, spacebar struggle, spawn eggs, fake Odiums, Carrion gas look

Paste this at the top of CHANGES.md.

## Photosensitivity (exposure effects)
- The vision pulse that starts at 40% exposure was a pale white-blue wash over the whole screen plus a near-white vignette. It is now a dim blue-grey darkening at about a third of the strength, with a pulse that comes no more often than every 12 s (`SporeHud`).
- At 100% the gauge blinked bright white every quarter second. It now stays a steady, slightly brighter blue.
- The white flash when a mask cracks is now a faint grey-blue (was 4.5 times stronger and white). The red flash at the F13ND injection is under half its old strength.
- Not changed: the white screen flash when a parry lands in the Elijah fight (`MACE_PARRY_FREEZE_TICKS` in `Tuning.java`, and `EliBoss`), the totem pop flash, and the hard-mode error window particles. If any of those are a problem, say so and I will tone them down or remove them.

## Odium
- **Lunge is avoidable:** the lunge clip is played 1.6 times slower (`ATTEMPT_SLOW`), it turns to you for 4 ticks and then aims, so it goes where you were, not where you are. It springs at tick 11 and decides at tick 21. Moving away after it aims makes it miss. Range 3.0 (was 3.4), reach 2.2 (was 2.5), 3 s before the next lunge after a miss (was 2 s).
- **Its sounds stop when it dies** (and the held sound stops when you break free): the server sends a stop-sound packet for the trapped, trap success, trap attempt and roar sounds.
- **Held means held:** the drifting was the Odium and the player pushing each other. While it holds you (and during the catch) it cannot be pushed and does not push you, and it stands 1.05 blocks away (was 0.9).
- **Spacebar struggle:** tap jump to fill the bar above the hotbar (0.12 per tap, drains 0.01 per tick, so about 2 taps a second just to hold your place; each hit you land adds 0.15). Full = free. The spacebar icon presses in with each tap (`spacebar_in.png`, `spacebar_out.png`). The 11.3 s limit stays as a fallback. New: `StrugglePayload` (client to server), `Struggle` (client).
- **Sensor ping:** when an Odium (or a Carrion) hears a sensor going off, a trail of blue souls runs from the sensor to it, with a pop and a click where it lands (`SensorPing`).

## Fake Odiums
- New `OdiumHusk`: half buried, never rises, one hit (arrow, fire, explosion) and it bursts into a small gas puff (radius 3.5, 6 s, still wears filters). The spores and other damage with no one behind it do nothing to it.
- The Odium marker block now becomes a husk 65% of the time and a real buried Odium otherwise (`FAKE_SHARE`). `odium_deep` is placed twice as often (1 in 4) so real ones are about as common as before.

## Carrion gas
- Repo check: the repo still had the Batch 70 gas (green spore particles, the old filter wear). This batch includes the Batch 72 changes again (filter wear 80, `CLOUD_DRAIN`) and goes further.
- Colours are now blue (dark navy, mid blue) instead of teal. The body is spread evenly through the whole sphere, and the edge is a full shell of bright light-blue dots over the whole sphere, turning slowly, exactly where the filter wear stops.

## Spawn eggs
- Stagnated, Carrion, Sculk Anathema and Odium spawn eggs with your icons, in the Spawn Eggs tab. If the build complains about `spawnEgg`, send me the log.
