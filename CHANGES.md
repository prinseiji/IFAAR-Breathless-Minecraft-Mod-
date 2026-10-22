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

## Batch 11 - Stagnated polish
- **Emerge animation:** your "spawn/emerge" animation (7.9 s) plays when 5-L0 spawns one or when a Stagnated Head is used. Your emerge sound plays with it, and Warden-style debris (floor-block particles) plus sculk souls drift up. It cannot move, see or be hurt until it finishes.
- **Stagnated Head** (Spawn Eggs tab, `/breathless give` gives 4): right-click a block to place one with the emerge animation; sneak-click to skip the animation for testing. Icon cut from the face of your skin.
- **SLOTH flash:** when a Stagnated spots you, "I SEE YOU" or "I FOUND YOU" appears word by word (any exposure, ignores the normal cooldown).
- **Faster, weaker:** speed 0.25 x 1.2 chase, 20 HP (10 hearts), 3 damage (1.5 hearts) plus 15 exposure.
- **Detection:** wide and tall (24 blocks up and down, height ignored inside that), and a sprinting player is noticed from 1.5x as far.
- **Ribcage:** the rib cubes read the top-left corner of the skin, which was empty. It is now painted bone-white with a few sculk-teal flecks (rows 0-4, columns 0-7). Paint over it with your own art whenever.

## Batch 12 - tweaks
- **Speed** base 0.35 (x1.2 when chasing).
- **Detection** much wider: 24 blocks clean, up to 72 at full exposure, 10 behind a working mask with a clean meter; sprinting doubles it; it sees 40 blocks up and down.
- **Spot** now turns to face what it spotted (the pose raises its head and arm).
- **Echolocation:** when a sculk sensor near it goes active (tendrils moving), a vibration wave travels from the sensor to the Stagnated and it walks over to investigate. Scans 20 blocks sideways, 10 up and down; it never does this while chasing something.
- New animations converted and ready in StagnatedAnims (ARMORING 1.04 s, BREAKING_BLOCK 1.29 s, PLACE 0.63 s). Not used yet: the pickaxe / building / armor behaviours come next.
- All numbers: Tuning.java (STAGNATED_*).

## Batch 13 - armor role
- Every Stagnated now gets one role at random when it spawns (saved with it): armor, pickaxe, or builder. Only the armor role does anything yet; pickaxe and builder come in the next batches.
- **Armored:** the first time a player hits it, it stops for 1.04 s and plays your "armoring" animation; the iron armor (helmet, chestplate, leggings, boots) goes on at 0.67 s with the vanilla iron equip sound. It never drops the armor.
- The armor is drawn on shells that share the Stagnated's own skeleton (same bind pose and animations), built from its body cubes with vanilla armor UVs, so it should follow the hunched pose.

## Batch 13b - armoring crash fix
- Fixed the "Network Protocol Error" when hitting an armored Stagnated: vanilla's client treats entity event 63 as a Sniffer event, so my armoring signal (63) crashed it. It now uses event 70. (Spot = 61 and emerge = 62 were fine; 21, 35 and 63 are the ones vanilla reserves.)

## Batch 14 - pickaxe and builder roles
- **Held item:** the pickaxe / the sculk block is held in the left hand (off-hand, so it adds no attack damage). It is equipped the moment the Stagnated spots a player, with the vanilla armor-equip sound.
- **Pickaxe role:** when a player is close (within 14 blocks) but it cannot walk to them, it stops, plays your breaking_block animation (three swings, 1.29 s per round) and breaks the block in the way. Harder blocks take 2 or 3 rounds. It never mines anything tougher than hardness 8 (so no obsidian, no bedrock), nothing with an inventory or other block entity, and nothing underwater. Broken blocks drop nothing (Tuning.STAGNATED_MINE_DROPS).
- **Builder role:** same trigger, but it plays "place" and puts down sculk: a block to bridge a gap, or a step up when the player is higher. It gives up after 32 blocks.
- Both only work while the mob griefing game rule is on.
- **Testing:** `/breathless role <0-3>` sets the role of every Stagnated within 16 blocks: 0 none, 1 armor, 2 pickaxe, 3 builder.

## Batch 14b - smarter Stagnated
- **No more digging down:** a miner only breaks the block under itself when the player is below it; with the player overhead it no longer undermines itself.
- **Pillar building:** a builder with the player above and within about 4 blocks now jumps and places a sculk block where it was standing, over and over, one block at a time (a real tower). Steps and bridges are still used from further away.
- **No more standing around:** when the target cannot be walked to, it now walks straight at it up to the wall, ledge or pillar instead of idling. It also notices when it has been standing still while chasing (about a second) and treats that like "unreachable", so the pickaxe / builder behaviours kick in. It checks every 5 ticks instead of 10.

