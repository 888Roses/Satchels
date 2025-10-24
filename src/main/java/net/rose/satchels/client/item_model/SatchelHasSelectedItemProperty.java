package net.rose.satchels.client.item_model;

import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;

import net.rose.satchels.common.data_component.SatchelContentsComponent;
import net.rose.satchels.common.item.SatchelItem;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SatchelHasSelectedItemProperty() implements BooleanProperty {
    public static final MapCodec<SatchelHasSelectedItemProperty> CODEC = MapCodec.unit(new SatchelHasSelectedItemProperty());

    public boolean test(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        final var maybeComponent = SatchelItem.maybeGetSatchelComponent(itemStack);
        if (maybeComponent.isPresent()) {
            final var component = maybeComponent.get();
            final var stacks = component.stacks();

            if (SatchelContentsComponent.selectedSlotIndex >= 0 && SatchelContentsComponent.selectedSlotIndex < stacks.size()) {
                return !itemStack.isEmpty();
            }
        }

        return false;
    }

    public MapCodec<SatchelHasSelectedItemProperty> getCodec() {
        return CODEC;
    }
}