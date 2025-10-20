package net.rose.satchels.common;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class ModItems {
    public static final Item SATCHEL = registerSatchel("");
    public static final Item WHITE_SATCHEL = registerSatchel("white");
    public static final Item LIGHT_GRAY_SATCHEL = registerSatchel("light_gray");
    public static final Item GRAY_SATCHEL = registerSatchel("gray");
    public static final Item BLACK_SATCHEL = registerSatchel("black");
    public static final Item BROWN_SATCHEL = registerSatchel("brown");
    public static final Item RED_SATCHEL = registerSatchel("red");
    public static final Item ORANGE_SATCHEL = registerSatchel("orange");
    public static final Item YELLOW_SATCHEL = registerSatchel("yellow");
    public static final Item LIME_SATCHEL = registerSatchel("lime");
    public static final Item GREEN_SATCHEL = registerSatchel("green");
    public static final Item CYAN_SATCHEL = registerSatchel("cyan");
    public static final Item LIGHT_BLUE_SATCHEL = registerSatchel("light_blue");
    public static final Item BLUE_SATCHEL = registerSatchel("blue");
    public static final Item PURPLE_SATCHEL = registerSatchel("purple");
    public static final Item MAGENTA_SATCHEL = registerSatchel("magenta");
    public static final Item PINK_SATCHEL = registerSatchel("pink");

    public static Item registerSatchel(String colorName) {
        final var name = colorName.isEmpty() ? "satchel" : colorName + "_satchel";
        return register(name, Item::new, new Item.Settings().maxCount(1));
    }

    public static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final var key = RegistryKey.of(RegistryKeys.ITEM, Satchels.identifier(name));
        return Items.register(key, factory, settings);
    }

    public static void initialize() {}
}
