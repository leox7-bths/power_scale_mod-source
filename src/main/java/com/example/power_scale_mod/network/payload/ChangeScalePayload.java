package com.example.power_scale_mod.network.payload;

import com.example.power_scale_mod.PowerScaleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeScalePayload(int direction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeScalePayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(PowerScaleMod.MODID, "change_scale"));

    public static final StreamCodec<ByteBuf, ChangeScalePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ChangeScalePayload::direction,
                    ChangeScalePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
