package dev.hyperlynx.pulsetech.feature.debugger.infotype;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DebuggerPosInfo(BlockPos pos) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, DebuggerPosInfo> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DebuggerPosInfo::pos,
            DebuggerPosInfo::new
    );

    public static final Type<DebuggerPosInfo> TYPE = new Type<>(Pulsetech.location("debugger_pos_info"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
