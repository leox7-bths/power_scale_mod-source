package com.example.power_scale_mod.network.payload;

import com.example.power_scale_mod.PowerScaleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record SyncAttributeListPayload(List<String> attributeIds) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncAttributeListPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(PowerScaleMod.MODID, "sync_attribute_list"));

    public static final StreamCodec<ByteBuf, SyncAttributeListPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncAttributeListPayload decode(ByteBuf buf) {
            int size = ByteBufCodecs.VAR_INT.decode(buf);
            List<String> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(ByteBufCodecs.STRING_UTF8.decode(buf));
            }
            return new SyncAttributeListPayload(ids);
        }

        @Override
        public void encode(ByteBuf buf, SyncAttributeListPayload payload) {
            ByteBufCodecs.VAR_INT.encode(buf, payload.attributeIds().size());
            for (String id : payload.attributeIds()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, id);
            }
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
