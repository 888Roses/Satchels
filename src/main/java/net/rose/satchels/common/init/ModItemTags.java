package net.rose.satchels.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.rose.satchels.common.Satchels;

public interface ModItemTags {
    TagKey<Item> SATCHELS = item("satchels");
    TagKey<Item> SATCHEL_EXCLUDED = item("satchel_excluded");

    TagKey<Item> DECREPIT_SATCHELS = item("decrepit_satchels");

    private static TagKey<Item> item(String name) {
        return TagKey.create(
                BuiltInRegistries.ITEM.key(),
                Satchels.id(name)
        );
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Item Tags");
    }
}
