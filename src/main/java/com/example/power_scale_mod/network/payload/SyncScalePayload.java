package com.example.power_scale_mod.network.payload;

import com.example.power_scale_mod.PowerScaleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncScalePayload(int scaleLevel) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncScalePayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(PowerScaleMod.MODID, "sync_scale"));

    public static final StreamCodec<ByteBuf, SyncScalePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SyncScalePayload::scaleLevel,
                    SyncScalePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
