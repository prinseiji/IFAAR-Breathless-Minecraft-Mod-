package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client, a few times a second while Elijah is fighting nearby: which phase (1 or 2, 0 = over) and his health (0..1). Drives the music and the boss bar. */
public record EliPayload(int phase, float health) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<EliPayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("eli"));
   public static final StreamCodec<RegistryFriendlyByteBuf, EliPayload> CODEC =
      StreamCodec.composite(ByteBufCodecs.VAR_INT, EliPayload::phase, ByteBufCodecs.FLOAT, EliPayload::health, EliPayload::new);

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
