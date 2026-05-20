package net.rose.satchels.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;

import java.util.function.Function;

public interface ModItems {
    Item SATCHEL = registerSatchel("");
    Item WHITE_SATCHEL = registerSatchel("white");
    Item LIGHT_GRAY_SATCHEL = registerSatchel("light_gray");
    Item GRAY_SATCHEL = registerSatchel("gray");
    Item BLACK_SATCHEL = registerSatchel("black");
    Item BROWN_SATCHEL = registerSatchel("brown");
    Item RED_SATCHEL = registerSatchel("red");
    Item ORANGE_SATCHEL = registerSatchel("orange");
    Item YELLOW_SATCHEL = registerSatchel("yellow");
    Item LIME_SATCHEL = registerSatchel("lime");
    Item GREEN_SATCHEL = registerSatchel("green");
    Item CYAN_SATCHEL = registerSatchel("cyan");
    Item LIGHT_BLUE_SATCHEL = registerSatchel("light_blue");
    Item BLUE_SATCHEL = registerSatchel("blue");
    Item PURPLE_SATCHEL = registerSatchel("purple");
    Item MAGENTA_SATCHEL = registerSatchel("magenta");
    Item PINK_SATCHEL = registerSatchel("pink");

    Item BROWN_DECREPIT_SATCHEL = registerSatchel("brown_decrepit");
    Item GREEN_DECREPIT_SATCHEL = registerSatchel("green_decrepit");
    Item PURPLE_DECREPIT_SATCHEL = registerSatchel("purple_decrepit");

    static Item registerSatchel(String satchelColor) {
        String identifier = satchelColor.isEmpty() ? "satchel" : satchelColor + "_satchel";

        return register(
                identifier,
                SatchelItem::new,
                new Item.Properties()
                        .stacksTo(1)
                        .component(ModDataComponents.SATCHEL_CONTENTS, SatchelContentsDataComponent.DEFAULT)
        );
    }

    static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Satchels.id(name));
        return Items.registerItem(key, factory, settings);
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Items");
    }
}
