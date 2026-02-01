package net.rose.satchels.common.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModDataComponents;

import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SatchelItem extends Item {
    public static final int MAX_ITEM_COUNT = 3;

    public SatchelItem(Settings settings) {
        super(settings);
    }

    // region Util

    /// Gets the [SatchelContentsDataComponent] attached to this [ItemStack].
    public static @Nullable SatchelContentsDataComponent getSatchelDataComponent(ItemStack itemStack) {
        return itemStack.contains(ModDataComponents.SATCHEL_CONTENTS) ? itemStack.get(ModDataComponents.SATCHEL_CONTENTS) : null;
    }

    /// Gets and returns the amount of item stored in this satchel [ItemStack].
    public static int getStoredItemStackCount(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        return component == null ? 0 : component.stacks().size();
    }

    private static void refreshScreenHandler(PlayerEntity user) {
        ScreenHandler screenHandler = user.currentScreenHandler;
        if (screenHandler != null) {
            screenHandler.onContentChanged(user.getInventory());
        }
    }

    // endregion

    // region Sounds

    public static void playInsertSound(PlayerEntity user, boolean failed) {
        SoundEvent soundEvent = failed ? SoundEvents.ITEM_BUNDLE_INSERT_FAIL : SoundEvents.ENTITY_HORSE_SADDLE.value();
        user.playSound(soundEvent, 0.5F, MathHelper.nextFloat(user.getRandom(), 0.98F, 1.02F));
    }

    public static void playRemoveSound(PlayerEntity user) {
        float pitch = MathHelper.nextFloat(user.getRandom(), 1.15F, 1.25F);
        user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), 0.75F, pitch);
    }

    // endregion

    // region In Inventory Behavior

    @Override
    public boolean onStackClicked(ItemStack itemStack, Slot slot, ClickType clickType, PlayerEntity user) {
        // When a satchel item is clicked using another stack.

        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(itemStack);
        if (currentComponent == null) {
            return false;
        }

        ItemStack slotItemStack = slot.getStack();

        // Insert in satchel.
        if (clickType == ClickType.LEFT) {
            if (slotItemStack.isEmpty()) {
                return false;
            }

            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            // The amount of items copied.
            int copiedStackSize = Math.min(SatchelContentsDataComponent.MAX_STACK_SIZE, slotItemStack.getCount());
            if (builder.add(slotItemStack.copyWithCount(copiedStackSize))) {
                slotItemStack.decrement(copiedStackSize);

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
        if (slot.getStack().isEmpty()) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            Optional<ItemStack> removed = builder.removeCurrent();

            if (removed.isPresent()) {
                slot.setStack(removed.get().copy());

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
    public boolean onClicked(ItemStack satchelItemStack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity user, StackReference cursorStackReference) {
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
                itemStackInCursor.decrement(copiedStackSize);

                builder.setSelectedSlotIndex(-1);
                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

                refreshScreenHandler(user);
                playInsertSound(user, false);

                return true;
            }

            playInsertSound(user, true);
            return true;
        }

        if (clickType == ClickType.RIGHT) {
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
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        return Optional.ofNullable(getSatchelDataComponent(itemStack));
    }

    // endregion

    // region Item Bar

    @Override
    public boolean isItemBarVisible(ItemStack itemStack) {
        return getStoredItemStackCount(itemStack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        if (component == null) return 0;

        return Math.round(component.getOccupancy() * 13);
    }

    @Override
    public int getItemBarColor(ItemStack itemStack) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        return component == null ? 0x373737 : component.getOccupancy() < 1F ? 0x5555FF : 0xFF5555;
    }

    // endregion

    // region R-Click Inventory

    /// CLIENT ONLY!!
    // public static ItemStack inspectedItemStack;

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }

        ItemStack satchelItemStack = user.getStackInHand(hand);
        SatchelContentsDataComponent currentComponent = getSatchelDataComponent(satchelItemStack);

        if (currentComponent != null) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(currentComponent);
            builder.setOpen(!builder.isOpen());
            satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());

            if (!builder.isOpen()) {
                Optional<ItemStack> removed = builder.removeCurrent();
                // user.sendMessage(Text.literal("Selected Slot Index: " + builder.satchelSlotIndex()).formatted(user.getEntityWorld().isClient() ? Formatting.YELLOW : Formatting.AQUA), false);
                if (removed.isPresent()) {
                    user.giveOrDropStack(removed.get().copy());

                    builder.setSelectedSlotIndex(-1);
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    user.setStackInHand(hand, satchelItemStack);

                    refreshScreenHandler(user);
                    user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), 0.75F, MathHelper.nextFloat(user.getRandom(), 1.15F, 1.25F));

                    return ActionResult.SUCCESS;
                }

                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                return ActionResult.FAIL;
            }

            // builder.setSelectedSlotIndex(0);
            satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
            user.setStackInHand(hand, satchelItemStack);

            if (currentComponent.stacks().isEmpty()) {
                return ActionResult.FAIL;
            }

            user.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.9F, MathHelper.nextFloat(user.getRandom(), 0.98F, 1.02F));

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
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