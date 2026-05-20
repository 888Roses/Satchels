package net.rose.satchels.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.init.ModItems;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(ModItemTags.SATCHELS)
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.WHITE_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.LIGHT_GRAY_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.GRAY_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.BLACK_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.BROWN_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.RED_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.ORANGE_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.YELLOW_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.LIME_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.GREEN_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.CYAN_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.LIGHT_BLUE_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.BLUE_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.PURPLE_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.MAGENTA_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.PINK_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.BROWN_DECREPIT_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.GREEN_DECREPIT_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.PURPLE_DECREPIT_SATCHEL))
                .build();

        getOrCreateRawBuilder(ModItemTags.DECREPIT_SATCHELS)
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.BROWN_DECREPIT_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.GREEN_DECREPIT_SATCHEL))
                .addElement(BuiltInRegistries.ITEM.getKey(ModItems.PURPLE_DECREPIT_SATCHEL))
                .build();

        getOrCreateRawBuilder(ModItemTags.SATCHEL_EXCLUDED)
                .addTag(ModItemTags.SATCHELS.location())
                .addOptionalTag(ItemTags.SHULKER_BOXES.location())
                .addOptionalTag(ItemTags.BUNDLES.location())
                .build();
    }
}