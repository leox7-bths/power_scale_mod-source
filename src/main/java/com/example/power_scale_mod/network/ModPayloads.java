package com.example.power_scale_mod.network;

import com.example.power_scale_mod.PowerScaleMod;
import com.example.power_scale_mod.attachment.ModAttachments;
import com.example.power_scale_mod.client.ClientScaleData;
import com.example.power_scale_mod.event.PowerScaleEvents;
import com.example.power_scale_mod.network.payload.ChangeScalePayload;
import com.example.power_scale_mod.network.payload.SyncAttributeListPayload;
import com.example.power_scale_mod.network.payload.SyncScalePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PowerScaleMod.MODID)
public class ModPayloads {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(
                ChangeScalePayload.TYPE,
                ChangeScalePayload.STREAM_CODEC,
                ModPayloads::handleChangeScale);

        registrar.playToClient(
                SyncScalePayload.TYPE,
                SyncScalePayload.STREAM_CODEC,
                ModPayloads::handleSyncScale);

        registrar.playBidirectional(
                SyncAttributeListPayload.TYPE,
                SyncAttributeListPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModPayloads::handleSyncAttributeList,
                        ModPayloads::handleUpdateAttributeList
                ));
    }

    private static void handleChangeScale(ChangeScalePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                PowerScaleEvents.changeScale(serverPlayer, payload.direction());
            }
        });
    }

    private static void handleSyncScale(SyncScalePayload payload, IPayloadContext context) {
        ClientScaleData.setScaleLevel(payload.scaleLevel());
    }

    private static void handleUpdateAttributeList(SyncAttributeListPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                PowerScaleEvents.clearPowerScale(serverPlayer);
                serverPlayer.setData(ModAttachments.ATTRIBUTE_IDS.get(), payload.attributeIds());
                int level = serverPlayer.getData(ModAttachments.SCALE_LEVEL.get());
                PowerScaleEvents.applyPowerScale(serverPlayer, level);
                PacketDistributor.sendToPlayer(serverPlayer, new SyncAttributeListPayload(payload.attributeIds()));
            }
        });
    }

    private static void handleSyncAttributeList(SyncAttributeListPayload payload, IPayloadContext context) {
        ClientScaleData.setTrackedAttributeIds(payload.attributeIds());
    }
}
