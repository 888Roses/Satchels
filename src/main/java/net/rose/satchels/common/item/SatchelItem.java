package net.rose.satchels.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

import net.rose.satchels.common.data_component.SatchelContentsComponent;
import net.rose.satchels.common.init.ModDataComponents;

import java.util.Optional;

public class SatchelItem extends Item {
    public static final int MAX_ITEM_COUNT = 3;

    public SatchelItem(Settings settings) {
        super(settings);
    }

    private static Optional<SatchelContentsComponent> maybeGetSatchelComponent(ItemStack itemStack) {
        if (itemStack.contains(ModDataComponents.SATCHEL_CONTENTS)) {
            return Optional.ofNullable(itemStack.get(ModDataComponents.SATCHEL_CONTENTS));
        }

        return Optional.empty();
    }

    public static int getStoredItemStackCount(ItemStack itemStack) {
        final var maybeSatchelComponent = maybeGetSatchelComponent(itemStack);
        if (maybeSatchelComponent.isPresent()) {
            final var satchelComponent = maybeSatchelComponent.get();
            return satchelComponent.stacks().size();
        }

        return 0;
    }

    @Override
    public boolean onStackClicked(ItemStack satchelItemStack, Slot slot, ClickType clickType, PlayerEntity user) {
        final var maybeSatchelComponent = maybeGetSatchelComponent(satchelItemStack);
        if (maybeSatchelComponent.isPresent()) {
            final var satchelComponent = maybeSatchelComponent.get();
            final var itemStackInSlot = slot.getStack();

            if (clickType == ClickType.LEFT) {
                if (itemStackInSlot.isEmpty()) {
                    return false;
                }

                final var builder = new SatchelContentsComponent.Builder(satchelComponent);
                if (builder.add(itemStackInSlot.copyWithCount(1))) {
                    itemStackInSlot.decrement(1);
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);
                    return true;
                }

                return false;
            }

            if (slot.getStack().isEmpty()) {
                final var builder = new SatchelContentsComponent.Builder(satchelComponent);
                final var removed = builder.removeCurrent();
                if (removed.isPresent()) {
                    slot.setStack(removed.get().copy());
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public boolean onClicked(ItemStack satchelItemStack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity user, StackReference cursorStackReference) {
        final var maybeSatchelComponent = maybeGetSatchelComponent(satchelItemStack);
        if (maybeSatchelComponent.isPresent()) {
            final var satchelComponent = maybeSatchelComponent.get();
            final var itemStackInCursor = cursorStackReference.get();

            if (!itemStackInCursor.isEmpty()) {
                final var builder = new SatchelContentsComponent.Builder(satchelComponent);
                if (builder.add(itemStackInCursor.copyWithCount(1))) {
                    itemStackInCursor.decrement(1);
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);
                    return true;
                }

                return false;
            }

            if (clickType == ClickType.RIGHT) {
                final var builder = new SatchelContentsComponent.Builder(satchelComponent);
                final var removed = builder.removeCurrent();
                if (removed.isPresent()) {
                    cursorStackReference.set(removed.get().copy());
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);
                    return true;
                }

                return false;
            }
        }

        return false;
    }

    private static void refreshScreenHandler(PlayerEntity user) {
        final var screenHandler = user.currentScreenHandler;
        if (screenHandler != null) {
            screenHandler.onContentChanged(user.getInventory());
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        return Optional.ofNullable(maybeGetSatchelComponent(itemStack).orElse(null));
    }

    @Override
    public boolean isItemBarVisible(ItemStack itemStack) {
        return getStoredItemStackCount(itemStack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack itemStack) {
        final var maybeSatchelComponent = maybeGetSatchelComponent(itemStack);
        if (maybeSatchelComponent.isPresent()) {
            final var component = maybeSatchelComponent.get();
            return Math.round(component.getOccupancy() * 13);
        }

        return 0;
    }

    @Override
    public int getItemBarColor(ItemStack itemStack) {
        final var maybeSatchelComponent = maybeGetSatchelComponent(itemStack);
        if (maybeSatchelComponent.isPresent()) {
            final var component = maybeSatchelComponent.get();
            return component.getOccupancy() < 1F ? 0x5555FF : 0xFF5555;
        }

        return 0x373737;
    }
}