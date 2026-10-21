package com.kodari.breathless;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client: open the lore folder for document number {@code doc} (see Lore.DOCS). */
public record OpenLorePayload(int doc) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<OpenLorePayload> TYPE = new CustomPacketPayload.Type<>(Breathless.id("open_lore"));
   public static final StreamCodec<RegistryFriendlyByteBuf, OpenLorePayload> CODEC =
      StreamCodec.composite(ByteBufCodecs.VAR_INT, OpenLorePayload::doc, OpenLorePayload::new);

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
