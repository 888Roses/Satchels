package net.rose.satchels.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
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

@Mixin(MouseHandler.class)
public abstract class MouseMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    /// Handles scrolling through satchel slots.
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void satchels$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        if (window != minecraft.getWindow().handle()) return;

        LocalPlayer player = minecraft.player;
        if (player == null || player.isSpectator()) {
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack selectedStack = inventory.getSelectedItem();

        if (selectedStack == null || selectedStack.isEmpty() || !selectedStack.is(ModItemTags.SATCHELS)) {
            return;
        }

        SatchelContentsDataComponent component = selectedStack.get(ModDataComponents.SATCHEL_CONTENTS);
        if (component == null) return;

        if (component.stacks().isEmpty() || !component.isOpen()) {
            return;
        }

        minecraft.getFramerateLimitTracker().onInputReceived();

        Boolean isDiscrete = minecraft.options.discreteMouseScroll().get();
        Double mouseScrollAmount = minecraft.options.mouseWheelSensitivity().get();
        double horizontalAmount = (isDiscrete ? Math.signum(horizontal) : horizontal) * mouseScrollAmount;
        double verticalAmount = (isDiscrete ? Math.signum(vertical) : vertical) * mouseScrollAmount;
        double speed = verticalAmount == 0 ? -horizontalAmount : verticalAmount;

        if (speed == 0) {
            return;
        }

        int satchelSlotIndex = ScrollWheelHandler.getNextScrollWheelSelection(speed, component.selectedSlotIndex(), component.stacks().size());

        SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
        builder.setSelectedSlotIndex(satchelSlotIndex);
        selectedStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

        // inventory.setStack(inventory.getSelectedSlot(), selectedStack);

        ClientPlayNetworking.send(new SetSatchelSlotIndexC2S(inventory.getSelectedSlot(), satchelSlotIndex));
        SatchelsClient.playScrollSound();

        callbackInfo.cancel();
    }
}