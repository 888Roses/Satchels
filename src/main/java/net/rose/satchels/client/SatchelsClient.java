package net.rose.satchels.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.rose.satchels.client.item_model.SatchelHasSelectedItemProperty;
import net.rose.satchels.client.item_model.SatchelSelectedItemModel;
import net.rose.satchels.client.overlay.SatchelUseInventoryHudElement;
import net.rose.satchels.client.tooltip.SatchelTooltipComponent;
import net.rose.satchels.common.Satchels;
import net.rose.satchels.common.data_component.SatchelContentsDataComponent;
import net.rose.satchels.common.networking.SatchelSelectedSlotS2CPayload;

public class SatchelsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipComponentCallback.EVENT.register(tooltipData -> {
            if (tooltipData instanceof SatchelContentsDataComponent data) return new SatchelTooltipComponent(data);
            return null;
        });

        HudElementRegistry.addLast(Satchels.id("satchel_use_inventory"), new SatchelUseInventoryHudElement());

        ClientPlayNetworking.registerGlobalReceiver(SatchelSelectedSlotS2CPayload.ID, (payload, context) -> SatchelContentsDataComponent.selectedSlotIndex = payload.selectedItemSlot());

        ItemModelTypes.ID_MAPPER.put(Satchels.id("satchel/selected_item"), SatchelSelectedItemModel.Unbaked.CODEC);
        BooleanProperties.ID_MAPPER.put(Satchels.id("satchel/has_selected_item"), SatchelHasSelectedItemProperty.CODEC);
    }
}
