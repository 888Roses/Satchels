package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /// Handles pressing the hotbar inputs to swap between different satchel slots.
    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void satchels$handleInputEvents(CallbackInfo callbackInfo) {
        if (!SatchelItem.isUseInventoryOpen) {
            return;
        }

        MinecraftClient client = (MinecraftClient) (Object) this;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        if (player.isSpectator() || SatchelItem.useInventoryItemStack == null || SatchelItem.useInventoryItemStack.isEmpty()) {
            return;
        }

        KeyBinding[] hotbarKeys = client.options.hotbarKeys;
        boolean hotbarKeyWasPressed = false;

        for (int i = 0; i < hotbarKeys.length; ++i) {
            if (hotbarKeys[i].wasPressed()) {
                hotbarKeyWasPressed = true;

                if (i >= SatchelItem.getStoredItemStackCount(SatchelItem.useInventoryItemStack)) {
                    break;
                }

                SatchelContentsDataComponent.selectedSlotIndex = i;
                SatchelsClient.playScrollSound();
            }
        }

        if (hotbarKeyWasPressed) {
            callbackInfo.cancel();
        }
    }
}
