package net.rose.satchels.common.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;

import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SatchelItem extends Item {
    public static final int MAX_ITEM_COUNT = 3;

    public SatchelItem(Properties settings) {
        super(settings);
    }

    // region Util

    /// Gets the [SatchelContentsDataComponent] attached to this [ItemStack].
    public static @Nullable SatchelContentsDataComponent getSatchelDataComponent(ItemStack itemStack) {
        return itemStack.has(ModDataComponents.SATCHEL_CONTENTS) ? itemStack.get(ModDataComponents.SATCHEL_CONTENTS) : null;
    }

    /// Gets and returns the amount of item stored in this satchel [ItemStack].
    public static int getStoredItemStackCount(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        return component == null ? 0 : component.stacks().size();
    }

    private static void refreshScreenHandler(Player user) {
        AbstractContainerMenu screenHandler = user.containerMenu;
        if (screenHandler != null) {
            screenHandler.slotsChanged(user.getInventory());
        }
    }

    // endregion

    // region Sounds

    public static void playInsertSound(Player user, boolean failed) {
        SoundEvent soundEvent = failed ? SoundEvents.BUNDLE_INSERT_FAIL : SoundEvents.HORSE_SADDLE.value();
        user.playSound(soundEvent, 0.5F, Mth.nextFloat(user.getRandom(), 0.98F, 1.02F));
    }

    public static void playRemoveSound(Player user) {
        float pitch = Mth.nextFloat(user.getRandom(), 1.15F, 1.25F);
        user.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 0.75F, pitch);
    }

    // endregion

    // region In Inventory Behavior

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickType, Player user) {
        // When a satchel item is clicked using another stack.

        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(itemStack);
        if (currentComponent == null) {
            return false;
        }

        ItemStack slotItemStack = slot.getItem();

        // Insert in satchel.
        if (clickType == ClickAction.PRIMARY) {
            if (slotItemStack.isEmpty()) {
                return false;
            }

            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            // The amount of items copied.
            int copiedStackSize = Math.min(SatchelContentsDataComponent.MAX_STACK_SIZE, slotItemStack.getCount());
            if (builder.add(slotItemStack.copyWithCount(copiedStackSize))) {
                slotItemStack.shrink(copiedStackSize);

                builder.setSelectedSlotIndex(-1);
                itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

                refreshScreenHandler(user);
                playInsertSound(user, false);

                return true;
            }

            playInsertSound(user, true);
            return false;
        }

        // Extract from satchel.
        if (slot.getItem().isEmpty()) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            Optional<ItemStack> removed = builder.removeCurrent();

            if (removed.isPresent()) {
                slot.setByPlayer(removed.get().copy());

                builder.setSelectedSlotIndex(-1);
                itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

                refreshScreenHandler(user);
                playRemoveSound(user);

                return true;
            }
        }

        return false;

    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack satchelItemStack, ItemStack otherStack, Slot slot, ClickAction clickType, Player user, SlotAccess cursorStackReference) {
        // When another stack is clicked using a satchel.

        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(satchelItemStack);
        if (currentComponent == null) {
            return false;
        }

        ItemStack itemStackInCursor = cursorStackReference.get();

        if (!itemStackInCursor.isEmpty()) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            // The amount of items copied.
            int copiedStackSize = Math.min(SatchelContentsDataComponent.MAX_STACK_SIZE, itemStackInCursor.getCount());
            if (builder.add(itemStackInCursor.copyWithCount(copiedStackSize))) {
                itemStackInCursor.shrink(copiedStackSize);

                builder.setSelectedSlotIndex(-1);
                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

                refreshScreenHandler(user);
                playInsertSound(user, false);

                return true;
            }

            playInsertSound(user, true);
            return true;
        }

        if (clickType == ClickAction.SECONDARY) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            // user.sendMessage(Text.literal("Selected Slot Index: " + builder.satchelSlotIndex()).formatted(user.getEntityWorld().isClient() ? Formatting.YELLOW : Formatting.AQUA), false);
            Optional<ItemStack> removed = builder.removeCurrent();
            if (removed.isPresent()) {
                cursorStackReference.set(removed.get().copy());

                builder.setSelectedSlotIndex(-1);
                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

                refreshScreenHandler(user);
                playRemoveSound(user);

                return true;
            }

            return false;
        }

        return false;
    }

    // endregion

    // region Tooltip

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.ofNullable(getSatchelDataComponent(itemStack));
    }

    // endregion

    // region Item Bar

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return getStoredItemStackCount(itemStack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        if (component == null) return 0;

        return Math.round(component.getOccupancy() * 13);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        return component == null ? 0x373737 : component.getOccupancy() < 1F ? 0x5555FF : 0xFF5555;
    }

    // endregion

    // region R-Click Inventory

    /// CLIENT ONLY!!
    // public static ItemStack inspectedItemStack;

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack satchelItemStack = user.getItemInHand(hand);
        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(satchelItemStack);

        if (currentComponent != null) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            builder.setOpen(!builder.isOpen());
            satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

            if (!builder.isOpen()) {
                Optional<ItemStack> removed = builder.removeCurrent();
                // user.sendMessage(Text.literal("Selected Slot Index: " + builder.satchelSlotIndex()).formatted(user.getEntityWorld().isClient() ? Formatting.YELLOW : Formatting.AQUA), false);
                if (removed.isPresent()) {
                    user.handleExtraItemsCreatedOnUse(removed.get().copy());

                    builder.setSelectedSlotIndex(-1);
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    user.setItemInHand(hand, satchelItemStack);

                    refreshScreenHandler(user);
                    user.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 0.75F, Mth.nextFloat(user.getRandom(), 1.15F, 1.25F));

                    return InteractionResult.SUCCESS;
                }

                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                return InteractionResult.FAIL;
            }

            // builder.setSelectedSlotIndex(0);
            satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
            user.setItemInHand(hand, satchelItemStack);

            if (currentComponent.stacks().isEmpty()) {
                return InteractionResult.FAIL;
            }

            user.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.9F, Mth.nextFloat(user.getRandom(), 0.98F, 1.02F));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(stack);

        if (currentComponent != null) {
            if (!currentComponent.isOpen()) {
                return;
            }

            boolean hasChanged = false;
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);

            if (builder.previousSelectedSlotIndex() != builder.selectedSlotIndex()) {
                builder.setPreviousSelectedSlotIndex(builder.selectedSlotIndex());
                hasChanged = true;
            }

            if (hasChanged) {
                currentComponent = builder.build();
                stack.set(ModDataComponents.SATCHEL_CONTENTS, currentComponent);
            }
        }
    }

    // endregion
}