package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DebuggerSequenceInfo(Sequence sequence) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, DebuggerSequenceInfo> STREAM_CODEC = StreamCodec.composite(
            Sequence.STREAM_CODEC, DebuggerSequenceInfo::sequence,
            DebuggerSequenceInfo::new
    );

    public static final CustomPacketPayload.Type<DebuggerSequenceInfo> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("debugger_sequence_info"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
