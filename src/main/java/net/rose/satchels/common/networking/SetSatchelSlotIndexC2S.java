package net.rose.satchels.common.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;

public record SetSatchelSlotIndexC2S(int inventorySlotId, int satchelSlotIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetSatchelSlotIndexC2S> ID = new Type<>(Satchels.id("set_satchel_slot_index"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSatchelSlotIndexC2S> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetSatchelSlotIndexC2S::inventorySlotId,
            ByteBufCodecs.INT, SetSatchelSlotIndexC2S::satchelSlotIndex,
            SetSatchelSlotIndexC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        // Cannot select satchel slot for an ItemStack outside the inventory slot array.
        if (inventorySlotId < 0) {
            return;
        }

        ServerPlayer serverPlayer = context.player();
        AbstractContainerMenu handler = serverPlayer.containerMenu;

        // Handler has to exist. Cannot select satchel slot for an ItemStack outside the inventory slot array.
        if (handler == null || inventorySlotId >= handler.slots.size()) {
            return;
        }

        ItemStack itemStack = serverPlayer.getInventory().getItem(inventorySlotId);

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
