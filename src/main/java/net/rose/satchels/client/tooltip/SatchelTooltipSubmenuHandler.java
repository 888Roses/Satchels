package net.rose.satchels.client.tooltip;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class SatchelTooltipSubmenuHandler implements ItemSlotMouseAction {
    private final ScrollWheelHandler scroller;

    public SatchelTooltipSubmenuHandler(Minecraft ignored) {
        this.scroller = new ScrollWheelHandler();
    }

    @Override
    public boolean matches(Slot slot) {
        return slot.getItem().is(ModItemTags.SATCHELS);
    }

    public boolean onMouseScrolled(double horizontal, double vertical, int inventorySlotId, ItemStack itemStack) {
        SatchelContentsDataComponent component = itemStack.get(ModDataComponents.SATCHEL_CONTENTS);

        if (component == null || component.stacks().isEmpty()) {
            return false;
        }

        Vector2i scrollDelta = this.scroller.onMouseScroll(horizontal, vertical);
        int speed = scrollDelta.y == 0
                ? -scrollDelta.x
                : scrollDelta.y;

        if (speed != 0) {
            int currentSatchelSlotIndex = component.selectedSlotIndex();
            int satchelFillAmount = component.stacks().size();
            int satchelSlotIndex = ScrollWheelHandler.getNextScrollWheelSelection(speed, currentSatchelSlotIndex, satchelFillAmount);

            Minecraft client = Minecraft.getInstance();

            Screen screen = client.screen;

            if (!(screen instanceof AbstractContainerScreen<?> handledScreen)) {
                client.player.displayClientMessage(Component.literal("Screen is not a handled screen! Please report this exact message in the discord! " + screen).withStyle(ChatFormatting.RED), false);
                return false;
            }

            Slot focusedSlot = handledScreen.hoveredSlot;
            if (focusedSlot == null) {
                return false;
            }

            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            builder.setSelectedSlotIndex(satchelSlotIndex);
            itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

            ClientPlayNetworking.send(new SetSatchelSlotIndexC2S(focusedSlot.getContainerSlot(), satchelSlotIndex));
        }

        return true;
    }

    @Override
    public void onStopHovering(Slot slot) {
        setSlot(slot);
    }

    private void setSlot(Slot slot) {
        ItemStack itemStack = slot.getItem();
        slot.setByPlayer(setSlot(itemStack, itemStack.get(ModDataComponents.SATCHEL_CONTENTS), -1));
    }

    private ItemStack setSlot(ItemStack itemStack, @Nullable SatchelContentsDataComponent component, int selectedItemIndex) {
        if (component == null) {
            return itemStack;
        }

        if (component.selectedSlotIndex() != selectedItemIndex) {
            // Satchels.LOGGER.info("UwU " + component.satchelSlotIndex() + " != " + selectedItemIndex);

            var builder = new SatchelContentsDataComponent.Builder(component);
            builder.setSelectedSlotIndex(selectedItemIndex);
            builder.setOpen(selectedItemIndex != -1 && builder.isOpen());

            itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

            SatchelsClient.playScrollSound();
        }

        return itemStack;
    }

    public void onSlotClicked(Slot slot, ClickType actionType) {
        if (actionType == ClickType.QUICK_MOVE || actionType == ClickType.SWAP) {
            setSlot(slot);
        }
    }
}
