package net.rose.satchels.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.rose.satchels.client.item_model.SatchelHasSelectedItemProperty;
import net.rose.satchels.client.item_model.SatchelSelectedItemModel;
import net.rose.satchels.client.overlay.SatchelUseInventoryHudElement;
import net.rose.satchels.client.tooltip.SatchelTooltipComponent;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.networking.SetSatchelSlotIndexC2S;

public class SatchelsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipComponentCallback.EVENT.register(tooltipData -> {
            if (tooltipData instanceof SatchelContentsDataComponent data) return new SatchelTooltipComponent(data);
            return null;
        });

        HudElementRegistry.addLast(Satchels.id("satchel_use_inventory"), new SatchelUseInventoryHudElement());

        ItemModels.ID_MAPPER.put(Satchels.id("satchel/selected_item"), SatchelSelectedItemModel.Unbaked.CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Satchels.id("satchel/has_selected_item"), SatchelHasSelectedItemProperty.CODEC);
    }

    public static void playScrollSound() {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;

        float pitch = Mth.nextFloat(clientPlayer.getRandom(), 1.95f, 2f);
        clientPlayer.playSound(SoundEvents.BUBBLE_POP, 0.5f, pitch);
    }
}
