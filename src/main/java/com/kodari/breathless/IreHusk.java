package com.kodari.breathless;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** A fake Ire: lies half buried in the Ire's idle pose, never moves, and bursts into a small gas puff when hit (everything else is OdiumHusk). */
public class IreHusk extends OdiumHusk {
   public IreHusk(EntityType<? extends IreHusk> type, Level level) {
      super(type, level);
   }
}
