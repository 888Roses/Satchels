package net.rose.satchels.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;

public interface ModDataComponents {
    DataComponentType<SatchelContentsDataComponent> SATCHEL_CONTENTS = register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, "satchel_content", DataComponentType
                    .<SatchelContentsDataComponent>builder()
                    .persistent(SatchelContentsDataComponent.CODEC)
                    .networkSynchronized(SatchelContentsDataComponent.PACKET_CODEC)
                    .build()
    );

    static <V, T extends V> T register(Registry<V> registry, String id, T entry) {
        return Registry.register(registry, ResourceKey.create(registry.key(), Satchels.id(id)), entry);
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Data Components");
    }
}
