# Changes
## Round 1 (first build)
Everything. See DEVELOPMENT.md.

## Batch 1 (untested)
- Masks: both are named "Gas Mask". A mask holds 2 filters (needs both). Percent lines under the name, click filters onto a mask in the inventory, right-click a mask to take one out.
- 11 new sounds hooked up (mask on, filter in/out, filter spent, 50%/25% cracks, tendrils, spore damage, sigh of relief, exposed, muffled/open ambience) + pop-up sounds.
- Fog: smooth 12-block fade, denser and darker with depth, blue. Mist layer of particles on the Y=0 line.
- Particles: blue dust; they stream toward you when the mask is off. Floating spore sprites on screen from your sheet.
- Exposure: 90 s to fill, Slowness I-IV by exposure, milky vision pulses, damage ramps up while critical.
- Visor: cracks only at the edges, no splotches, instant with a flash and shake.
- Zombies: much rarer drops, masks hold 2 weak filters.
- Gauge: your biohazard gauge replaces the bar (background removed).
- Adrenaline Shot while critical: one-time cure through IFAAR's BEFORE_SHOT hook, no rush. F13ND pop-up sequence with sounds, red vignette, final "event run unsuccessful" pop-up.
- `/breathless fiend` to test the pop-ups; `/breathless give` now gives masks with filters.

## Batch 2 (untested)
- Fog: thick unmasked, about twice the sight range when masked.
- Gauge smaller; on-screen spore sprites removed; real particles are now tiny blue motes and bigger, brighter mist along Y=0 (denser, down to 5 blocks below the line).
- Unmasked spores are pulled to your face (the pull was being cut to a tenth by the particle type).
- Tendrils: thin, smooth strands instead of blocks.
- F13ND: red vignette fades in; pop-ups half the size; final pop-up in the middle of the screen; trimmed 0.6 s of silence off the start of the final sound (that was the delay) and 0.2 s off the sigh.
- Sounds: ambience, damage sound and critical sounds now also play in creative (damage itself does not); "exposed" cough removed.
- Instant Health purges by level: I = 30, II = 60, III+ = 100.
- Adrenaline Shot: IFAAR's rush only works at 0 exposure; with any exposure the shot is the F13ND cure.
- FOUND the cause of the silent sounds: the mp3s had cover art, so the converted OGGs had a hidden video stream and Minecraft would not play them. All sounds re-converted without it, and raised in volume (several were very quiet).
- Adrenaline cure: if the 4-heart hit is swallowed for any reason, health is taken directly; overlay message added.

