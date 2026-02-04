package dev.hyperlynx.pulsetech.feature.debugger.infotype;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DebuggerTextInfo(String text) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, DebuggerTextInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebuggerTextInfo::text,
            DebuggerTextInfo::new
    );

    public static final Type<DebuggerTextInfo> TYPE = new Type<>(Pulsetech.location("debugger_text_info"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
