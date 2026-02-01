package net.rose.satchels.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Scroller;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    /// Handles scrolling through satchel slots.
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void satchels$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        if (window != client.getWindow().getHandle()) return;

        ClientPlayerEntity player = client.player;
        if (player == null || player.isSpectator()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack selectedStack = inventory.getSelectedStack();

        if (selectedStack == null || selectedStack.isEmpty() || !selectedStack.isIn(ModItemTags.SATCHELS)) {
            return;
        }

        SatchelContentsDataComponent component = selectedStack.get(ModDataComponents.SATCHEL_CONTENTS);
        if (component == null) return;

        if (component.stacks().isEmpty() || !component.isOpen()) {
            return;
        }

        client.getInactivityFpsLimiter().onInput();

        Boolean isDiscrete = client.options.getDiscreteMouseScroll().getValue();
        Double mouseScrollAmount = client.options.getMouseWheelSensitivity().getValue();
        double horizontalAmount = (isDiscrete ? Math.signum(horizontal) : horizontal) * mouseScrollAmount;
        double verticalAmount = (isDiscrete ? Math.signum(vertical) : vertical) * mouseScrollAmount;
        double speed = verticalAmount == 0 ? -horizontalAmount : verticalAmount;

        if (speed == 0) {
            return;
        }

        int satchelSlotIndex = Scroller.scrollCycling(speed, component.selectedSlotIndex(), component.stacks().size());

        SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
        builder.setSelectedSlotIndex(satchelSlotIndex);
        selectedStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

        // inventory.setStack(inventory.getSelectedSlot(), selectedStack);

        ClientPlayNetworking.send(new SetSatchelSlotIndexC2S(inventory.getSelectedSlot(), satchelSlotIndex));
        SatchelsClient.playScrollSound();

        callbackInfo.cancel();
    }
}