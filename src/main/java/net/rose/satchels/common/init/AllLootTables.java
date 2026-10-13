package net.rose.satchels.common.init;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.rose.satchels.common.Satchels;

public interface AllLootTables {
    ResourceKey<LootTable> DECREPIT_SATCHELS = ResourceKey.create(Registries.LOOT_TABLE, Satchels.id("chests/decrepit_satchels"));

    static void initialize() {
        LootTableEvents.MODIFY.register((registryKey, builder, lootTableSource, wrapperLookup) -> {
            if (lootTableSource.isBuiltin() && registryKey.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
                LootPool.Builder pool = LootPool.lootPool().add(
                        NestedLootTable.lootTableReference(wrapperLookup.lookupOrThrow(Registries.LOOT_TABLE).getOrThrow(DECREPIT_SATCHELS))
                );
                builder.withPool(pool);
            }
        });


        Satchels.LOGGER.info("Registered Satchel Loot Tables");
    }
}
