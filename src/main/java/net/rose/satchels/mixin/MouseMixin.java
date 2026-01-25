package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.rose.satchels.common.item.SatchelItem;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    /// Handles scrolling through satchel slots.
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void satchels$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        ClientPlayerEntity player = client.player;

        if (window != client.getWindow().getHandle() || player == null || player.isSpectator()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack selectedStack = inventory.getSelectedStack();
        if (selectedStack == null || selectedStack.isEmpty() || !selectedStack.isIn(ModItemTags.SATCHELS)) {
            return;
        }

        SatchelContentsDataComponent component = selectedStack.get(ModDataComponents.SATCHEL_CONTENTS);
        if (component != null) {
            if (component.stacks().isEmpty() || !component.isOpen()) {
                return;
            }

            client.getInactivityFpsLimiter().onInput();

            Boolean isDiscrete = client.options.getDiscreteMouseScroll().getValue();
            Double mouseScrollAmount = client.options.getMouseWheelSensitivity().getValue();
            double horizontalAmount = (isDiscrete ? Math.signum(horizontal) : horizontal) * mouseScrollAmount;
            double verticalAmount = (isDiscrete ? Math.signum(vertical) : vertical) * mouseScrollAmount;
            double scrollAmount = verticalAmount == 0 ? -horizontalAmount : verticalAmount;

            if (scrollAmount != 0) {
                int selectedIndex = Scroller.scrollCycling(scrollAmount, component.selectedSlotIndex(), component.stacks().size());
                component = new SatchelContentsDataComponent.Builder(component).setSelectedSlotIndex(selectedIndex).build();
                selectedStack.set(ModDataComponents.SATCHEL_CONTENTS, component);
                inventory.setStack(inventory.getSelectedSlot(), selectedStack);

                SatchelsClient.playScrollSound();
                callbackInfo.cancel();
            }
        }
    }
}