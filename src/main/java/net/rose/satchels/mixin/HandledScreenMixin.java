package net.rose.satchels.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.rose.satchels.client.tooltip.TooltipSubmenuHandlers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("CodeBlock2Expr")
@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {
    @SuppressWarnings("rawtypes")
    @Inject(method = "init", at = @At("TAIL"))
    private void satchels$init(CallbackInfo ci) {
        AbstractContainerScreen screen = (AbstractContainerScreen) (Object) this;
        TooltipSubmenuHandlers.addAll(builder -> {
            screen.addItemSlotMouseAction(builder.apply(screen.minecraft));
        });
    }
}
