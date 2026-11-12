package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client -> server: the jump key was tapped while an Odium is holding this player. Each tap fills the struggle bar a little. */
public record StrugglePayload() implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<StrugglePayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("struggle"));
   public static final StreamCodec<RegistryFriendlyByteBuf, StrugglePayload> CODEC = StreamCodec.unit(new StrugglePayload());

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
