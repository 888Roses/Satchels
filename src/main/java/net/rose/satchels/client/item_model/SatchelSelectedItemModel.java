package net.rose.satchels.client.item_model;

import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;

@Environment(EnvType.CLIENT)
public class SatchelSelectedItemModel implements ItemModel {
    static final ItemModel INSTANCE = new SatchelSelectedItemModel();

    public void update(ItemStackRenderState state, ItemStack itemStack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel world, @Nullable ItemOwner heldItemContext, int seed) {
        state.appendModelIdentityElement(this);

        SatchelContentsDataComponent component = SatchelItem.getSatchelDataComponent(itemStack);
        if (component != null && component.selectedSlotIndex() >= 0 && component.selectedSlotIndex() < component.stacks().size()) {
            if (!itemStack.isEmpty()) {
                ItemStack stack = component.stacks().get(component.selectedSlotIndex());
                resolver.appendItemLayers(state, stack, displayContext, world, heldItemContext, seed);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<net.rose.satchels.client.item_model.SatchelSelectedItemModel.Unbaked> CODEC = MapCodec.unit(new net.rose.satchels.client.item_model.SatchelSelectedItemModel.Unbaked());

        public MapCodec<net.rose.satchels.client.item_model.SatchelSelectedItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation) {
            return INSTANCE;
        }

        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }
    }
}