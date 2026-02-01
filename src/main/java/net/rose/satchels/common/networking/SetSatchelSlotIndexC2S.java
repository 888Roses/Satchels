package net.rose.satchels.common.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;

public record SetSatchelSlotIndexC2S(int inventorySlotId, int satchelSlotIndex) implements CustomPayload {
    public static final CustomPayload.Id<SetSatchelSlotIndexC2S> ID = new Id<>(Satchels.id("set_satchel_slot_index"));

    public static final PacketCodec<RegistryByteBuf, SetSatchelSlotIndexC2S> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SetSatchelSlotIndexC2S::inventorySlotId,
            PacketCodecs.INTEGER, SetSatchelSlotIndexC2S::satchelSlotIndex,
            SetSatchelSlotIndexC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        // Cannot select satchel slot for an ItemStack outside the inventory slot array.
        if (inventorySlotId < 0) {
            return;
        }

        ServerPlayerEntity serverPlayer = context.player();
        ScreenHandler handler = serverPlayer.currentScreenHandler;

        // Handler has to exist. Cannot select satchel slot for an ItemStack outside the inventory slot array.
        if (handler == null || inventorySlotId >= handler.slots.size()) {
            return;
        }

        ItemStack itemStack = serverPlayer.getInventory().getStack(inventorySlotId);

        if (itemStack.isEmpty()) {
            return;
        }

        SatchelContentsDataComponent component = itemStack.get(ModDataComponents.SATCHEL_CONTENTS);

        if (component != null) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            builder.setSelectedSlotIndex(satchelSlotIndex());
            itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
        }
    }
}
