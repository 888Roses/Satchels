package net.rose.satchels.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.client.tooltip.SatchelTooltipComponent;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.item.SatchelItem;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SatchelUseInventoryHudElement implements HudElement {
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer clientPlayer = client.player;
        Font textRenderer = client.font;

        if (clientPlayer == null || textRenderer == null) {
            return;
        }

        ItemStack itemStack = clientPlayer.getInventory().getSelectedItem();

        if (itemStack == null || !itemStack.is(ModItemTags.SATCHELS)) {
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

        int x = graphics.guiWidth() / 2;
        int y = graphics.guiHeight() - 48;

        if (!component.stacks().isEmpty()) {
            int seed = 1;
            int size = component.stacks().size();
            x -= size * 10;

            for (int i = 0; i < size; i++) {
                SatchelTooltipComponent.drawItem(
                        component,
                        seed, x - 2 + i * 20, y,
                        component.stacks(), seed, Minecraft.getInstance().font, graphics
                );

                seed++;
            }

            if (component.selectedSlotIndex() >= 0 && component.selectedSlotIndex() < component.stacks().size()) {
                ItemStack selectedItemStack = component.stacks().get(component.selectedSlotIndex());

                List<ClientTooltipComponent> allComponents = Screen
                        .getTooltipFromItem(Minecraft.getInstance(), selectedItemStack)
                        .stream()
                        .map(Component::getVisualOrderText)
                        .map(ClientTooltipComponent::create)
                        .collect(Util.toMutableList());
                selectedItemStack
                        .getTooltipImage()
                        .ifPresent(data -> allComponents.add(allComponents.isEmpty() ? 0 : 1, ClientTooltipComponent.create(data)));

                Integer componentWidth = allComponents.stream().map(c -> c.getWidth(textRenderer)).max(Integer::compareTo).orElse(2);

                int tooltipHeight = 0;
                for (ClientTooltipComponent tooltipComponent : allComponents) {
                    tooltipHeight += tooltipComponent.getHeight(textRenderer);
                }

                graphics.tooltip(
                        textRenderer, allComponents,
                        graphics.guiWidth() / 2 - componentWidth / 2 - 12,
                        graphics.guiHeight() - 42 - tooltipHeight,
                        DefaultTooltipPositioner.INSTANCE, selectedItemStack.get(DataComponents.TOOLTIP_STYLE)
                );
            }
        }
    }
}
