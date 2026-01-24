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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
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
        if (screenHandler != null) screenHandler.onContentChanged(user.getInventory());
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

    // TODO
    public static void playScrollSound() {
        // var clientPlayer = MinecraftClient.getInstance().player;
        //
        // if (clientPlayer != null) {
        //     clientPlayer.playSound(
        //             SoundEvents.ENTITY_ITEM_PICKUP,
        //             0.3F, MathHelper.nextFloat(clientPlayer.getRandom(), 0.95F, 1.05F)
        //     );
        // }
    }

    // endregion

    // region In Inventory Behavior

    @Override
    public boolean onStackClicked(ItemStack itemStack, Slot slot, ClickType clickType, PlayerEntity user) {
        SatchelContentsDataComponent component = getSatchelDataComponent(itemStack);
        if (component == null) return false;

        ItemStack slotItemStack = slot.getStack();

        // Insert in satchel.
        if (clickType == ClickType.LEFT) {
            if (slotItemStack.isEmpty()) {
                return false;
            }

            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            if (builder.add(slotItemStack.copyWithCount(1))) {
                slotItemStack.decrement(1);
                itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                refreshScreenHandler(user);
                playInsertSound(user, false);
                SatchelContentsDataComponent.selectedSlotIndex = -1;
                return true;
            }

            playInsertSound(user, true);
            return false;
        }

        // Extract from satchel.
        if (slot.getStack().isEmpty()) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            Optional<ItemStack> removed = builder.removeCurrent();

            if (removed.isPresent()) {
                slot.setStack(removed.get().copy());
                itemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                refreshScreenHandler(user);
                playRemoveSound(user);
                SatchelContentsDataComponent.selectedSlotIndex = -1;
                return true;
            }
        }

        return false;

    }

    @Override
    public boolean onClicked(ItemStack satchelItemStack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity user, StackReference cursorStackReference) {
        SatchelContentsDataComponent component = getSatchelDataComponent(satchelItemStack);
        if (component == null) return false;

        ItemStack itemStackInCursor = cursorStackReference.get();

        if (!itemStackInCursor.isEmpty()) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            if (builder.add(itemStackInCursor.copyWithCount(1))) {
                itemStackInCursor.decrement(1);
                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                refreshScreenHandler(user);
                user.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1, MathHelper.nextFloat(user.getRandom(), 0.98F, 1.02F));
                playInsertSound(user, false);
                SatchelContentsDataComponent.selectedSlotIndex = -1;
                return true;
            }

            playInsertSound(user, true);
            return false;
        }

        if (clickType == ClickType.RIGHT) {
            SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(component);
            Optional<ItemStack> removed = builder.removeCurrent();
            if (removed.isPresent()) {
                cursorStackReference.set(removed.get().copy());
                satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                refreshScreenHandler(user);
                playRemoveSound(user);
                SatchelContentsDataComponent.selectedSlotIndex = -1;
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

    public static boolean isUseInventoryOpen = false;
    public static ItemStack useInventoryItemStack = null;
    private static int previousSelectedSlotIndex;

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            return ActionResult.PASS;
        }

        useInventoryItemStack = null;

        isUseInventoryOpen = !isUseInventoryOpen;
        ItemStack satchelItemStack = user.getStackInHand(hand);

        if (!isUseInventoryOpen) {
            SatchelContentsDataComponent satchelComponent = getSatchelDataComponent(satchelItemStack);
            if (satchelComponent != null) {
                SatchelContentsDataComponent.Builder builder = new SatchelContentsDataComponent.Builder(satchelComponent);
                Optional<ItemStack> removed = builder.removeCurrent();
                if (removed.isPresent()) {
                    user.giveItemStack(removed.get().copy());
                    satchelItemStack.set(ModDataComponents.SATCHEL_CONTENTS, builder.build());
                    refreshScreenHandler(user);

                    world.playSound(
                            null, user.getBlockPos(),
                            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(),
                            SoundCategory.PLAYERS,
                            0.75F, MathHelper.nextFloat(user.getRandom(), 1.15F, 1.25F)
                    );

                    SatchelContentsDataComponent.selectedSlotIndex = -1;
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.FAIL;
        }

        SatchelContentsDataComponent.selectedSlotIndex = 0;

        world.playSound(
                null, user.getBlockPos(),
                SoundEvents.ITEM_BUNDLE_DROP_CONTENTS,
                SoundCategory.PLAYERS,
                0.9F, MathHelper.nextFloat(user.getRandom(), 0.98F, 1.02F)
        );

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
                SatchelContentsDataComponent.selectedSlotIndex = 0;
            }
        }

        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (previousSelectedSlotIndex != SatchelContentsDataComponent.selectedSlotIndex) {
                ServerPlayNetworking.send(serverPlayerEntity, new SatchelSelectedSlotS2CPayload(SatchelContentsDataComponent.selectedSlotIndex));
            }
        }

        previousSelectedSlotIndex = SatchelContentsDataComponent.selectedSlotIndex;
    }

    // endregion
}