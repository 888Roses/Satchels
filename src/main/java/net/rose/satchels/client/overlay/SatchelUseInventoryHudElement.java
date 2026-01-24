package net.rose.satchels.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import net.rose.satchels.client.tooltip.SatchelTooltipComponent;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.item.SatchelItem;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static net.rose.satchels.common.data_component.SatchelContentsDataComponent.selectedSlotIndex;
import static net.rose.satchels.common.item.SatchelItem.useInventoryItemStack;

public class SatchelUseInventoryHudElement implements HudElement {
    @Override
    public void render(@NonNull DrawContext context, @NonNull RenderTickCounter tickCounter) {
        ItemStack satchelStack = useInventoryItemStack;
        if (!SatchelItem.isUseInventoryOpen || satchelStack == null || satchelStack.isEmpty()) {
            return;
        }

        SatchelContentsDataComponent satchelComponent = SatchelItem.getSatchelDataComponent(satchelStack);
        if (satchelComponent != null) {

            int x = context.getScaledWindowWidth() / 2;
            int y = context.getScaledWindowHeight() - 48;
            if (!satchelComponent.stacks().isEmpty()) {
                int seed = 1;
                int size = satchelComponent.stacks().size();
                x -= size * 10;

                for (int i = 0; i < size; i++) {
                    SatchelTooltipComponent.drawItem(
                            seed, x - 2 + i * 20, y,
                            satchelComponent.stacks(), seed, MinecraftClient.getInstance().textRenderer, context
                    );
                    seed++;
                }


                if (selectedSlotIndex >= 0 && selectedSlotIndex < satchelComponent.stacks().size()) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    ItemStack itemStack = satchelComponent.stacks().get(selectedSlotIndex);

                    ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
                    if (clientPlayer != null) {
                        List<TooltipComponent> allComponents = Screen
                                .getTooltipFromItem(MinecraftClient.getInstance(), itemStack)
                                .stream()
                                .map(Text::asOrderedText)
                                .map(TooltipComponent::of)
                                .collect(Util.toArrayList());
                        itemStack
                                .getTooltipData()
                                .ifPresent(data -> allComponents.add(allComponents.isEmpty() ? 0 : 1, TooltipComponent.of(data)));

                        Integer componentWidth = allComponents.stream().map(c -> c.getWidth(textRenderer)).max(Integer::compareTo).orElse(2);

                        int tooltipHeight = 0;
                        for (TooltipComponent component : allComponents) tooltipHeight += component.getHeight(textRenderer);

                        context.drawTooltipImmediately(
                                textRenderer, allComponents,
                                context.getScaledWindowWidth() / 2 - componentWidth / 2 - 12,
                                context.getScaledWindowHeight() - 42 - tooltipHeight,
                                HoveredTooltipPositioner.INSTANCE, itemStack.get(DataComponentTypes.TOOLTIP_STYLE)
                        );
                    }
                }
            }
        }
    }
}
