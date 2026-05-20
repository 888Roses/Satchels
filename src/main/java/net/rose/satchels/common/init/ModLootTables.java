package net.rose.satchels.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.rose.satchels.common.Satchels;

public interface ModLootTables {
    ResourceKey<LootTable> DECREPIT_SATCHELS = ResourceKey.create(Registries.LOOT_TABLE, Satchels.id("chests/decrepit_satchel"));

    static void initialize() {}
}
