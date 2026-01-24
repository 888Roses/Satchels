package net.rose.satchels.common.init;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.registry.RegistryKey;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;

public interface ModDataComponents {
    ComponentType<SatchelContentsDataComponent> SATCHEL_CONTENTS = register(
            Registries.DATA_COMPONENT_TYPE, "satchel_content", ComponentType
                    .<SatchelContentsDataComponent>builder()
                    .codec(SatchelContentsDataComponent.CODEC)
                    .packetCodec(SatchelContentsDataComponent.PACKET_CODEC)
                    .build()
    );

    static <V, T extends V> T register(Registry<V> registry, String id, T entry) {
        return Registry.register(registry, RegistryKey.of(registry.getKey(), Satchels.id(id)), entry);
    }

    static void initialize() {
        Satchels.LOGGER.info("Registered Satchels Data Components");
    }
}
