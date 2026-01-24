package net.rose.satchels.common.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.rose.satchels.common.Satchels;

public record SatchelSelectedSlotS2CPayload(int selectedItemSlot) implements CustomPayload {
    public static final Identifier SELECTED_SLOT_PAYLOAD_ID = Satchels.id("satchel_selected_slot_s2c");
    public static final CustomPayload.Id<SatchelSelectedSlotS2CPayload> ID = new CustomPayload.Id<>(SELECTED_SLOT_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SatchelSelectedSlotS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SatchelSelectedSlotS2CPayload::selectedItemSlot, SatchelSelectedSlotS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
