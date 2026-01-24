package net.rose.satchels.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import net.rose.satchels.common.init.ModDataComponents;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.init.ModItems;
import net.rose.satchels.common.networking.SatchelSelectedSlotS2CPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Satchels implements ModInitializer {
    public static final String MOD_ID = "satchels";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModItemTags.initialize();
        ModDataComponents.initialize();

        PayloadTypeRegistry.playS2C().register(SatchelSelectedSlotS2CPayload.ID, SatchelSelectedSlotS2CPayload.CODEC);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.addAfter(
                    Items.PINK_BUNDLE,
                    ModItems.SATCHEL,
                    ModItems.WHITE_SATCHEL,
                    ModItems.LIGHT_GRAY_SATCHEL,
                    ModItems.GRAY_SATCHEL,
                    ModItems.BLACK_SATCHEL,
                    ModItems.BROWN_SATCHEL,
                    ModItems.RED_SATCHEL,
                    ModItems.ORANGE_SATCHEL,
                    ModItems.YELLOW_SATCHEL,
                    ModItems.LIME_SATCHEL,
                    ModItems.GREEN_SATCHEL,
                    ModItems.CYAN_SATCHEL,
                    ModItems.LIGHT_BLUE_SATCHEL,
                    ModItems.BLUE_SATCHEL,
                    ModItems.PURPLE_SATCHEL,
                    ModItems.MAGENTA_SATCHEL,
                    ModItems.PINK_SATCHEL
            );
        });
    }
}
