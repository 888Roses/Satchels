package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;

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

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll$satchels(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        if (window != this.client.getWindow().getHandle()) {
            return;
        }

        this.client.getInactivityFpsLimiter().onInput();
        final var isDiscrete = this.client.options.getDiscreteMouseScroll().getValue();
        final var mouseScrollAmount = this.client.options.getMouseWheelSensitivity().getValue();
        final var horizontalAmount = (isDiscrete ? Math.signum(horizontal) : horizontal) * mouseScrollAmount;
        final var verticalAmount = (isDiscrete ? Math.signum(vertical) : vertical) * mouseScrollAmount;

        final var itemStack = SatchelItem.useInventoryItemStack;
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        final var storedItemStackCount = SatchelItem.getStoredItemStackCount(itemStack);
        if (storedItemStackCount == 0) {
            return;
        }

        final var scrollAmount = verticalAmount == 0 ? -horizontalAmount : verticalAmount;

        if (scrollAmount != 0) {
            selectedSlotIndex = Scroller.scrollCycling(scrollAmount, selectedSlotIndex, storedItemStackCount);
            SatchelItem.playScrollSound();

            callbackInfo.cancel();
        }
    }
}