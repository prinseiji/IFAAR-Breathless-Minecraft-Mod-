# Batch 69: the Nidus cave (code-generated, deep dark only)

Paste this at the top of CHANGES.md.

- New feature `NidusCaveFeature`: one big cave carved into the wall of a deep dark cavern (ancient cities are in the deep dark, so they are covered). About 36 wide, 26 deep, 12 tall, with a short passage out to the cavern.
- Sculk lining on floor, walls and ceiling, tendrils hanging from the ceiling, and 8 tendons (thin sculk columns from floor to ceiling, wider at both ends).
- Three Anathemas, at least 12 blocks apart (at least 2 if the cave is cramped), facing the exit. Each has 2 shriekers, 2 catalysts and 2 sensors around it, and there are 3 more sensors and 2 more catalysts scattered about. Shriekers can summon.
- Rarity: the world is split into regions of 12 chunks. Each region has a 40% chance of trying once, in one chosen chunk. If there is no cavern wall to carve into, nothing is placed. It never carves through water, lava or reinforced deepslate. Numbers to tune are at the top of `NidusCaveFeature.java`.
- Limit: a feature may only touch its own chunk and the neighbours, so the cave cannot be larger than about 44 blocks across.
- The old jigsaw pockets are switched off (`has_anathema_pocket.json` is now an empty list). The `.nbt` files, `structure`, `structure_set` and `template_pool` json can be deleted from the repo.
- Test command: `/place feature breathless:nidus_cave_here` while standing in a deep dark cavern. Try a few spots.
