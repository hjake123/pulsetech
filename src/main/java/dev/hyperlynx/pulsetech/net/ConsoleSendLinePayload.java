package dev.hyperlynx.pulsetech.net;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.PulsetechClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C -> S payload that reports the previous line of text entered by the player into the console
/// and attempts to save it to a ConsoleBlockEntity at pos
public record ConsoleSendLinePayload(BlockPos pos, String line) implements CustomPacketPayload {

    public static final Type<ConsoleSendLinePayload> TYPE = new Type<>(Pulsetech.location("console_line"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsoleSendLinePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsoleSendLinePayload::pos,
            ByteBufCodecs.STRING_UTF8, ConsoleSendLinePayload::line,
            ConsoleSendLinePayload::new
    );

    public void handler(IPayloadContext context) {
        Pulsetech.LOGGER.debug("Received command line {} for console at pos {}", line, pos);
    }
}
