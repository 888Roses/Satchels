package net.rose.satchels.common.data_component;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ContainerComponent;
import net.minecraft.world.item.component.GrowableMutableContainer;
import net.minecraft.world.item.slot.SlotSelector;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record SatchelContentsDataComponent(List<ItemStack> stacks, int selectedSlotIndex, int previousSelectedSlotIndex, boolean isOpen) implements TooltipComponent, ContainerComponent<SatchelContentsDataComponent> {
    public static final SatchelContentsDataComponent DEFAULT = new SatchelContentsDataComponent(
            List.of(),
            -1,
            -1,
            false
    );
    /// The maximum amount of item in a single stack stored in the satchel.
    public static final int MAX_STACK_SIZE = 16;

    public static final Codec<SatchelContentsDataComponent> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder
            .create(instance -> instance
                    .group(
                            ItemStack.CODEC.listOf().fieldOf("stacks").forGetter(SatchelContentsDataComponent::stacks),
                            Codec.INT.fieldOf("satchelSlotIndex").forGetter(SatchelContentsDataComponent::selectedSlotIndex),
                            Codec.INT.fieldOf("previousSelectedSlotIndex").forGetter(SatchelContentsDataComponent::previousSelectedSlotIndex),
                            Codec.BOOL.fieldOf("isOpen").forGetter(SatchelContentsDataComponent::isOpen)
                    )
                    .apply(instance, SatchelContentsDataComponent::new)
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, SatchelContentsDataComponent> PACKET_CODEC = StreamCodec.ofMember(
            (value, buf) -> {
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, value.stacks());
                ByteBufCodecs.INT.encode(buf, value.selectedSlotIndex());
                ByteBufCodecs.INT.encode(buf, value.previousSelectedSlotIndex());
                ByteBufCodecs.BOOL.encode(buf, value.isOpen());
            },
            buf -> new SatchelContentsDataComponent(
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf)
            )
    );

    public float getOccupancy() {
        return this.stacks.size() / (float) SatchelItem.MAX_ITEM_COUNT;
    }

    @Override
    public @NonNull Stream<ItemStack> itemCopies() {
        return stacks.stream();
    }

    @Override
    public int size() {
        return stacks.size();
    }

    /*
    isOpen is set to false, cus like idk, uhh me no want copy to be open ?
    Ellie Elysieon
     */
    @Override
    public SatchelContentsDataComponent copyWithContents(@NonNull Stream<ItemStack> newContents) {
        return new SatchelContentsDataComponent(newContents.toList(), selectedSlotIndex, previousSelectedSlotIndex, false);
    }

    @Override
    public @NonNull Mutable<SatchelContentsDataComponent> asMutable() {
        return new Builder(this);
    }

    public static final class Builder extends GrowableMutableContainer<SatchelContentsDataComponent> implements Mutable<SatchelContentsDataComponent> {
        private final List<ItemStack> stacks;
        private int selectedSlotIndex;
        private int previousSelectedSlotIndex;
        private boolean needsFlattening;
        private boolean isOpen;

        public Builder(SatchelContentsDataComponent baseComponent) {
            super(baseComponent.stacks);
            this.stacks = new ArrayList<>(baseComponent.stacks());
            this.selectedSlotIndex = baseComponent.selectedSlotIndex();
            this.previousSelectedSlotIndex = baseComponent.previousSelectedSlotIndex();
            this.isOpen = baseComponent.isOpen();
        }

        public int selectedSlotIndex() {
            return selectedSlotIndex;
        }

        public int previousSelectedSlotIndex() {
            return previousSelectedSlotIndex;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public Builder setSelectedSlotIndex(int selectedSlotIndex) {
            this.selectedSlotIndex = selectedSlotIndex;
            return this;
        }

        public Builder setPreviousSelectedSlotIndex(int previousSelectedSlotIndex) {
            this.previousSelectedSlotIndex = previousSelectedSlotIndex;
            return this;
        }

        public Builder setOpen(boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        /// Makes sure the given [ItemStack] can be inserted in the [#stacks] list.
        public boolean validate(ItemStack itemStack) {
            return !itemStack.isEmpty() && this.stacks.size() < SatchelItem.MAX_ITEM_COUNT && !itemStack.is(ModItemTags.SATCHEL_EXCLUDED);
        }

        /// Tries to add the given [ItemStack] in the [#stacks] list and returns whether it could be added or not.
        public boolean add(ItemStack stack) {
            if (!validate(stack)) {
                return false;
            }

            this.stacks.add(stack);
            this.needsFlattening = true;
            return true;
        }

        /// Tries to remove the selected stack from the satchel. If no stack is in the satchel, returns an empty [Optional].
        public Optional<ItemStack> removeCurrent() {
            if (this.stacks.isEmpty()) {
                return Optional.empty();
            }

            int clampedIndex = Mth.clamp(selectedSlotIndex, 0, this.stacks.size() - 1);
            ItemStack itemStack = this.stacks.get(clampedIndex).copy();
            this.stacks.remove(clampedIndex);
            return Optional.of(itemStack);
        }

        public SatchelContentsDataComponent build() {
            return new SatchelContentsDataComponent(List.of(this.stacks.toArray(ItemStack[]::new)), selectedSlotIndex, previousSelectedSlotIndex, isOpen);
        }

        public Builder clear() {
            stacks.clear();
            selectedSlotIndex = DEFAULT.selectedSlotIndex();
            previousSelectedSlotIndex = DEFAULT.previousSelectedSlotIndex();
            isOpen = DEFAULT.isOpen();
            return this;
        }

        @Override
        public SatchelContentsDataComponent toImmutable() {
            return build();
        }

        @Override
        public Stream<ItemStack> itemCopies() {
            return stacks.stream();
        }

        @Override
        public int size() {
            return stacks.size();
        }

        @Override
        public boolean canInsertNewSlots() {
            return stacks.size() > 3;
        } // ???

        @Override
        public int replaceSlotItems(ItemProvider items, SlotSelector slotSelector) {
            this.mergeIdenticalStacks();
            return replaceSlotItems(items, slotSelector);
        }

        @Override
        public void modifySlots(Consumer<? super SlotAccess> consumer, SlotSelector slotSelector) {

        }

        private void mergeIdenticalStacks() {
            if (this.needsFlattening) {
                for (int index = 0; index < this.items.size(); index++) {
                    ItemStack itemStack = (ItemStack)this.items.get(index);
                    if (itemStack.isEmpty()) {
                        this.items.remove(index--);
                    } else {
                        int stackIndex = this.findStackIndexWithinRange(itemStack, index + 1, this.items.size());
                        if (stackIndex != -1) {
                            ItemStack targetStack = (ItemStack)this.items.get(stackIndex);
                            int mergedCount = itemStack.getCount() + targetStack.getCount();
                            this.items.set(stackIndex, itemStack.copyWithCount(mergedCount));
                            this.items.remove(index--);
                        }
                    }
                }

                this.needsFlattening = false;
            }
        }

        private int findStackIndexWithinRange(final ItemStack itemsToAdd, final int minInclusive, final int maxExclusive) {
            if (!itemsToAdd.isStackable()) {
                return -1;
            } else {
                int startIndex = Math.max(minInclusive, 0);
                int endIndex = Math.min(maxExclusive, this.items.size());

                for (int i = startIndex; i < endIndex; i++) {
                    if (ItemStack.isSameItemSameComponents((ItemStack)this.items.get(i), itemsToAdd)) {
                        return i;
                    }
                }

                return -1;
            }
        }


    }
}