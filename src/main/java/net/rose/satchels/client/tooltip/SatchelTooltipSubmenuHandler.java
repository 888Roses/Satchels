package net.rose.satchels.client.tooltip;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class SatchelTooltipSubmenuHandler implements TooltipSubmenuHandler {
    private final Scroller scroller;

    public SatchelTooltipSubmenuHandler(MinecraftClient ignored) {
        this.scroller = new Scroller();
    }

    @Override
    public boolean isApplicableTo(Slot slot) {
        return slot.getStack().isIn(ModItemTags.SATCHELS);
    }

    public boolean onScroll(double horizontal, double vertical, int inventorySlotId, ItemStack itemStack) {
        SatchelContentsDataComponent component = itemStack.get(ModDataComponents.SATCHEL_CONTENTS);

        if (component == null || component.stacks().isEmpty()) {
            return false;
        }

        Vector2i scrollDelta = this.scroller.update(horizontal, vertical);
        int speed = scrollDelta.y == 0
                ? -scrollDelta.x
                : scrollDelta.y;

        if (speed != 0) {
            int currentSatchelSlotIndex = component.selectedSlotIndex();
            int satchelFillAmount = component.stacks().size();
            int satchelSlotIndex = Scroller.scrollCycling(speed, currentSatchelSlotIndex, satchelFillAmount);

            MinecraftClient client = MinecraftClient.getInstance();

            Screen screen = client.currentScreen;

            if (!(screen instanceof HandledScreen<?> handledScreen)) {
                client.player.sendMessage(Text.literal("Screen is not a handled screen! Please report this exact message in the discord! " + screen).formatted(Formatting.RED), false);
                return false;
            }

            Slot focusedSlot = handledScreen.focusedSlot;
            if (focusedSlot == null) {
                return false;
            }

            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            builder.setSelectedSlotIndex(satchelSlotIndex);
            itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

            ClientPlayNetworking.send(new SetSatchelSlotIndexC2S(focusedSlot.getIndex(), satchelSlotIndex));
        }

        return true;
    }

    @Override
    public void reset(Slot slot) {
        setSlot(slot);
    }

    private void setSlot(Slot slot) {
        ItemStack itemStack = slot.getStack();
        slot.setStack(setSlot(itemStack, itemStack.get(ModDataComponents.SATCHEL_CONTENTS), -1));
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

    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            setSlot(slot);
        }
    }
}
