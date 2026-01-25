package net.rose.satchels.mixin;

import net.minecraft.client.MinecraftClient;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /// Handles pressing the hotbar inputs to swap between different satchel slots.
    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void satchels$handleInputEvents(CallbackInfo callbackInfo) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        ClientPlayerEntity player = client.player;

        if (player == null || player.isSpectator()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack selectedStack = inventory.getSelectedStack();
        if (selectedStack == null || selectedStack.isEmpty() || !selectedStack.isIn(ModItemTags.SATCHELS)) {
            return;
        }

        if (!selectedStack.isEmpty() && selectedStack.isIn(ModItemTags.SATCHELS)) {
            SatchelContentsDataComponent component = selectedStack.get(ModDataComponents.SATCHEL_CONTENTS);
            if (component != null && !component.stacks().isEmpty() && component.isOpen()) {
                KeyBinding[] hotbarKeys = client.options.hotbarKeys;
                boolean hotbarKeyWasPressed = false;

                for (int i = 0; i < hotbarKeys.length; ++i) {
                    if (hotbarKeys[i].wasPressed()) {
                        hotbarKeyWasPressed = true;

                        if (i >= component.stacks().size()) {
                            break;
                        }

                        component = new SatchelContentsDataComponent.Builder(component).setSelectedSlotIndex(i).build();
                        selectedStack.set(ModDataComponents.SATCHEL_CONTENTS, component);
                        inventory.setStack(inventory.getSelectedSlot(), selectedStack);

                        SatchelsClient.playScrollSound();
                    }
                }

                if (hotbarKeyWasPressed) {
                    callbackInfo.cancel();
                }
            }
        }
    }
}
