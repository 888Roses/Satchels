package net.rose.satchels.common.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;

public record SetSatchelSlotIndexC2S(int slotId, int selectedSlotIndex) implements CustomPayload {
    public static final CustomPayload.Id<SetSatchelSlotIndexC2S> ID = new Id<>(Satchels.id("set_satchel_slot_index"));

    public static final PacketCodec<RegistryByteBuf, SetSatchelSlotIndexC2S> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SetSatchelSlotIndexC2S::slotId,
            PacketCodecs.INTEGER, SetSatchelSlotIndexC2S::selectedSlotIndex,
            SetSatchelSlotIndexC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        ScreenHandler handler = context.player().currentScreenHandler;
        // context.player().sendMessage(Text.literal("SERVER slot: " + slotId + " selected: " + selectedSlotIndex));

        if (slotId() >= 0 && slotId() < handler.slots.size()) {
            ItemStack itemStack = handler.slots.get(slotId()).getStack();
            SatchelContentsDataComponent component = itemStack.get(ModDataComponents.SATCHEL_CONTENTS);

            if (component != null) {
                SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
                builder.setSelectedSlotIndex(selectedSlotIndex());
                itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
            }
        }
    }
}
