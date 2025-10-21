package net.rose.satchels.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.registry.RegistryWrapper;

import net.rose.satchels.common.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class LangProvider extends FabricLanguageProvider {
    public LangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.SATCHEL, "Satchel");
        translationBuilder.add(ModItems.WHITE_SATCHEL, "White Satchel");
        translationBuilder.add(ModItems.LIGHT_GRAY_SATCHEL, "Light Gray Satchel");
        translationBuilder.add(ModItems.GRAY_SATCHEL, "Gray Satchel");
        translationBuilder.add(ModItems.BLACK_SATCHEL, "Black Satchel");
        translationBuilder.add(ModItems.BROWN_SATCHEL, "Brown Satchel");
        translationBuilder.add(ModItems.RED_SATCHEL, "Red Satchel");
        translationBuilder.add(ModItems.ORANGE_SATCHEL, "Orange Satchel");
        translationBuilder.add(ModItems.YELLOW_SATCHEL, "Yellow Satchel");
        translationBuilder.add(ModItems.LIME_SATCHEL, "Lime Satchel");
        translationBuilder.add(ModItems.GREEN_SATCHEL, "Green Satchel");
        translationBuilder.add(ModItems.CYAN_SATCHEL, "Cyan Satchel");
        translationBuilder.add(ModItems.LIGHT_BLUE_SATCHEL, "Light Blue Satchel");
        translationBuilder.add(ModItems.BLUE_SATCHEL, "Blue Satchel");
        translationBuilder.add(ModItems.PURPLE_SATCHEL, "Purple Satchel");
        translationBuilder.add(ModItems.MAGENTA_SATCHEL, "Magenta Satchel");
        translationBuilder.add(ModItems.PINK_SATCHEL, "Pink Satchel");

        translationBuilder.add("item.satchels.satchel.desc", "Can hold multiple items of any stack size");
    }
}
