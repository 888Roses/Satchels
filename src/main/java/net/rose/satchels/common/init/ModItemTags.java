package net.rose.satchels.common.init;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

import net.rose.satchels.common.Satchels;

public interface ModItemTags {
    TagKey<Item> SATCHELS = item("satchels");
    TagKey<Item> SATCHEL_EXCLUDED = item("satchel_excluded");

    private static TagKey<Item> item(String name) {
        return TagKey.of(
                Registries.ITEM.getKey(),
                Satchels.id(name)
        );
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Item Tags");
    }
}
