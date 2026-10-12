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
