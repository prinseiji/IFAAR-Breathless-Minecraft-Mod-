package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client: show this line of Elijah's as an on-screen caption for this many ticks. */
public record CaptionPayload(String text, int ticks) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<CaptionPayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("caption"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CaptionPayload> CODEC =
      StreamCodec.composite(ByteBufCodecs.STRING_UTF8, CaptionPayload::text, ByteBufCodecs.VAR_INT, CaptionPayload::ticks, CaptionPayload::new);

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
