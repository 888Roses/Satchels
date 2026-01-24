package net.rose.satchels.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import net.minecraft.registry.tag.ItemTags;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.init.ModItems;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.@NonNull WrapperLookup wrapperLookup) {
        getTagBuilder(ModItemTags.SATCHELS)
                .add(Registries.ITEM.getId(ModItems.SATCHEL))
                .add(Registries.ITEM.getId(ModItems.WHITE_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.LIGHT_GRAY_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.GRAY_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.BLACK_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.BROWN_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.RED_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.ORANGE_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.YELLOW_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.LIME_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.GREEN_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.CYAN_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.LIGHT_BLUE_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.BLUE_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.PURPLE_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.MAGENTA_SATCHEL))
                .add(Registries.ITEM.getId(ModItems.PINK_SATCHEL))
                .build();

        getTagBuilder(ModItemTags.SATCHEL_EXCLUDED)
                .addTag(ModItemTags.SATCHELS.id())
                .addOptionalTag(ItemTags.SHULKER_BOXES.id())
                .addOptionalTag(ItemTags.BUNDLES.id())
                .build();
    }
}