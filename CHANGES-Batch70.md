# Batch 70: the Carrion, sculk splotches, bigger Nidus cave

Paste this at the top of CHANGES.md.

## Carrion (new entity)
- Stationary sculk growth, 1.4 x 1.7 blocks, never despawns. Model, clips and texture from Carrion.bbmodel (idle, and the "death" clip is the swell). The texture is the one embedded in the model.
- It swells for 2.9 s and bursts. It goes off when: a player who is **not sneaking** walks, runs or jumps within 7 blocks; a player gets within 2.2 blocks (even sneaking); a sculk sensor within 6 blocks is going off; or anything hurts it (an arrow from far away sets it off far away). Only players set it off by noise. Creative and spectator players are ignored.
- The explode sound starts 22 ticks in, so its boom (1.8 s into the file) lands on the burst. Idle sounds play at random every ~10 s.
- The burst destroys nothing. `GasClouds` turns natural surface blocks (the sculk_replaceable tag) within 10 blocks into sculk, in a growing shell over about 12 ticks, and leaves a gas cloud (radius 7, 30 s) that takes 30 filter life every 10 ticks from each player inside (about 40 s for both filters). It fades over its last fifth.
- Fire: a Carrion that is burning when it bursts (flame arrow, fire aspect) leaves a thin cloud of only 5 s. Burning things inside a cloud (players, mobs, flame arrows) burn off 6 s of it every 0.5 s; fire blocks and lava inside burn off 10 s every second.
- Numbers to tune are at the top of `Carrion.java` and `GasClouds.java`.
- New: `Carrion`, `CarrionMarkerBlock` (placed by world generation, turns into the Carrion when it ticks, no item), `GasClouds`, client `CarrionModel`, `CarrionAnims`, `CarrionRenderer`, `CarrionRenderState`. Sounds `carrion_idle_1..3`, `carrion_explode`. Lang: `entity.breathless.carrion`.

## Where they generate
- `SculkPatchFeature` (three features): `carrion_deep` puts Carrions on existing sculk floors in the deep dark (abundant, max 2 per chunk); `carrion_patch` paints a sculk patch with a Carrion in the middle in other caves below Y=0 (rare, 1 chunk in 10); `sculk_splotch` paints a small sculk patch in other caves below Y=0 with nothing on it (uncommon, 1 chunk in 3). Registered in `ModWorldgen` with the Fabric biome API.
- Each try picks a random spot and height in the chunk and looks for a cave floor, so many tries find nothing.

## Nidus cave
- Bigger: about 42 wide, 30 deep and 14 tall (was 36 / 26 / 12). Anathemas at least 15 blocks apart (was 12), 10 tendons.
- Easier to find a spot: 4 directions x 3 sideways offsets x up to 3 cavern floor heights. The limit is still the 3 x 3 chunk area a feature may touch (about 44 blocks).
