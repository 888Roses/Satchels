package net.rose.satchels.client.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
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

    public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack itemStack) {
        SatchelContentsDataComponent component = itemStack.get(ModDataComponents.SATCHEL_CONTENTS);
        if (component == null || component.stacks().isEmpty()) {
            return false;
        }

        Vector2i scrollDelta = this.scroller.update(horizontal, vertical);
        int scrollAmount = scrollDelta.y == 0 ? -scrollDelta.x : scrollDelta.y;

        if (scrollAmount != 0) {
            int cycledSlotIndex = Scroller.scrollCycling(scrollAmount, component.selectedSlotIndex(), component.stacks().size());
            setSlot(itemStack, component, cycledSlotIndex);
        }

        return true;
    }

    @Override
    public void reset(Slot slot) {
        //setSlot(slot);
    }

    private void setSlot(Slot slot) {
        ItemStack itemStack = slot.getStack();

        setSlot(itemStack, itemStack.get(ModDataComponents.SATCHEL_CONTENTS), -1);
    }

    private void setSlot(ItemStack itemStack, @Nullable SatchelContentsDataComponent component, int selectedItemIndex) {
        if (component == null) {
            return;
        }

        if (component.selectedSlotIndex() != selectedItemIndex) {
            itemStack.set(
                    ModDataComponents.SATCHEL_CONTENTS,
                    new SatchelContentsDataComponent.Builder(component).setSelectedSlotIndex(selectedItemIndex).build()
            );

            SatchelsClient.playScrollSound();
        }
    }

    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            setSlot(slot);
        }
    }
}
