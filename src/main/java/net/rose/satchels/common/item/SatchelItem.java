package net.rose.satchels.common.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.rose.satchels.common.data_component.SatchelContentsComponent;
import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.networking.SatchelSelectedSlotS2CPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SatchelItem extends Item {
    public static final int MAX_ITEM_COUNT = 3;

    public SatchelItem(Settings settings) {
        super(settings);
    }

    // region Component Interaction

    public static Optional<SatchelContentsComponent> maybeGetSatchelComponent(ItemStack itemStack) {
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

    // endregion

    // region Inventory

    private static void refreshScreenHandler(PlayerEntity user) {
        final var screenHandler = user.currentScreenHandler;
        if (screenHandler != null) {
            screenHandler.onContentChanged(user.getInventory());
        }
    }

    // endregion

    // region In Inventory Behaviour

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

    // endregion

    // region Tooltip

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        return Optional.ofNullable(maybeGetSatchelComponent(itemStack).orElse(null));
    }

    // endregion

    // region Item Bar

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

    // endregion

    // region R-Click Inventory

    public static boolean isUseInventoryOpen = false;
    public static ItemStack useInventoryItemStack = null;
    private static int previousSelectedSlotIndex;

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return ActionResult.PASS;
        }

        useInventoryItemStack = null;

        isUseInventoryOpen = !isUseInventoryOpen;
        final var satchelItemStack = user.getStackInHand(hand);

        if (!isUseInventoryOpen) {
            final var maybeSatchelComponent = maybeGetSatchelComponent(satchelItemStack);
            if (maybeSatchelComponent.isPresent()) {
                final var satchelComponent = maybeSatchelComponent.get();
                final var builder = new SatchelContentsComponent.Builder(satchelComponent);
                final var removed = builder.removeCurrent();
                if (removed.isPresent()) {
                    user.giveItemStack(removed.get().copy());
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.FAIL;
        } else {
            SatchelContentsComponent.selectedSlotIndex = 0;
        }

        useInventoryItemStack = satchelItemStack;
        return ActionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!isUseInventoryOpen) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.getMainHandStack().isIn(ModItemTags.SATCHELS) && !livingEntity.getOffHandStack().isIn(ModItemTags.SATCHELS)) {
                isUseInventoryOpen = false;
                useInventoryItemStack = null;
                SatchelContentsComponent.selectedSlotIndex = 0;
            }
        }

        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (previousSelectedSlotIndex != SatchelContentsComponent.selectedSlotIndex) {
                ServerPlayNetworking.send(serverPlayerEntity, new SatchelSelectedSlotS2CPayload(SatchelContentsComponent.selectedSlotIndex));
            }
        }

        previousSelectedSlotIndex = SatchelContentsComponent.selectedSlotIndex;
    }

    // endregion
}