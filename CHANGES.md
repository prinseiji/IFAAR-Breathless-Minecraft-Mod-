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

## Batch 16d - Stagnated advancements for two roles
- **Perseverance is removed.** With the miner and builder merged into the excavator, **Determination** now covers it: an excavator breaks a block with you within 4 blocks, or has placed 2 pillar blocks and is within 4.5 blocks of you. Description: "Let a Stagnated excavator dig or build its way to you. My hands may grow weak, but never my resolve." **Bravery** (kill a Stagnated in iron armor) stays the soldier's.
- **Delete this file from your project** (a zip cannot remove it): `src/main/resources/data/breathless/advancement/perseverance.json`. Otherwise Perseverance stays in the tab as an advancement that can no longer be earned.

## Batch 16e - advancement tab background
- The Breathless tab now tiles your teal / dark / white block pattern as its background (`textures/gui/advancements/backgrounds/breathless.png`, set on `root.json`). I rebuilt the 16x16 tile from the block picture you sent (left face), so it is a close copy rather than your exact file. Send the flat 16x16 texture and I will swap it in, or just overwrite that png yourself.

## Batch 17a: the sweaty Stagnated
- About 1 in 150 soldiers is a diamond-armored Stagnated named xX_5L0TH_Xx with a mace (normal speed, no armor slowdown).
- Wind charge launch toward the target, then a mace smash on landing (damage 4 + 1.6 per block fallen, capped at 12 = 6 hearts, knockback, mace smash sounds, gust particles). With no target it does the water bucket clutch instead (water placed at 0.21 s, picked up at 0.42 s, only with mob griefing on). No fall damage ever.
- New animations: mace_smash, windburst, waterbucketclutch, airborne.
- Kill it with a player: mace always drops, each armor piece 85%, all at about half durability.
- Test with /breathless sweaty. Numbers are in Tuning (SWEATY_*).

## Batch 17b: sweaty attack pattern (and the knockback build fix)
- The sweaty Stagnated now cycles: diamond sword melee (120 ticks or 4 hits) -> swaps to the mace (reuses the unarm animation) -> wind charge launch from any distance -> smash, or water clutch if the target is more than 3.5 blocks from where he is landing -> swaps back to the sword. The mace never melees.
- Stronger steering in the air. Smash knockback no longer uses LivingEntity.knockback (changed in 26.2).
- Tuning: SWEATY_SWORD_TICKS, SWEATY_SWORD_HITS, SWEATY_SMASH_REACH. SWEATY_RANGE_MIN and the cooldown are gone. The sword does not drop; the mace and armor still do.

## Batch 17c: stun slam and blocked smashes
- Smash sound and particles now only play if the smash actually hurt someone. If a shield soaks it, he takes the fall damage himself (ceil(fall - 3), reduced by his armor) and nothing plays.
- Stun slam: each time he goes for the mace there is a 35% chance (70% if the target has a shield up) that it is a stun slam. He holds a diamond axe and flashes red from the wind-up through the fall, the axe turns into the mace on landing, and a raised shield is disabled (100 ticks) before the damage lands, so it connects. Tuning: SWEATY_STUN_CHANCE, SWEATY_STUN_CHANCE_SHIELD.
- The glow is a red outline like a spectral arrow (isCurrentlyGlowing + getTeamColor overrides while stunning).

## Batch 17d: smash fix and enchanted mace
- A smash only counts as blocked when a raised shield soaks it. Before, any hit that took no health (maxed resistance, absorption) counted as failed, so he skipped the smash sound and hurt himself.
- The dropped mace comes with Breach II and Unbreaking III.

## Batch 17e: Ethan on the surface, Stagnated wither above Y=0
- The sweaty one now also turns up above ground: each second a player is on the surface, 1 in 1800 (about once per half hour of surface time), then a 10 minute cooldown, only one near a player (96 blocks), 20-30 blocks away, no emerge animation. Tuning: SWEATY_SURFACE_ODDS, SWEATY_SURFACE_COOLDOWN_SECONDS.
- Every other Stagnated now withers when it is at Y=0 or above (overworld): 3 seconds grace, then 1 heart per second with sculk souls. Ethan is exempt. A Stagnated Head used on the surface will wither too. Tuning: STAGNATED_SURFACE_GRACE_TICKS, STAGNATED_SURFACE_DAMAGE.
- The dropped mace is named "Elijah's Mace".

