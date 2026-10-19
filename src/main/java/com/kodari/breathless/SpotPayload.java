package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client: a Stagnated just spotted you, so SLOTH speaks ("I SEE YOU"). The float is unused. */
public record SpotPayload(float unused) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<SpotPayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("spot"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SpotPayload> CODEC =
      StreamCodec.composite(ByteBufCodecs.FLOAT, SpotPayload::unused, SpotPayload::new);

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
