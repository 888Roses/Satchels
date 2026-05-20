package net.rose.satchels.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.rose.satchels.client.item_model.SatchelHasSelectedItemProperty;
import net.rose.satchels.client.item_model.SatchelSelectedItemModel;
import net.rose.satchels.common.init.ModItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        registerSatchel(itemModelGenerator, ModItems.SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.WHITE_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.LIGHT_GRAY_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.GRAY_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.BLACK_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.BROWN_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.RED_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.ORANGE_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.YELLOW_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.LIME_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.GREEN_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.CYAN_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.LIGHT_BLUE_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.BLUE_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.PURPLE_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.MAGENTA_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.PINK_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.BROWN_DECREPIT_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.GREEN_DECREPIT_SATCHEL);
        registerSatchel(itemModelGenerator, ModItems.PURPLE_DECREPIT_SATCHEL);
    }

    private void registerSatchel(ItemModelGenerators itemModelGenerator, Item item) {
        ItemModel.Unbaked generated = ItemModelUtils.plainModel(itemModelGenerator.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        Identifier openBackIdentifier = uploadOpenSatchelModel(itemModelGenerator, item, ModelTemplates.BUNDLE_OPEN_BACK_INVENTORY, "_back");
        Identifier openFrontIdentifier = uploadOpenSatchelModel(itemModelGenerator, item, ModelTemplates.BUNDLE_OPEN_FRONT_INVENTORY, "_front");
        ItemModel.Unbaked selectedItemModel = ItemModelUtils.composite(ItemModelUtils.plainModel(openBackIdentifier), new SatchelSelectedItemModel.Unbaked(), ItemModelUtils.plainModel(openFrontIdentifier));
        ItemModel.Unbaked effectiveModel = ItemModelUtils.conditional(new SatchelHasSelectedItemProperty(), selectedItemModel, generated);
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.select(new DisplayContext(), generated, ItemModelUtils.when(ItemDisplayContext.GUI, effectiveModel)));
    }

    private Identifier uploadOpenSatchelModel(ItemModelGenerators itemModelGenerator, Item item, ModelTemplate model, String textureSuffix) {
        Material material = TextureMapping.getItemTexture(item, textureSuffix);
        return model.create(item, TextureMapping.layer0(material), itemModelGenerator.modelOutput);
    }
}
