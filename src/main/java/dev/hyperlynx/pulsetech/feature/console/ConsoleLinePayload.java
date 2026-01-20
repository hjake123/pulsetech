package dev.hyperlynx.pulsetech.feature.console;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C <-> S payload that reports the previous lines of text entered by the player into the console
/// and attempts to save it to a ConsoleBlockEntity at pos.
/// Also used to send feedback to the client.
public record ConsoleLinePayload(BlockPos pos, String line) implements CustomPacketPayload {

    public static final Type<ConsoleLinePayload> TYPE = new Type<>(Pulsetech.location("console_line"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsoleLinePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsoleLinePayload::pos,
            ByteBufCodecs.STRING_UTF8, ConsoleLinePayload::line,
            ConsoleLinePayload::new
    );

    public void clientHandler(IPayloadContext context) {
        ClientWrapper.acceptConsoleLine(pos, line);
    }

    public void serverHandler(IPayloadContext context) {
        if(!(context.player().level().getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            Pulsetech.LOGGER.error("Received command {} for nonexistent console at {}", line, pos);
            return;
        }
        if(!context.player().level().isLoaded(pos)) {
            return;
        }
        console.processLine(line, (ServerPlayer) context.player());
    }
}
