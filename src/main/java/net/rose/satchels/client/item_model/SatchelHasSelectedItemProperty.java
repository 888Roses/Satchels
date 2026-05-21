package net.rose.satchels.client.item_model;

import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SatchelHasSelectedItemProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<SatchelHasSelectedItemProperty> CODEC = MapCodec.unit(new SatchelHasSelectedItemProperty());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        SatchelContentsDataComponent component = SatchelItem.getSatchelDataComponent(itemStack);

        if (component != null && component.selectedSlotIndex() >= 0 && component.selectedSlotIndex() < component.stacks().size()) {
            return !itemStack.isEmpty();
        }

        return false;
    }

    public MapCodec<SatchelHasSelectedItemProperty> type() {
        return CODEC;
    }
}