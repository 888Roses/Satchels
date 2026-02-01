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
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SatchelUseInventoryHudElement implements HudElement {
    @Override
    public void render(@NonNull DrawContext context, @NonNull RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayer = client.player;
        TextRenderer textRenderer = client.textRenderer;

        if (clientPlayer == null || textRenderer == null) {
            return;
        }

        ItemStack itemStack = clientPlayer.getInventory().getSelectedStack();

        if (itemStack == null || !itemStack.isIn(ModItemTags.SATCHELS)) {
            return;
        }

        SatchelContentsDataComponent component = SatchelItem.getSatchelDataComponent(itemStack);
        
        if (component == null) {
            return;
        }

        // context.drawText(textRenderer, "Index: " + component.satchelSlotIndex(), 0, 0, 0xFFFFFFFF, true);

        if (!component.isOpen() || itemStack.isEmpty()) {
            return;
        }

        int x = context.getScaledWindowWidth() / 2;
        int y = context.getScaledWindowHeight() - 48;

        if (!component.stacks().isEmpty()) {
            int seed = 1;
            int size = component.stacks().size();
            x -= size * 10;

            for (int i = 0; i < size; i++) {
                SatchelTooltipComponent.drawItem(
                        component,
                        seed, x - 2 + i * 20, y,
                        component.stacks(), seed, MinecraftClient.getInstance().textRenderer, context
                );

                seed++;
            }

            if (component.selectedSlotIndex() >= 0 && component.selectedSlotIndex() < component.stacks().size()) {
                ItemStack selectedItemStack = component.stacks().get(component.selectedSlotIndex());

                List<TooltipComponent> allComponents = Screen
                        .getTooltipFromItem(MinecraftClient.getInstance(), selectedItemStack)
                        .stream()
                        .map(Text::asOrderedText)
                        .map(TooltipComponent::of)
                        .collect(Util.toArrayList());
                selectedItemStack
                        .getTooltipData()
                        .ifPresent(data -> allComponents.add(allComponents.isEmpty() ? 0 : 1, TooltipComponent.of(data)));

                Integer componentWidth = allComponents.stream().map(c -> c.getWidth(textRenderer)).max(Integer::compareTo).orElse(2);

                int tooltipHeight = 0;
                for (TooltipComponent tooltipComponent : allComponents) {
                    tooltipHeight += tooltipComponent.getHeight(textRenderer);
                }

                context.drawTooltipImmediately(
                        textRenderer, allComponents,
                        context.getScaledWindowWidth() / 2 - componentWidth / 2 - 12,
                        context.getScaledWindowHeight() - 42 - tooltipHeight,
                        HoveredTooltipPositioner.INSTANCE, selectedItemStack.get(DataComponentTypes.TOOLTIP_STYLE)
                );
            }
        }
    }
}
