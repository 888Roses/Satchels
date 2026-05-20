package net.rose.satchels.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.rose.satchels.common.init.ModContainerComponentModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ContainerComponentManipulators.class)
public interface ContainerComponentModifierMixin {
    /**
     * @author Rosenoire_
     * @reason Add satchel container component modifier.
     */
    @Overwrite
    private static DataResult<?> method_59729(DataComponentType<?> componentType) {
        ContainerComponentManipulator<?> containerComponentModifier = ModContainerComponentModifiers.TYPE_TO_MODIFIER.get(componentType);
        return containerComponentModifier != null
                ? DataResult.success(containerComponentModifier)
                : DataResult.error(() -> "Container component modifier name is invalid.");
    }
}