## Batch 3 (untested)
- Ambience: now glued to you (centered, no panning, doesn't fall behind when you walk). Unmasked is loud (1.0), masked is muffled and quieter (0.55). Volumes in Tuning.
- Sigh and spore-damage sounds quieter (0.6).
- Taking a filter out of a mask now plays the same sound as putting one in.
- Gauge smaller (0.30).
- Y=0: a brine-pool surface. A flat, bright, perfectly still sheet of particles exactly on the line (they are held still so the edge stays sharp), with thinner darker gas hanging below it.
- Unmasked spores: now "fly toward a point" particles (enchanting-table style) that stream into your face. Dust could not do it, it lived about half a second.
- F13ND: pop-ups also cover the % label under the gauge, and narrow ones can't leave the bar peeking out. They now peel off one by one (newest first, one every 2 ticks) as the red vignette fades out, revealing the empty bar. Then a short pause, then the final pop-up.
- New F13ND timeline (ticks, 20 = 1 s): first pop-up 4, last lands 37, peeling starts 50, bar empty at 74 (3.7 s), final pop-up at 90 (4.5 s), over at 145 (7.25 s). All in Tuning.

## Batch 4 (untested, includes batch 3b)
- New sounds: syringe injection (trimmed to its 2 s of real sound, replaces IFAAR's inject sound for the cure) and your 6.3 s build-up.
- F13ND sequence rebuilt around the build-up: red flash + syringe at the injection; at 0.5 s the build-up sound starts together with the animated loading pop-up (middle of the screen); the cover pop-ups start slow and speed up; the bar slowly drains while the % number warbles; at 6.8 s the bar is empty and ALL pop-ups vanish at once while the red vignette fades out; short pause; final pop-up at 7.6 s; over at 10.35 s.
- Loading pop-up: your GIF, 30 sampled frames in a sheet (textures/gui/popup_loading.png), one frame per 3 ticks.
- Everything is in Tuning (BUILDUP_START_TICK, BUILDUP_TICKS, POPUP_SPEEDUP, WARBLE_PERCENT, volumes).

## Batch 5 (untested)
- Loading pop-up much smaller (110 x 80).
- Fog: right under Y=0 the air is already a little foggy (30% strength) and builds to full over 24 blocks, instead of fading in over 12 blocks. The surface skin is denser and its particles bigger, so you can't see into it from above.
- Spores: they were sinking 1.2 blocks at the end of their flight (that is built into this particle), so they landed on your legs. Target is now raised to your head, shifted ahead by your own movement, and the flight is shorter (Tuning.SPORE_STREAM_TICKS = 14) so they keep up when you run.
- Sigh quieter (0.35). Filter removal still uses the insert sound until your unscrewing sound arrives.
- NEW: "Spore Exposure" status effect (third-row, third heart). Shown whenever the meter is above 0, even outside the spores; level goes up every 25%. Re-applied every 2 s, so milk or /effect clear can't remove it without clearing the exposure.
- NEW: filter durability meter under the gauge: your two filter icons drawn at 2x with a green/yellow/red bar and % under each. Shows while a mask is on and you are below Y=0, and for 8 s after a mask goes on or a filter goes in/out.
- NEW: 5-L0 (SLOTH) text. Silent stacked letters in sculk blue that quiver every tick and fade in/out. Only while exposure is above 0. Lines: LEAVE (first contact / walking back down), BREATHE 25%, STAY STILL 50%, ALMOST 75%, JOIN US / DON'T RESIST at critical, TOO LATE / WE ARE STILL HERE / CLEVER with a mask on, LOUD / STOP / WALK / NOISE / QUIET while sprinting. Chance-based with a 25 s cooldown and no immediate repeats (all in Tuning). Multi-word lines are spread: one tower per word.
- During a purge: one line from IT BURNS / THE FIEND / STOP / CHEATER / MINE / YOU (40% chance of a second), glitching from blue to red as the bar drains, on the right side of the screen, cut off when the bar is empty.

## Batch 6 (untested)
- Status effect icon bigger (the heart now fills the icon). Provisional name: "Calcification" (levels I-V).
- Filter meter: smaller (your art at its own 16 px size), placed right next to the gauge, level with its middle, so it can never run off the bottom of the screen.
- Loading pop-up: re-made from the GIF at 3x resolution (it was being shrunk too hard), and moved right and up, away from the cover pop-ups.
- Spores now converge on a point just in front of your face (not above your head) so they never block your view; still lead your movement.
- SLOTH purge text sits further right (80-96% of the screen width).

## Batch 7 (untested)
- Loading pop-up now uses your 4 static frames (loading. / .. / ... / ....) and cycles them like a GIF, one change every 7 ticks. Drawn at half size (103x42), same style as the cover pop-ups.

## Batch 8 (untested)
- **Real Y=0 sheet.** A flat, see-through, sculk-cyan sheet is now drawn exactly at Y=0 (new file `LevelSheet.java`, registered in `BreathlessClient`). It uses the game's own flat-quad drawing, as a tile grid around you that fades with distance so the edge melts away, and it shows from above and below. It is strongest when you are near the line and fainter further away. The surface dust is cut down to a light shimmer.
- **SLOTH text now uses Minecraft's own font**, scaled up by whole numbers, with a soft glow. Quiver, chance rules and the blue-to-red purge glitch are unchanged.
- This batch was checked against the real 26.1 game files (names and call shapes), but not run.

## Batch 9 (untested)
**Big batch. Drag `src` and `CHANGES.md` into the Breathless repo only.**
- **Masks (6 items, one name "Gas Mask" / "Night Vision Gas Mask").** Plain, reinforced (purple band), empty, and the same three with the green night vision lens. The game swaps to the right one by itself when the filters change; the band shows what is inside: yellow = ordinary filters, purple = only reinforced filters, none = empty. Your new worn textures and icons are in.
- **Night vision mask:** hidden Night Vision below Y=0 while worn (no icon, no potion particles). Recipe: empty mask + 2 glow ink sacs (shapeless). Take the filters out first, crafting returns an empty NV mask.
- **Reinforced filter:** one filter + 2 amethyst dust + 2 charcoal dust (shapeless), comes out at full life. Wears down half as fast. Purple filter icon in the HUD meter (`filters_reinforced.png`) and a purple tooltip line.
- **Dusts:** 1 amethyst shard -> 2 amethyst dust. 1 charcoal -> 2 charcoal dust.
- **Basic filter recipe:** now copper ingots instead of iron ingots (iron nuggets and charcoal stay), and makes 2 filters per craft.
- **Spore tonics (drinkable, give the glass bottle back):**
  - Lesser Spore Tonic: honey bottle + 2 oxeye daisies. Clears 20 on the meter.
  - Strong Spore Tonic: honey bottle + 2 lily of the valley + 1 oxeye daisy. Clears 45.
  - They follow the same rule as Instant Health: with a working mask on they clear the meter now; without one they do nothing unless you are at 100, where the cure is remembered for a short while until you put a mask on. Drinking with no mask and below 100 tells you it does nothing.
  - Instant Health potions and the Adrenaline Shot are unchanged.
- **Nearby sculk rule.** Once a second the game counts sculk around your head (6 blocks) but only the sculk the spores can actually reach through open air, so sculk behind a wall or floor does not count and walling a room off protects you. Sculk block or vein = 1, catalyst / shrieker / sensor = 4. Score 8 = exposure fills 1.5x, 30 = 2x and filters wear 2x, 80 = 3x and 3x. All in Tuning (`SCULK_*`).
- **Effect names:** "Stagnation" (was "Calcification"). Next to heavy sculk (score 80+) the same marker shows as **"Moribund"** with the cracked-heart icon you picked (new registered effect `breathless:moribund`).
- **Filter removal sound:** your unscrewing sound is now used when you take a filter out.
- **Teal eyes:** the three 6x6 eyes are now two particles. Small ones hang in the air all around you underground and glow; larger ones rush toward your face when you have no mask (replacing the conduit-style ones). The dust motes are fewer, the mist puffs along Y=0 are unchanged.
- **Y=0 sheet:** stronger and in your pick of colour (29DFEB, alpha up from 0.30 to 0.45). Surface shimmer matches.
- **Fog:** keeps getting darker with depth but is already noticeably dark at Y=0 ("submerged").
- **Filter meter:** now in the bottom-left corner of the screen.
- **Creative tabs:** masks and filters in Combat, dusts in Ingredients, tonics in Food & Drinks. `/breathless give` hands out all of it.
- Checked against the real 26.1 game files where the call shapes could be (item/consumable, particles, block names), but not compiled or run.

## Batch 10a
- Filter-meter icons no longer cropped (cut exactly 15x15 from row 5 of the sheet).
- Tonics weaker: lesser purges 10, strong purges 25 (Tuning.TONIC_*).
- Own damage types: spores -> "% bursted by the seams."; the adrenaline cure's 4-heart hit -> "% crashed due to an unexpected issue." Both bypass armor.
- Sear plays a "tsss" (vanilla fire-extinguish placeholder; swap in your own later).
- Dying stops all Breathless sounds; the HUD effects clear the moment you die.

## Batch 10b - the Stagnated
- New mob `breathless:stagnated` (try `/summon breathless:stagnated`). Model, bones and all six animations come from your Stagnated.bbmodel (converted into Minecraft's own model code, no GeckoLib); skin = your PNG.
- Behaviour: wanders and idles; on first lock-on it freezes for about 0.8 s ("spots" pose + spot sound, then the growl) and chases (faster than walking, slower than sprinting). Chase hit = 2 hearts + 15 exposure.
- Notices players from about 16 blocks (48 at full exposure; 6 if you wear a working mask with a clean meter). Also attacks animals/villagers within 12 blocks, and zombies/skeletons only within 6 (players always come first). Ignores wardens and other Stagnated.
- 30 HP, drops nothing, hurt/death/idle/hit sounds from your files.
- Spawns only by 5-L0: below Y=0 in heavy sculk (Moribund tier) or at 100% exposure, about once per 45 s of rolling, 90 s cooldown, at most 2 within 64 blocks, out of your line of sight.
- Not done yet: the SLOTH text flash on spotting, lore drop.
- All numbers are in Tuning.java (STAGNATED_*).