## Batch 15 - the IFAAR: Breathless advancement tab
- A new advancement tab, **IFAAR: Breathless**, with 29 advancements in your final wording (hidden ones stay hidden until earned; Lose Yourself., Breathless and Finally, Still. are challenges).
- No rewards, only the toast and chat message. Icons use existing Breathless items and vanilla sculk items.
- The Mask and Purple Rain trigger on crafting the recipes (any gas mask recipe; the reinforced filter recipe). Everything else is handed out by the mod. Finally, Still. counts kills in its own progress (10 steps, shown as a bar).
- Mt. Ebott, Yeah Mr. White!, Nightcrawler, Annihilation, Lose Yourself. (Moribund), The Thing That Hates (100%) and the two timers are checked once a second below / around Y=0. Creative and spectator players do not earn the survival ones.
- A Quiet Place: 5 minutes in a row below Y=0 without sprinting (leaving or sprinting resets it). Breathless: 10 minutes below Y=0 with the meter at 0 (any exposure resets it). Both reset on death or relog. Change them in Tuning (ACH_QUIET_TICKS, ACH_CLEAN_TICKS).
- **Behavior change:** a Stagnated now gives up on a player it can no longer sense for 5 seconds (Tuning.STAGNATED_GIVE_UP_TICKS) and goes back to wandering. Before, it kept the target until 128 blocks away, so "outrun it" was impossible. Run Rabbit, Run Rabbit needs a chase of at least 5 seconds where you sprinted.
- Hidden and special: The Breaking Point = a filter below 25%; Unwelcome Guests = a filter runs out below Y=0; IYEARNFORABODY / THEWEAKNESSOFFLESH = the F13ND purge (1 heart or less left); The Silence / error_log.txt = dying to spores / the sear; My Mind Of Many Ears = a Stagnated starts walking toward an active sculk sensor within 24 blocks of you; Hate Filled Amalgamate = a Warden kills a Stagnated within 32 blocks of you; Malignant Flesh = a Stagnated kills a zombie or skeleton within 32 blocks of you.
- Bravery = you kill an armored Stagnated; Determination = a miner breaks a block with you within 4 blocks; Perseverance = a builder has placed at least 2 pillar blocks and is within 4.5 blocks of you.

## Batch 15b - the Excavator (miner + builder merged), smarter Stagnated
- **Two roles now:** 1 = Soldier (armor, weapons come in 15c), 2 = Excavator (breaks AND builds). Odds are 50/50. Old saved builders (role 3) become excavators. `/breathless role <0-2>` (0 none, 1 soldier, 2 excavator).
- **Excavator order of thinking** (only when it cannot walk to its target, griefing on):
  1. Target above and the block it stands on is within 4.5 blocks of the Stagnated: break that block (a player on a 3-high tower is dropped without it destroying the tower).
  2. Target straight below: dig down.
  3. Target above and within 4.5 blocks sideways: pillar up with sculk, one block at a time.
  4. Otherwise break the first solid block in the way on the straight line to the target, in any direction (behind it too), head height first.
  5. Nothing to break: bridge a gap or lay a step with sculk.
- It swaps the held item between the iron pickaxe (breaking) and sculk (placing).
- **No more grass / veins:** it only breaks real solid blocks (anything with collision, hardness up to 8, no block entities). Plants, sculk veins, water and the like are ignored. A one-block step it can climb is never broken, and it never undermines the block it stands on unless it is digging down after a target below.
- **Builder fix:** it no longer insists on standing on a full block, which is why it stopped on the corner of its own bridge and fell. It also keeps one heading on diagonals instead of flipping axis every block, and its 32-block cap now resets for each new target instead of lasting its whole life.
- **Stuck-breaker:** if it has a path but stands still for a second while chasing, it re-paths; after two seconds it pushes straight at the target and jumps if it hits a wall.
- **Soldier armor now reacts to any attacker**, not just players (a zombie, a Warden, a skeleton arrow, anything living).
- New tuning: `STAGNATED_REACH` (4.5).

## Batch 15c - Soldier weapons, slowdown and the shield-break clap
- **Weapons (soldiers only):** each Stagnated rolls a weapon when it spawns, saved with it: 50% iron sword, 25% stone axe, 25% nothing. It takes it out in its main hand the moment it spots a target (vanilla equip sound), like the excavator's tools. Odds are `STAGNATED_SWORD_CHANCE` / `STAGNATED_AXE_CHANCE` in Tuning.
- **Damage:** a held weapon adds its own damage, so the base drops to 1: iron sword hit = 6 (3 hearts), stone axe hit = 9 (4.5 hearts). Bare-handed stays 3.
- **Slower once armored:** putting the iron on makes it 20% slower (`STAGNATED_ARMOR_SLOW`); holding the axe makes it another 15% slower (`STAGNATED_AXE_SLOW`). They multiply, so an armored axe soldier moves at about 68% of its normal speed. Saved games keep the effect.
- **Shield break (axe only):** if a player is blocking with a shield, the axe soldier stops, plays your "shield disable" clap (hoist at 0.42 s, clap at 0.5 s, 0.71 s total), faces you, and at 0.5 s lands the hit and knocks the shield down for 5 seconds with the vanilla shield-break sound. If you stop blocking during the wind-up it still hits you normally. Timings: `STAGNATED_SHIELD_TICKS`, `STAGNATED_SHIELD_STRIKE_AT`, `STAGNATED_SHIELD_DISABLE_TICKS`.
- **Testing:** `/breathless weapon <0-2>` turns every Stagnated within 16 blocks into a soldier and gives it 0 nothing, 1 iron sword or 2 stone axe right away.

