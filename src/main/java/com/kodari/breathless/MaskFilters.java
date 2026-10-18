package com.kodari.breathless;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * What is inside a gas mask: two filter slots. Each number is that filter's remaining life
 * (0 = empty slot, Tuning.FILTER_MAX = brand new). The two booleans say whether that filter is a reinforced one.
 * Stored on the mask as a data component.
 */
public record MaskFilters(int first, int second, boolean firstReinforced, boolean secondReinforced) {
   public static final MaskFilters EMPTY = new MaskFilters(0, 0, false, false);

   /** An empty slot can never be "reinforced". */
   public MaskFilters {
      firstReinforced = firstReinforced && first > 0;
      secondReinforced = secondReinforced && second > 0;
   }

   /** Two ordinary filters. */
   public MaskFilters(int first, int second) {
      this(first, second, false, false);
   }

   public static final Codec<MaskFilters> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("first").forGetter(MaskFilters::first),
      Codec.INT.fieldOf("second").forGetter(MaskFilters::second),
      Codec.BOOL.optionalFieldOf("first_reinforced", false).forGetter(MaskFilters::firstReinforced),
      Codec.BOOL.optionalFieldOf("second_reinforced", false).forGetter(MaskFilters::secondReinforced)
   ).apply(instance, MaskFilters::new));

   public static final StreamCodec<ByteBuf, MaskFilters> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, MaskFilters::first,
      ByteBufCodecs.VAR_INT, MaskFilters::second,
      ByteBufCodecs.BOOL, MaskFilters::firstReinforced,
      ByteBufCodecs.BOOL, MaskFilters::secondReinforced,
      MaskFilters::new
   );

   /** How many slots hold a filter. */
   public int count() {
      return (first > 0 ? 1 : 0) + (second > 0 ? 1 : 0);
   }

   public boolean isFull() {
      return count() >= 2;
   }

   /** True when there are enough filters for the mask to protect. */
   public boolean working() {
      return count() >= Math.min(2, Tuning.FILTERS_REQUIRED);
   }

   /** True if the given slot (0 or 1) holds a reinforced filter. */
   public boolean reinforced(int slot) {
      return slot == 0 ? firstReinforced : secondReinforced;
   }

   /** True when there is at least one filter and every loaded filter is reinforced (this picks the purple mask). */
   public boolean allReinforced() {
      return count() > 0 && (first <= 0 || firstReinforced) && (second <= 0 || secondReinforced);
   }

   /** Life of the given slot (0 or 1) as 0..1. */
   public float fraction(int slot) {
      int value = slot == 0 ? first : second;
      return Math.max(0.0F, Math.min(1.0F, value / (float)Tuning.FILTER_MAX));
   }

   /** The weakest loaded filter, 0..1 (1 if none, so nothing looks cracked). */
   public float weakest() {
      float result = 1.0F;
      if (first > 0) {
         result = Math.min(result, fraction(0));
      }
      if (second > 0) {
         result = Math.min(result, fraction(1));
      }
      return result;
   }

   /** Put a filter with this much life into the first empty slot. */
   public MaskFilters with(int life, boolean reinforced) {
      int clamped = Math.max(1, Math.min(Tuning.FILTER_MAX, life));
      return first <= 0
         ? new MaskFilters(clamped, second, reinforced, secondReinforced)
         : new MaskFilters(first, clamped, firstReinforced, reinforced);
   }

   /** The life of the filter that would come out next (the last one in), or 0 if none. */
   public int lastLife() {
      return second > 0 ? second : first;
   }

   /** Whether the filter that would come out next is reinforced. */
   public boolean lastReinforced() {
      return second > 0 ? secondReinforced : firstReinforced;
   }

   /** Remove the last filter that went in. */
   public MaskFilters withoutLast() {
      return second > 0 ? new MaskFilters(first, 0, firstReinforced, false) : EMPTY;
   }

   /** Same filters with new life values (keeps the reinforced flags). */
   public MaskFilters withLife(int newFirst, int newSecond) {
      return new MaskFilters(newFirst, newSecond, firstReinforced, secondReinforced);
   }
}
