# Batch 76: struggle fixes

- **Could not break free:** a tap that filled the bar was applied between ticks, and the next tick drained it a little before checking for full, so it never counted. It now frees you the moment the bar is full, and the check comes before the drain.
- The bar fill is darker (0xFF1B6F94) on a darker track.
- The spacebar icon is 1.5 times bigger (54 px).
- While held, a slow pulsing dark vignette (about one pulse every 2 s, only darkens, eases off as the bar fills).
- Replaces `Odium.java` and `SporeHud.java` from Batches 74 and 75.
