package com.example.power_scale_mod.client;

import com.example.power_scale_mod.PowerScaleMod;
import com.example.power_scale_mod.client.gui.AttributeSelectionScreen;
import com.example.power_scale_mod.network.payload.ChangeScalePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PowerScaleMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (ModKeyMappings.INCREASE_SCALE_KEY.get().consumeClick()) {
            PacketDistributor.sendToServer(new ChangeScalePayload(1));
        }

        while (ModKeyMappings.DECREASE_SCALE_KEY.get().consumeClick()) {
            PacketDistributor.sendToServer(new ChangeScalePayload(-1));
        }

        while (ModKeyMappings.OPEN_ATTRIBUTE_SCREEN_KEY.get().consumeClick()) {
            mc.setScreen(new AttributeSelectionScreen());
        }
    }
}
