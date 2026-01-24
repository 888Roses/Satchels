package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;

import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void invoke(CallbackInfo callbackInfo) {
        if (!SatchelItem.isUseInventoryOpen) {
            return;
        }

        final var client = (MinecraftClient) (Object) this;
        final var player = client.player;
        if (player == null || player.isSpectator() || SatchelItem.useInventoryItemStack == null || SatchelItem.useInventoryItemStack.isEmpty()) {
            return;
        }

        final var hotbarKeys = client.options.hotbarKeys;
        var hasChanged = false;
        for (int i = 0; i < hotbarKeys.length; ++i) {
            if (hotbarKeys[i].wasPressed()) {
                hasChanged = true;

                if (i >= SatchelItem.getStoredItemStackCount(SatchelItem.useInventoryItemStack)) {
                    break;
                }

                SatchelContentsDataComponent.selectedSlotIndex = i;
                SatchelItem.playScrollSound();
            }
        }

        if (hasChanged) {
            callbackInfo.cancel();
        }
    }
}
