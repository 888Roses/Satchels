package net.rose.satchels.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;

import java.util.List;
import java.util.Optional;

public record SatchelTooltipComponent(SatchelContentsDataComponent data) implements ClientTooltipComponent {
    private static final Identifier PROGRESS_BAR_BORDER_TEXTURE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_border");
    private static final Identifier PROGRESS_BAR_FILL_TEXTURE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_fill");
    private static final Identifier PROGRESS_BAR_FULL_TEXTURE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_full");
    private static final Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");
    private static final Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_front");
    private static final Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("container/bundle/slot_background");
    private static final Component FULL_TEXT = Component.translatable("item.minecraft.bundle.full");
    private static final Component EMPTY_TEXT = Component.translatable("item.minecraft.bundle.empty");
    private static final Component DESCRIPTION_TEXT = Component.translatable("item.satchels.satchel.desc").withStyle(ChatFormatting.GRAY);
    private static final int FILL_BAR_HEIGHT = 13;
    private static final int FILL_BAR_WIDTH = 96;
    private static final int MAX_WIDTH = FILL_BAR_WIDTH;

    private static int getDescriptionHeight(Font textRenderer) {
        return textRenderer.wordWrapHeight(DESCRIPTION_TEXT, MAX_WIDTH);
    }

    @Override
    public int getHeight(Font textRenderer) {
        return getDescriptionHeight(textRenderer) + FILL_BAR_HEIGHT + 8 + (!this.data.stacks().isEmpty() ? 24 : 0);
    }

    @Override
    public int getWidth(Font textRenderer) {
        return MAX_WIDTH;
    }

    @Override
    public boolean showTooltipWithItemInHand() {
        return true;
    }

    @Override
    public void renderImage(Font textRenderer, int x, int y, int width, int height, GuiGraphics drawContext) {
        drawContext.drawWordWrap(textRenderer, DESCRIPTION_TEXT, x, y, MAX_WIDTH, 0xFFFFFFFF, true);

        int slotIndex = data.selectedSlotIndex();
        // drawContext.drawWrappedText(textRenderer, Text.literal("Index: " + slotIndex), x, y, MAX_WIDTH, 0xFFFFFFFF, true);
        if (slotIndex >= 0 && slotIndex < data.stacks().size()) {
            ItemStack itemStack = data.stacks().get(slotIndex);
            Component text = itemStack.getStyledHoverName();
            int i = textRenderer.width(text.getVisualOrderText());
            int j = x + width / 2 - 12;
            ClientTooltipComponent tooltipComponent = ClientTooltipComponent.create(text.getVisualOrderText());
            drawContext.renderTooltip(textRenderer, List.of(tooltipComponent), j - i / 2, y - 16 - 2, DefaultTooltipPositioner.INSTANCE, itemStack.get(DataComponents.TOOLTIP_STYLE));
        }

        int descriptionHeight = getDescriptionHeight(textRenderer);
        y += descriptionHeight;

        if (!this.data.stacks().isEmpty()) {
            int seed = 1;
            for (int i = 0; i < this.data.stacks().size(); i++) {
                drawItem(data, seed, x - 2 + i * 24, y, this.data.stacks(), seed, textRenderer, drawContext);
                seed++;
            }
            y += 24;
        }

        y += 2;

        this.drawProgressBar(x, y, textRenderer, drawContext);
    }

    public static void drawItem(SatchelContentsDataComponent data, int index, int x, int y, List<ItemStack> stacks, int seed, Font textRenderer, GuiGraphics drawContext) {
        int slotIndex = index - 1;
        boolean isSlotSelected = slotIndex == data.selectedSlotIndex();
        ItemStack itemStack = stacks.get(slotIndex);

        Identifier slotTexture = isSlotSelected ? BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE : BUNDLE_SLOT_BACKGROUND_TEXTURE;
        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, slotTexture, x, y, 24, 24);

        drawContext.renderItem(itemStack, x + 4, y + 4, seed);
        drawContext.renderItemDecorations(textRenderer, itemStack, x + 4, y + 4);

        if (isSlotSelected) {
            drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, 24, 24);
        }
    }

    private void drawProgressBar(int x, int y, Font textRenderer, GuiGraphics drawContext) {
        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, this.getProgressBarFillTexture(), x + 1, y, this.getProgressBarFill(), FILL_BAR_HEIGHT);
        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BAR_BORDER_TEXTURE, x, y, FILL_BAR_WIDTH, FILL_BAR_HEIGHT);

        this.getProgressBarLabel().ifPresent(text ->
                drawContext.drawCenteredString(textRenderer, text, x + FILL_BAR_WIDTH / 2, y + 3, CommonColors.WHITE)
        );
    }

    private int getProgressBarFill() {
        return (int) Mth.clamp(this.data.getOccupancy() * (FILL_BAR_WIDTH - 2), 0, FILL_BAR_WIDTH - 2);
    }

    private Identifier getProgressBarFillTexture() {
        return this.data.getOccupancy() >= 1 ? PROGRESS_BAR_FULL_TEXTURE : PROGRESS_BAR_FILL_TEXTURE;
    }

    private Optional<Component> getProgressBarLabel() {
        if (this.data.stacks().isEmpty()) {
            return Optional.of(EMPTY_TEXT);
        }

        return Optional.ofNullable(this.data.getOccupancy() >= 1 ? FULL_TEXT : null);
    }
}
