package net.rose.satchels.client.item_model;

import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;

import net.rose.satchels.common.data_component.SatchelContentsComponent;
import net.rose.satchels.common.item.SatchelItem;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SatchelSelectedItemModel implements ItemModel {
    static final ItemModel INSTANCE = new SatchelSelectedItemModel();

    public void update(ItemRenderState state, ItemStack itemStack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        state.addModelKey(this);

        final var maybeComponent = SatchelItem.maybeGetSatchelComponent(itemStack);
        if (maybeComponent.isPresent()) {
            final var component = maybeComponent.get();
            final var stacks = component.stacks();

            if (SatchelContentsComponent.selectedSlotIndex >= 0 && SatchelContentsComponent.selectedSlotIndex < stacks.size()) {
                final var stack = stacks.get(SatchelContentsComponent.selectedSlotIndex);
                if (!itemStack.isEmpty()) {
                    resolver.update(state, stack, displayContext, world, heldItemContext, seed);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        public ItemModel bake(ItemModel.BakeContext context) {
            return INSTANCE;
        }

        public void resolve(ResolvableModel.Resolver resolver) {
        }
    }
}