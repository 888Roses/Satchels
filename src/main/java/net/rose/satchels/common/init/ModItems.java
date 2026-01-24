package net.rose.satchels.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsComponent;
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

    static Item registerSatchel(String satchelColor) {
        String identifier = satchelColor.isEmpty() ? "satchel" : satchelColor + "_satchel";

        return register(
                identifier,
                SatchelItem::new,
                new Item.Settings()
                        .maxCount(1)
                        .component(ModDataComponents.SATCHEL_CONTENTS, SatchelContentsComponent.DEFAULT)
        );
    }

    static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Satchels.id(name));
        return Items.register(key, factory, settings);
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Items");
    }
}
