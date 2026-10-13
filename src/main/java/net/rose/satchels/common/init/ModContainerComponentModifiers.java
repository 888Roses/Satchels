package net.rose.satchels.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ModContainerComponentModifiers {
    ContainerComponentManipulator<SatchelContentsDataComponent> SATCHEL_CONTENTS = new ContainerComponentManipulator<>(
            ModDataComponents.SATCHEL_CONTENTS, SatchelContentsDataComponent.DEFAULT
    );

    Map<DataComponentType<?>, ContainerComponentManipulator<?>> TYPE_TO_MODIFIER = Stream
            .of(
                    ContainerComponentManipulators.CONTAINER,
                    ContainerComponentManipulators.BUNDLE_CONTENTS,
                    ContainerComponentManipulators.CHARGED_PROJECTILES,
                    SATCHEL_CONTENTS
            )
            .collect(Collectors.toMap(
                            ContainerComponentManipulator::type,
                            (containerComponentModifier) -> containerComponentModifier
                    )
            );

    static void initialize() {
    }
}
