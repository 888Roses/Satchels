package net.rose.satchels.common.data_component;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.MathHelper;

import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;

import java.util.*;

public record SatchelContentsDataComponent(List<ItemStack> stacks, int selectedSlotIndex, int previousSelectedSlotIndex, boolean isOpen) implements TooltipData {
    public static final SatchelContentsDataComponent DEFAULT = new SatchelContentsDataComponent(List.of(), 0, 0, false);
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

    public static final PacketCodec<RegistryByteBuf, SatchelContentsDataComponent> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> {
                ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).encode(buf, value.stacks());
                PacketCodecs.INTEGER.encode(buf, value.selectedSlotIndex());
                PacketCodecs.INTEGER.encode(buf, value.previousSelectedSlotIndex());
                PacketCodecs.BOOLEAN.encode(buf, value.isOpen());
            },
            buf -> new SatchelContentsDataComponent(
                    ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.BOOLEAN.decode(buf)
            )
    );

    public float getOccupancy() {
        return this.stacks.size() / (float) SatchelItem.MAX_ITEM_COUNT;
    }

    public static final class Builder {
        private final List<ItemStack> stacks;
        private int selectedSlotIndex;
        private int previousSelectedSlotIndex;
        private boolean isOpen;

        public Builder(SatchelContentsDataComponent baseComponent) {
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
            return !itemStack.isEmpty() && this.stacks.size() < SatchelItem.MAX_ITEM_COUNT && !itemStack.isIn(ModItemTags.SATCHEL_EXCLUDED);
        }

        /// Tries to add the given [ItemStack] in the [#stacks] list and returns whether it could be added or not.
        public boolean add(ItemStack stack) {
            if (!validate(stack)) {
                return false;
            }

            this.stacks.add(stack);
            return true;
        }

        /// Tries to remove the selected stack from the satchel. If no stack is in the satchel, returns an empty [Optional].
        public Optional<ItemStack> removeCurrent() {
            if (this.stacks.isEmpty()) {
                return Optional.empty();
            }

            int clampedIndex = MathHelper.clamp(selectedSlotIndex, 0, this.stacks.size() - 1);
            ItemStack itemStack = this.stacks.get(clampedIndex).copy();
            this.stacks.remove(clampedIndex);
            return Optional.of(itemStack);
        }

        public SatchelContentsDataComponent build() {
            return new SatchelContentsDataComponent(List.of(this.stacks.toArray(ItemStack[]::new)), selectedSlotIndex, previousSelectedSlotIndex, isOpen);
        }
    }
}