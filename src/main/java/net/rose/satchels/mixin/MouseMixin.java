package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;

import net.minecraft.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.rose.satchels.common.item.SatchelItem;

import static net.rose.satchels.common.data_component.SatchelContentsDataComponent.selectedSlotIndex;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    /// Handles scrolling through satchel slots.
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void satchels$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        if (window != client.getWindow().getHandle()) {
            return;
        }

        client.getInactivityFpsLimiter().onInput();
        Boolean isDiscrete = client.options.getDiscreteMouseScroll().getValue();
        Double mouseScrollAmount = client.options.getMouseWheelSensitivity().getValue();
        double horizontalAmount = (isDiscrete ? Math.signum(horizontal) : horizontal) * mouseScrollAmount;
        double verticalAmount = (isDiscrete ? Math.signum(vertical) : vertical) * mouseScrollAmount;

        ItemStack itemStack = SatchelItem.useInventoryItemStack;
        if (itemStack == null || itemStack.isEmpty()) return;

        int storedItemStackCount = SatchelItem.getStoredItemStackCount(itemStack);
        if (storedItemStackCount == 0) return;

        double scrollAmount = verticalAmount == 0 ? -horizontalAmount : verticalAmount;

        if (scrollAmount != 0) {
            selectedSlotIndex = Scroller.scrollCycling(scrollAmount, selectedSlotIndex, storedItemStackCount);
            SatchelsClient.playScrollSound();
            callbackInfo.cancel();
        }
    }
}