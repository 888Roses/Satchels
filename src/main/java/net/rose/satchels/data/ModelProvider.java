package net.rose.satchels.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.rose.satchels.common.ModItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.WHITE_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_GRAY_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GRAY_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLACK_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROWN_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.RED_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORANGE_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.YELLOW_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIME_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GREEN_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.CYAN_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_BLUE_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLUE_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURPLE_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.MAGENTA_SATCHEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.PINK_SATCHEL, Models.GENERATED);
    }
}
