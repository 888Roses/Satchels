package net.rose.satchels.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.client.SatchelsClient;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    /// Handles pressing the hotbar inputs to swap between different satchel slots.
    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void satchels$handleInputEvents(CallbackInfo callbackInfo) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;

        if (player == null || player.isSpectator()) {
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack selectedStack = inventory.getSelectedItem();
        if (selectedStack == null || selectedStack.isEmpty() || !selectedStack.is(ModItemTags.SATCHELS)) {
            return;
        }

        if (!selectedStack.isEmpty() && selectedStack.is(ModItemTags.SATCHELS)) {
            SatchelContentsDataComponent component = selectedStack.get(ModDataComponents.SATCHEL_CONTENTS);
            if (component != null && !component.stacks().isEmpty() && component.isOpen()) {
                KeyMapping[] hotbarKeys = client.options.keyHotbarSlots;
                boolean hotbarKeyWasPressed = false;

                for (int i = 0; i < hotbarKeys.length; ++i) {
                    if (hotbarKeys[i].consumeClick()) {
                        hotbarKeyWasPressed = true;

                        if (i >= component.stacks().size()) {
                            break;
                        }

                        component = new SatchelContentsDataComponent.Builder(component).setSelectedSlotIndex(i).build();
                        selectedStack.set(ModDataComponents.SATCHEL_CONTENTS, component);
                        inventory.setItem(inventory.getSelectedSlot(), selectedStack);

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
