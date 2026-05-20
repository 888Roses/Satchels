package net.rose.satchels.client.tooltip;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemSlotMouseAction;

public class TooltipSubmenuHandlers {
    public static void addAll(Consumer<Function<Minecraft, ItemSlotMouseAction>> builder) {
        builder.accept(SatchelTooltipSubmenuHandler::new);
    }
}
