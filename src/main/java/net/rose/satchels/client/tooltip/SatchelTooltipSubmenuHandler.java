package net.rose.satchels.client.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import org.joml.Vector2i;

import static net.rose.satchels.common.data_component.SatchelContentsDataComponent.selectedSlotIndex;

public class SatchelTooltipSubmenuHandler implements TooltipSubmenuHandler {
    private final Scroller scroller;

    public SatchelTooltipSubmenuHandler(MinecraftClient ignored) {
        this.scroller = new Scroller();
    }

    @Override
    public boolean isApplicableTo(Slot slot) {
        return slot.getStack().isIn(ModItemTags.SATCHELS);
    }

    public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack itemStack) {
        int storedItemStackCount = SatchelItem.getStoredItemStackCount(itemStack);
        if (storedItemStackCount == 0) return false;

        Vector2i scrollDelta = this.scroller.update(horizontal, vertical);
        int scrollAmount = scrollDelta.y == 0 ? -scrollDelta.x : scrollDelta.y;

        if (scrollAmount != 0) {
            int cycledSlotIndex = Scroller.scrollCycling(scrollAmount, selectedSlotIndex, storedItemStackCount);
            this.setSlot(cycledSlotIndex);
        }

        return true;
    }

    @Override
    public void reset(Slot slot) {
        this.setSlot(-1);
    }

    private void setSlot(int selectedItemIndex) {
        if (selectedSlotIndex != selectedItemIndex) {
            selectedSlotIndex = selectedItemIndex;
            SatchelsClient.playScrollSound();
        }
    }

    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            this.setSlot(-1);
        }
    }
}
