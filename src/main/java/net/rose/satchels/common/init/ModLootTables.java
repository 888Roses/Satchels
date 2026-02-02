package net.rose.satchels.common.init;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.rose.satchels.common.Satchels;

public interface ModLootTables {
    RegistryKey<LootTable> DECREPIT_SATCHELS = RegistryKey.of(RegistryKeys.LOOT_TABLE, Satchels.id("chests/decrepit_satchel"));

    static void initialize() {}
}