## Batch 15d - left hand, shorter excavator reach, armed spot and unarm
- **Left hand:** everything a Stagnated carries now shows in its left (dominant) hand: the soldier's sword / axe and the excavator's pickaxe / sculk. The sword and axe are still its main-hand item underneath, so they keep their damage and the axe keeps the shield break.
- **Excavator reach is now measured like a player's:** from the eyes to the nearest point of the block, up to `STAGNATED_REACH` (4.5, the survival block reach). Before, it measured from its feet / block centers, which made it feel longer. If a blocker is further than that it walks closer first instead of mining from afar. It also starts pillaring only when the target is within `STAGNATED_PILLAR_RANGE` (3.0) sideways, down from 4.5. If it still feels long, set `STAGNATED_REACH` to 3.0.
- **Armed spot (soldiers with a sword or axe):** your "spots armed variant" animation (1.83 s) replaces the normal spot pose, and the weapon appears in its hand at 1.04 s with the equip sound. Bare-handed soldiers and excavators keep the normal spot.
- **Unarm:** when a soldier loses or finishes its target it plays your "unarm" animation (0.875 s, stands still for it) and the weapon is gone at 0.33 s. If it spots something again during it, it cancels and draws again. Damage and speed update when the weapon is put away.
- New tuning: `STAGNATED_SPOT_ARMED_TICKS` 37, `STAGNATED_DRAW_AT` 21, `STAGNATED_UNARM_TICKS` 18, `STAGNATED_UNARM_AT` 7.

## Batch 16 - lore documents
- **Four documents** (right-click to read, not used up): Researcher Mandel's Report (folder), McNally's Complaint (folder), UrG3Nt M4Il!!11 (red envelope) and To Whoever Finds This. (paper). They open in your open-folder art as a two-page spread with `<` `>` buttons (or the arrow keys) and a [ Done ] button. Text is in the language file (`breathless.lore.<id>.title / .date / .body`); `<r>..</r>` makes red text and `<k>..</k>` bold. Layout constants are at the top of `LoreScreen` if the text needs nudging on the folder.
- **Texts:** your wording with the grammar fixes, the Howard tweak (the strong tonic paragraph), one line about security iron kits in Mandel's report, "5-L0 (5-L0TH)" in Mandel's report, and day stamps (Mandel Day 1, McNally Day 63, Howard [RECOVERED] Day 118, Vincent none).
- **Recipes:** reading Mandel unlocks the reinforced filter, McNally the night-vision mask, Howard the strong tonic. Until then those three recipes cannot be taken out of a crafting table or the 2x2 grid (the result shows but cannot be picked up). Creative players are not locked. Droppers/crafters are not covered. Vincent's note unlocks nothing; it gives the hidden advancement "For Those Who Find These" (the title and description are my placeholders).
- **Where they come from:** a Stagnated killed by a player drops an unread document 15% of the time (Mandel, McNally or Howard, never one you already read). Vincent's note only drops from a Stagnated that dies while on fire (20%). Ancient city chests hold one of the first three now and then (15%). Tuning is at the top of `Lore`.
- **Testing:** `/breathless give` now includes all four; `/breathless lorereset` forgets what you read and locks the recipes again.
- The recipe lock is a small mixin (`RecipeLockMixin`). If it ever fails to apply the game still runs; the recipes are just not locked.

## Batch 16b - build fix
- 26.2 no longer has `Minecraft.setScreen`; the lore reader now opens with `setScreenAndShow`. That was the only compile error in batch 16.

## Batch 16c - lore polish
- **Text nudged down** on the folder (starts 6 px lower, 13 lines per page instead of 14 so it still ends above the sculk).
- **New icons:** Mandel's folder is the one you sent; Howard's letter is row 6, column 4 of the envelope sheet. McNally's folder is unchanged.
- **Letter backgrounds:** Howard's recovered letter now opens on your glitched paper (one page, text over it), Vincent's note on the burnt paper (one page, text starts below and right of the burn). Mandel and McNally still use the folder. Each has its own layout block at the top of `LoreScreen` (picture, text columns, start height, lines per page, button height) if anything needs nudging.
- **Names:** Mandel, McNally and Vincent's items have the purple (epic) name like the mace; Howard's is red.
