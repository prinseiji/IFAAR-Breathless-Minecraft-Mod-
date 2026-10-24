package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client: a parry just happened. White flash. */
public record ParryPayload() implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<ParryPayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("parry"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ParryPayload> CODEC = StreamCodec.unit(new ParryPayload());

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
