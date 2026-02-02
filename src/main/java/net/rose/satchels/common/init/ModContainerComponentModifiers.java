package net.rose.satchels.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.ContainerComponentModifiers;
import net.minecraft.registry.Registries;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ModContainerComponentModifiers {
    ContainerComponentModifier<SatchelContentsDataComponent> SATCHEL_CONTENTS = new ContainerComponentModifier<>() {
        public ComponentType<SatchelContentsDataComponent> getComponentType() {
            return ModDataComponents.SATCHEL_CONTENTS;
        }

        public SatchelContentsDataComponent getDefault() {
            return SatchelContentsDataComponent.DEFAULT;
        }

        public Stream<ItemStack> stream(SatchelContentsDataComponent bundleContentsComponent) {
            return bundleContentsComponent.stacks().stream();
        }

        public SatchelContentsDataComponent apply(SatchelContentsDataComponent component, Stream<ItemStack> stream) {
            SatchelContentsDataComponent.Builder builder = (new SatchelContentsDataComponent.Builder(component)).clear();
            Objects.requireNonNull(builder);
            stream.forEach(builder::add);
            return builder.build();
        }
    };

    Map<ComponentType<?>, ContainerComponentModifier<?>> TYPE_TO_MODIFIER = Stream
            .of(
                    ContainerComponentModifiers.CONTAINER,
                    ContainerComponentModifiers.BUNDLE_CONTENTS,
                    ContainerComponentModifiers.CHARGED_PROJECTILES,
                    SATCHEL_CONTENTS
            )
            .collect(Collectors.toMap(
                            ContainerComponentModifier::getComponentType,
                            (containerComponentModifier) -> containerComponentModifier
                    )
            );

    static void initialize() {
    }
}
