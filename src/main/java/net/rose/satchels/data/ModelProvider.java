package net.rose.satchels.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.client.data.*;

import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.rose.satchels.client.item_model.SatchelHasSelectedItemProperty;
import net.rose.satchels.client.item_model.SatchelSelectedItemModel;
import net.rose.satchels.common.init.ModItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
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
    }

    private void registerSatchel(ItemModelGenerator itemModelGenerator, Item item) {
        ItemModel.Unbaked generated = ItemModels.basic(itemModelGenerator.upload(item, Models.GENERATED));
        Identifier openBackIdentifier = uploadOpenSatchelModel(itemModelGenerator, item, Models.TEMPLATE_BUNDLE_OPEN_BACK, "_back");
        Identifier openFrontIdentifier = uploadOpenSatchelModel(itemModelGenerator, item, Models.TEMPLATE_BUNDLE_OPEN_FRONT, "_front");
        ItemModel.Unbaked selectedItemModel = ItemModels.composite(ItemModels.basic(openBackIdentifier), new SatchelSelectedItemModel.Unbaked(), ItemModels.basic(openFrontIdentifier));
        ItemModel.Unbaked effectiveModel = ItemModels.condition(new SatchelHasSelectedItemProperty(), selectedItemModel, generated);
        itemModelGenerator.output.accept(item, ItemModels.select(new DisplayContextProperty(), generated, ItemModels.switchCase(ItemDisplayContext.GUI, effectiveModel)));
    }

    private Identifier uploadOpenSatchelModel(ItemModelGenerator itemModelGenerator, Item item, Model model, String textureSuffix) {
        Identifier identifier = TextureMap.getSubId(item, textureSuffix);
        return model.upload(item, TextureMap.layer0(identifier), itemModelGenerator.modelCollector);
    }
}
