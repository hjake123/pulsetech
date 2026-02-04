package dev.hyperlynx.pulsetech.feature.debugger.infotype;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DebuggerByteInfo(byte number) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, DebuggerByteInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, DebuggerByteInfo::number,
            DebuggerByteInfo::new
    );

    public static final CustomPacketPayload.Type<DebuggerByteInfo> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("debugger_byte_info"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
