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
import org.jspecify.annotations.NonNull;

import java.util.*;

public record SatchelContentsDataComponent(List<ItemStack> stacks) implements TooltipData {
    public static final SatchelContentsDataComponent DEFAULT = new SatchelContentsDataComponent(List.of());
    public static int selectedSlotIndex;

    // region Serialization

    public static final Codec<SatchelContentsDataComponent> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder
            .create(instance -> instance
                    .group(ItemStack.CODEC.listOf().fieldOf("stacks").forGetter(SatchelContentsDataComponent::stacks))
                    .apply(instance, SatchelContentsDataComponent::new)
            ));

    public static final PacketCodec<RegistryByteBuf, SatchelContentsDataComponent> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).encode(buf, value.stacks),
            buf -> new SatchelContentsDataComponent(ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).decode(buf))
    );

    // endregion

    // region Implementation

    public @NonNull String toString() {
        return "SatchelContents " + this.stacks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof SatchelContentsDataComponent(List<ItemStack> otherStacks))) return false;
        if (this.stacks.size() != otherStacks.size()) return false;

        for (int i = 0; i < this.stacks.size(); i++) {
            if (!ItemStack.areEqual(this.stacks.get(i), otherStacks.get(i))) return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int hashCode() {
        return ItemStack.listHashCode(this.stacks);
    }

    // endregion

    public float getOccupancy() {
        return this.stacks.size() / (float) SatchelItem.MAX_ITEM_COUNT;
    }

    public static final class Builder {
        private final List<ItemStack> stacks;

        public Builder(SatchelContentsDataComponent baseComponent) {
            this.stacks = new ArrayList<>(baseComponent.stacks);
        }

        public boolean add(ItemStack stack) {
            if (stack.isEmpty() || this.stacks.size() >= SatchelItem.MAX_ITEM_COUNT || stack.isIn(ModItemTags.SATCHEL_EXCLUDED)) {
                return false;
            }

            this.stacks.add(stack);
            return true;
        }

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
            return new SatchelContentsDataComponent(List.of(this.stacks.toArray(ItemStack[]::new)));
        }
    }
}