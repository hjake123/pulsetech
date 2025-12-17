package dev.hyperlynx.pulsetech.feature.screen;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ScreenUpdatePayload(ScreenData data, BlockPos pos)  implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ScreenUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ScreenData.STREAM_CODEC, ScreenUpdatePayload::data,
            BlockPos.STREAM_CODEC, ScreenUpdatePayload::pos,
            ScreenUpdatePayload::new
    );

    public static final CustomPacketPayload.Type<ScreenUpdatePayload> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("screen_update"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