## Batch 17f: Ethan behavior
- Golden apple: at 40% health or less he stops and eats for 32 ticks (standing still), then gets Regeneration II (5 s) and Absorption. At most 2 per fight (refills after 10 s without a target). Tuning: SWEATY_EAT_BELOW, SWEATY_APPLES, SWEATY_EAT_TICKS.
- No spore exposure from his sword or his smash.
- His name tag is always visible, like a mob's.
- Death messages: "<player> had their skull caved in by xX_5L0TH_Xx" (mace and stun slam), "<player> had their guts spilled by xX_5L0TH_Xx" (sword). New damage types ethan_mace and ethan_sword. His sword hit now goes through its own code (damage from his attack attribute, small knockback).

## Batch 17g: Ethan's voicelines
- 22 clips converted to mono .ogg (assets/breathless/sounds/ethan_*.ogg) and listed in sounds.json as six events: ethan_spot, ethan_wind, ethan_smash, ethan_stun, ethan_clutch, ethan_death. Weighted so the "soul" lines are rare (stop, don't hurt them: about 13% of stun slams; finally.. / thank you..: about 17% of deaths).
- Plays on spotting a target, at the wind charge wind-up (stun slam wind-up uses the stun lines instead), at the water clutch, when a mace smash lands (not on stun slams), and as his death sound. One line at a time (1.5 s gap).
- To swap or add a line: drop a mono .ogg in the sounds folder and add it to that event in sounds.json (weight sets how often).

## Batch 17h: the mace he holds is the enchanted one
- Ethan's held mace now has Breach II, Unbreaking III and the name "Elijah's Mace" (so it glints in his hand), and the drop is a copy of it at about half durability.

## Batch 17i: Breach for real
- The smash now counts the victim's armor as 30% weaker (Breach II), so it hits armored players harder. Tuning: SWEATY_BREACH.

## Batch 18a: Ethan as a boss, balance
- Ethan has 80 health of his own (set when he gears up; Ethans spawned before this keep their old health). Tuning: SWEATY_HEALTH.
- Stun slam launches higher (1.5 instead of 1.1, needs 12 blocks of room) so there is time to get away, and its damage cap is 16 (8 hearts). Tuning: SWEATY_STUN_LAUNCH_UP, SWEATY_STUN_MAX.
- Weaker steering in the air for every launch. Tuning: SWEATY_STEER, SWEATY_STEER_CAP.
- Before eating a golden apple, if you are within 8 blocks he hops away with a wind charge (no smash, he can still clutch) and eats where he lands. Tuning: SWEATY_EAT_SAFE, SWEATY_FLEE_SPEED.

## Batch 18b: dodgeable smashes, clutch water fix
- He steers only while rising. At the top of the arc he locks his landing spot (the target's position at that moment) and falls on a fixed path, so moving away then works.
- The stun slam draws a ring of red dust on the ground where he will land.
- The smash area is smaller (SWEATY_SMASH_RADIUS 2.2, was about 3).
- Clutch water: the spot where he placed it is now remembered, so he always picks up the same block (before, the position was recalculated while he fell and the water stayed behind). Water is also removed if he dies or despawns mid-clutch.

## Batch 18c: Ethan leaves
- When the player he was fighting dies (or disconnects), he says "<xX_5L0TH_Xx> gg ez" in chat, waits 1.5 seconds, then the yellow "xX_5L0TH_Xx left the game" appears and he despawns with a puff. This stops a second one from being spawned by accident. Tuning: SWEATY_LEAVE_TICKS.
- When he dies, the same yellow "left the game" line shows.

## Batch 18d: Ethan's shield, plus the ring and leave fixes
- Shield: after he has been hit twice within 3 seconds he raises a shield in his sword phase and keeps it up. It only drops for a moment around each of his swings (so: swing, shield, swing, shield). A raised shield blocks hits and arrows from the front (shield sound). A player's axe hit breaks it: he lowers it and cannot raise it again for 5 seconds; after that, two more quick hits make him raise it again. The shield shows in his left hand in place of the sword while it is up (no blocking animation yet). Tuning: SWEATY_GUARD_*.
- Fix: the red ring for the stun slam was never drawn because the ground search only looked 12 blocks down and the top of his arc is higher than that. It now looks 40 blocks down, and the ring has more and bigger particles.
- Fix: he never left after a win. Nothing ticks near a dead player, so the old 1.5 second countdown never finished. He now says "gg ez", shows the yellow "left the game" line and despawns in the same moment (the leave-delay tuning value is gone).

## Batch 18e: shield animations, hands, wider stun slam
- New clips: shieldup (raising it), shieldup(poseoneframe) (held up), shielddisabledforhim (axe breaks it), shieldupwithhit (blocked a hit). The raise, hold and block clips only drive his head, body and arms, laid over his walking, so his legs keep moving. The break clip is the whole body.
- His shield is in the right hand, the sword stays in the left.
- The stun slam hit area is wider: SWEATY_STUN_RADIUS 3.0 (the plain smash stays 2.2). The red ring uses it too.
- The enraged animation is in the bbmodel but not used yet.

## Batch 18f: shield sounds
- A hit on his raised shield plays the shield block thunk; an axe breaking it plays the shield break sound plus a tool-break crack. Both are played from the server so the attacker hears them (the old entity events were not reliable).

## Batch 18g: Ethan's phase two (enrage)
- His first lethal hit does not kill him (totem style). He goes invulnerable and plays the 5.5 s enraged animation: he eats an enchanted golden apple (gone at tick 45), the diamond armor is swapped for netherite (tick 70), the ENRAGED sound and one of six random voicelines play at the start. Then he comes back with half of his max health and Regeneration II / Absorption IV. /kill and the void still kill him. Test with /breathless enrage.
- Phase two: shorter sword phases (60 ticks or 2 hits), 60% of heavy attacks are stun slams, stun slams use the plain smash radius again (2.2), and he stops talking until he dies (the death voicelines play then).
- Drops: the mace as before, and the armor he is wearing (netherite), no diamond set. All at about half durability, 85% each.
- His outline is now a constant dark sculk teal for as long as he is Ethan (diamond or netherite helmet), replaced by red during the stun slam warning.
- Tuning: SWEATY_ENRAGE_*, SWEATY_ENRAGED_*.

## Batch 18h: glow only in phase two, totem pop
- The teal outline only shows in phase two (the red stun slam warning still replaces it). Fixed my misreading of the earlier request.
- Entering phase two plays the vanilla totem of undying pop (particles and sound) on him.

## Batch 19a: Ethan, harder (gear and effects)
- His sword is netherite with Fire Aspect: a player he hits burns for 4 seconds. (Both phases.)
- His armor, diamond and netherite, has Protection II (the dropped pieces keep it). Tuning: SWEATY_PROTECTION. Ethans spawned before this keep their old gear.
- Phase two: Strength I and Speed I for the rest of the fight (24000 ticks), with the vanilla effect swirl. Tuning: SWEATY_STRENGTH, SWEATY_SPEED (0 = level I, 1 = II).

## Batch 19b: chain slams and the lunge feint
- Phase two: after a slam lands, if his target is within 6 blocks he winds up again right away (no sword phase between), once per heavy phase. The second slam can be a stun slam too. Tuning: SWEATY_CHAIN_MAX, SWEATY_CHAIN_RANGE.
- Lunge feint (stun slams): with about half a second of fall left, if the target has dodged the red ring but is still within 4 blocks of it, he lunges mid-air: the ring jumps up to 4 blocks toward where the target is standing right then, and he comes down on the new ring. Always in phase two, 40% in phase one; at most once per slam. A whoosh and a gust mark the lunge. If you keep moving or run far, he misses (or clutches). Tuning: SWEATY_LUNGE_*.
- The lunge reuses his airborne pose; there is no separate arm thrust animation.

## Batch 20a: Elijah (new skin, own spot pose, rename)
- Ethan is now Elijah (Eli): the dropped mace is "Elijah's Mace". Internal ids (ethan_* sounds and damage types) are unchanged so nothing breaks.
- He has his own skin (textures/entity/elijah.png), without the Stagnated's body strips, and his own spot pose (1.42 s). Everything else animates as before.

## Batch 20b: slam readability, shield hold (chain and lunge reverted)
- The chained slams and the lunge feint are gone again. The netherite Fire Aspect sword, Protection II and Strength/Speed stay.
- The red stun-slam ring now shows from the launch: on the way up it follows his target, then freezes at the top of the jump where he will land.
- While he falls onto the ring, a pling sounds at the ring, rising in pitch and repeating faster the closer he gets, so you can hear it even when he is above you.
- With his shield up he now keeps it raised for about 2.5 seconds (SWEATY_GUARD_HOLD_TICKS) before lowering it to swing, then raises it again, until an axe disables it.

## Batch 20b2: stun warning sound, Breach III
- The repeating pling is replaced by your flash sound (stun_warning), played once as he winds up a stun slam.
- His mace is now Breach III (the drop too). His pseudo-breach (armor reduction on his slam) went from 30% to 38%, between II and III. Tuning: SWEATY_BREACH.

## Batch 20c: Elijah's melee
- Three-swing combo: every third sword swing is a heavy one. He stops, his outline flashes red, sparks fly off his sword and a heavy clang plays (14 ticks), then the swing lands for 1.4x damage and a big knockback. Back away or block it.
- Sword crits: now and then he hops toward you and swings on the way down for a crit (1.5x damage, crit sparks and sound).
- Strafing: with his shield up he circles you, switching direction every 1.5-3 seconds.
- Axe vs shields: if you keep your shield up for about 3 seconds close to him, he swaps to a diamond axe, claps your shield down (it goes on cooldown) and swaps back to the sword. 15 s cooldown before he does it again.
- Breaking your shield plays the shield break sound.
- The sword phase is longer (220 ticks, 110 in phase two) because the shield hold makes his swings slower.
- Tuning: SWEATY_COMBO_*, SWEATY_CRIT_*, SWEATY_STRAFE, SWEATY_AXE_*.

## Batch 20d: heavy swing tweaks
- Heavy third swing hits harder (1.4x to 1.9x damage, SWEATY_COMBO_DAMAGE) and uses the shield-disabled pose as its wind-up tell, with the red outline on from the start of the wind-up until the swing lands.
- Sounds swapped: the stun slam now plays your alert sound (stun_alert); the heavy swing plays the flash sound that the stun slam used before (combo_warning).

## Batch 21a: Rotting Heart, Eli's Sword, the summon
- Rotting Heart (item): burn it (drop it into fire, soul fire, a lit campfire or lava) and it bursts with a shrieker soundwave, your heart-destroyed sound and sculk particles. The fire goes out, the ground around splotches with sculk and Elijah climbs out of it with the "You called?" voiceline. Chat shows "xX_5L0TH_Xx joined the game" in yellow, then his "You called?".
- Where hearts come from: ancient city chests (12% per chest) and a 4% drop from any Stagnated other than Elijah killed by a player. Tuning: HEART_*.
- Eli's Sword (diamond sword stats, your texture, your description) drops when you defeat him through phase two. It makes whoever it hits bleed (Hemorrhage: 1 armor-ignoring damage per second for 6 seconds, not stacking) and cannot be enchanted. Sword blocking and the parry come later with your sfx.
- Elijah's final death plays your death sfx on top of his voiceline.
- New: ModItems.ROTTING_HEART/ELI_SWORD, ModEffects.HEMORRHAGE, EliSummon.java; textures item/rotting_heart.png, item/eli_sword.png, mob_effect/hemorrhage.png (my placeholder icon, replace it any time).

## Batch 21b: music, boss bar, sculk splotch fix, louder voicelines
- Fixed: the sculk splotch around the burned heart did not appear (the ground search stopped too early).
- Heart burst and shriek sounds a bit quieter; every Elijah voiceline file is about 9 dB louder (limiter on, so nothing clips).
- Phase one music (eli_phase_one) starts when he appears and repeats while the fight goes on. It stops dead when phase two begins.
- Phase two theme (Tenebre Rosso Sangue): starts at its 22 second mark, then repeats from the beginning. Stops when he dies or leaves, or when you walk about 80 blocks away.
- Boss bar: only in phase two, small (144x52), at the top centre, teal health fill in the rail. Constants RAIL_* in EliBoss.java if the fill is off.
- New: EliPayload, EliBoss (client), music ids eli_phase_one / eli_tenebre / eli_tenebre_start.

## Batch 21c: sword block and parry, boss bar look, no exposure from his axe
- Eli's Sword can block (hold use): it takes half the damage from the front. A block in its first 7 ticks is a parry: the hit is cancelled, the attacker is thrown back, a white flash plays with your parry sound and time freezes for half a second (everything on the server stops, like a hit stop). Tuning: PARRY_*.
- Boss bar: black fill and black outline (the white edge is gone), moved up.
- Fixed: Elijah's axe hit (the shield clap) gave you spore exposure. His axe hit is now like his sword: no exposure.
- Phase one now also pings the music/bar from the general tick, so it starts while he is still climbing out.

## Batch 21d: music timing
- Tenebre now starts the instant the totem pops (the phase change is announced immediately, not at the next update), and phase one music starts the moment he appears.
- When Elijah dies the music fades out over about a second (the fade also happens if you walk away or he leaves). The phase one to phase two switch is still an abrupt cut.

## Batch 21e: boss bar fade
- The boss bar fades in (about 0.4 s) when phase two starts and fades out (about 0.6 s) when Elijah dies, leaves or you walk away. Boss bar health no longer vanishes instantly.

## Batch 21f: new boss bar, voice range, sculk death burst
- Boss bar uses your new transparent picture (scaled to 144x48, black, no edge). The fill rail is set in EliBoss.java (RAIL_*) if you want to nudge it.
- All of Elijah's voicelines can be heard from 64 blocks (was 16).
- When Elijah finally dies he bursts into sculk soul and sculk charge particles, with a sculk break sound.

## Batch 21g: boss bar meter contrast
- The empty part of the meter is now a lighter gray so the black health fill reads clearly against it.

## Batch 21h: phase two screen effects, sharper bar, shift-only descriptions
- When he pops his totem the screen flashes dark cyan (quick: up in 3 ticks, gone in about 0.8 s).
- In phase two the screen edges pulse dark cyan with veins creeping in from the borders, fading out with the boss bar when the fight ends.
- Boss bar uses the full-resolution picture shown at half size (117x37), so it is sharp instead of blurry.
- The Rotting Heart and Eli's Sword descriptions are now the same dark cyan, and they only show while holding Shift ("Hold [Shift] for description" otherwise).

## Batch 21i: veins, vignette, vanilla music
- Edge veins are now short and thick (they were long and thin); the edge glow is much stronger (14 bands, pulsing).
- Vanilla background music is stopped while the boss theme plays and for 5 seconds after, so the two never overlap.

## Batch 22a: golden apples cleanse gradually, healing without a mask above Y=0
- Golden apple: cleanses 65% of the meter over 14 seconds. Enchanted golden apple: the whole meter over 20 seconds. The cleansing only runs while you are safe (a working mask, or above Y=0); otherwise it waits (for up to 30 more seconds). Tuning: GAP_*, EGAP_*.
- Tonics, Instant Health and the apples now work without a mask when you are above Y=0 (below it, a working mask is still needed).

## Batch 22b: every mob breathes the spores, sculk deaths, natural blocks only
- Below Y=0 every mob without a working mask is hurt by the spores once a second, like zombies. Damage scales with max health (max health / 20, at least 1) so big mobs die about as fast. Pets, villagers and golems included.
- Immune: all skeleton kinds (skeleton, stray, wither skeleton, bogged, skeleton horse), the Stagnated and Elijah, the Warden, the Creaking, the Ender Dragon and the Wither.
- A mob that dies of the spores leaves a sculk splotch with as many blocks as its XP (at least 1, at most 40) and bursts into particles of the sculk block itself.
- Both this splotch and the Rotting Heart splotch now only replace natural ground (stone types, deepslate, tuff, dirt, grass, mud, sand, gravel, clay), never placed blocks.

## Batch 22c: phase two edges
- The cyan veins are replaced by short black tendrils that taper in from the screen edges, always in the same place, plus a black shadow along the edges. Only their strength pulses (slowly); nothing moves.

## Batch 23a: phase two voicelines and on-screen captions
- 17 phase two lines (sloth_*.ogg, loudness-matched). Mace slam: JUST DIE / STAY STILL. Axe clap on your shield: FACE ME / STOP HIDING / Clever. / You think you're safe? / CHEAP TRICKS. Stun slam: I WANT YOU DEAD / CATCH THIS / KEEP RUNNING / YOU'RE A JOKE / (laugh, no caption). Taunts (every 12-24 seconds while fighting nearby): RUSTY, KEEP SWINGING, OUTCLASSED, IS THIS WHAT HE LOST TO?, IS THIS ALL YOU'VE GOT? (the last one was in your files but not your list, I put it with the taunts).
- Lines never overlap (1.5 s gap) and never repeat twice in a row. Heard from 64 blocks.
- Missing: I'LL MAKE THIS HURT was in your list but I got no file for it.
- On-screen captions for these lines: dark cyan, each letter shaking, a quick glitch flicker at the start, fading out. As his phase two health runs down (50% to 0) the text drains to white with the black outline, so black and white at the end. No caption for the laugh.
- He no longer says "gg ez" when he wins in phase two (phase one still does).
- No line at his true death.

## Batch 23b: caption colour change
- The caption colour now changes when phase two begins: dark cyan at the totem pop, draining to black and white over the following 10 seconds (it used to follow his health).

## Batch 24a: parry rage, more HP
- HP: Elijah has 100 max health (was 80, existing ones keep their old value) and comes back from the totem pop at 70% (was 50%).
- Parry rage: parrying him enrages him for 15 seconds. 4 parries of his sword hits, or 2 of his mace slam, within a 10 second window (the count resets after 10 quiet seconds). A parry is a sword parry with Eli's Sword, or a shield block timed in the first moments of the raise (ticks 5-10). Neither counts against the stun slam, which can never be parried.
- In the rage: he drops his shield, takes the diamond axe and Speed II, and every swing is the heavy wind-up strike (the red outline and the disabled pose tell it) for 2.6x damage that also breaks your shield if it is up. Back away to dodge it. A ring of thorns (your picture, always facing you) circles him while it lasts. In phase one he plays frustrated.mp3; in phase two it is CHEAP TRICKS with its caption.
- Eli's Sword drops with an enchantment glint. (A red glint is not possible per item: the game has one glint colour for all items.)

## Batch 24b: ring fix, music fix, shield rule
- The thorn ring now follows him (it is told whose it is, so it stops trailing copies behind him), is bigger so it circles his body, and is red.
- The frustrated voiceline is now played at his position with the world sound call (it was not heard); report if it is still silent.
- Phase one music is now The Cyber Grind (meganeko), replacing the old one.
- Fixed the phase one song fading in, out and in again when he emerges: the "fight is on" updates stopped while he climbed out, so the client faded the song out and back in. They now go out the whole time.
- Only a parry with Eli's Sword counts toward the parry rage now. Blocking with a shield never does.

## Batch 24c: your thorn ring picture
- The ring particle uses your own transparent picture (black, not recoloured).

## Batch 24d
- Boss bar meter is now a white fill (dark backing), slot slightly taller so it sits under the black lines. Only EliBoss.java.

## Batch 25a
- 17 new hidden advancements: an Eli branch (Here Lies/Burns The Wicked, Organic Encounter, Rematch, Stop Hitting Yourself., +PARRY, U Mad Bro?, The Imperfect Vessel., Anachronism., Bled Dry, Skill Issue., Unstoppable Object Vs Immovable Object.), Classified./Archivist., and Wrong Air Wrong Time / Cooling Down / No Flesh, No Lungs.
- Eli name tag is xX_ELIJ4H_Xx, becomes xX_5L0TH_Xx at phase two. He fights back against a Warden that hits him. Tracks whether he was summoned (Organic Encounter = natural spawn).
- Eli's Sword tooltip: extra line about enchanting.

## Batch 25b
- Rage (parry rage only): he raises a guard window now and then; a hit into it bounces off (parry sound, sparks, attacker thrown back) and leaves him open ~0.9s; an axe still breaks it. Speed II -> Speed I. Every axe swing is a crit (RAGE_DAMAGE 2.6 -> 2.0 x1.5 crit).
- Left eye turns red (elijah_rage.png) with red particles during rage only.

## Batch 25c
- The exposure gauge and the filter meters now fade in and out (0.7 s) when you cross Y=0 or put the mask on/off, instead of popping. SporeHud.java only.

## Batch 25d
- Stun slam: your target picture (stun_mark.png) lies flat on the ground where he will land, turning slowly, with red dust drifting up from it. The dust ring stays.
- Eli's Sword can no longer be enchanted at the anvil (new AnvilMixin: no result with an enchanted book/item on the right). Any enchant that already got on is stripped from inventories every second.

## Batch 25e (combined 25a-25e)
- Rage parry now uses his sword: the axe is swapped for his sword and he holds it up in a mirrored blocking pose (no shield). New clip SWORD_BLOCK, synced PARRYING flag.

## Batch 26a (firework crossbow)
- Eli fires firework rockets with a crossbow: ground (backs off if you are close, loads with a red outline + warning sound, tracks you, fires; 1/3 of the time against a raised shield he holds the aim a beat first), and aerial (rises ~13 blocks, loads, fires a fixed-damage rocket, dives into a guaranteed water clutch). Multishot at 3+ foes. Phase/low-HP ramp: cooldown 160/100/70 ticks, fast reload in phase two and under 35
## Batch 27 (untested): Elijah crossbow voicelines, balance, shield stall
- 21 new crossbow voicelines (mono OGG, matched to the loudness of the existing sloth lines). Groups live in SlothLines: P1_SHOT, P1_FAST, MULTI (several foes), P2_SHOT, RELOAD (phase two fast reloads), AIR (phase two aerial snipe; phase one aerial snipes use P1_FAST). Phase one lines have no caption.
- Crossbow voicelines are said the moment he fires (after the shield stall), cutting in over the 60-tick voice cooldown.
- His sword, mace and slams now ignore Protection enchantments (bypasses_enchantments tag), like the rocket. Armor still counts.
- Rockets about 1.4x to 1.5x stronger (24 / 19 / 14 / 9 before armor at 2.5 / 4.5 / 8 / 12 blocks). Rage axe multiplier 2.0 to 1.6.
- Crossbow cooldowns 160/100/70 to 130/90/90 (low HP no longer shoots more often, since that made him run off and drag the fight out). Load 21 to 14 ticks, fast load 13 to 9. Aerial snipe chance 15%/35% to 30%/50%, and his first shot of a fight is a snipe when there is room.
- New: he shoots when you stay 9+ blocks away for 1.25 s (running off, getting further).
- Glint 0.5 block further forward (`XB_GLINT_FORWARD` 1.9, past the rocket) and much bigger (0.2 to 0.55).
- Shield stall: when your shield is up as he finishes aiming he now always holds the aim for 1.2 to 2 seconds (was 0.3 to 1 s, and only 1 time in 3), then fires anyway. Lower the shield and he fires at once. `XB_STALL_*` in Tuning.
- Aerial snipe damage 8 to 16 before armor (about 2 hearts through full diamond or netherite, up from 1).
- Slam one-shot fix: with the Protection bypass the Breach boost (made for a Protection IV player) stacked on top of Hard difficulty, so a max stun slam did about 26 damage to full netherite. Stun slam max 16 to 9 and smash max 12 to 7 (about 6 and 4.4 hearts on Hard through full netherite).

## Batch 28 (untested): stall variety, more aerial shots, rarer spawn
- Shield stall now happens about half the time when your shield is up (`XB_STALL_CHANCE` 1.0 to 0.5); otherwise he fires straight away. Stall length unchanged (1.2 to 2 s).
- Aerial snipe chance 30%/50% to 35%/55% (phase one/phase two). His first shot of a fight is still a snipe when there is room.
- Natural surface spawn rarer: `SWEATY_SURFACE_ODDS` 1800 to 2700 (average surface time before he turns up goes from 30 to 45 minutes; the 10 minute cooldown is unchanged).
