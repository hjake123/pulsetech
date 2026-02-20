package dev.hyperlynx.pulsetech.feature.console;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// C <- S payload that updates the client with new macros for its completion system.
/// Sent both upon the Console screen opening and when a new macro is defined.
public record ConsoleCompletionDataPayload(BlockPos pos, List<String> macros) implements CustomPacketPayload {

    public static final Type<ConsoleCompletionDataPayload> TYPE = new Type<>(Pulsetech.location("console_macros"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsoleCompletionDataPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsoleCompletionDataPayload::pos,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ConsoleCompletionDataPayload::macros,
            ConsoleCompletionDataPayload::new
    );

    public void clientHandler(IPayloadContext context) {
        ClientWrapper.acceptConsoleCompletionData(pos, macros);
    }
}
