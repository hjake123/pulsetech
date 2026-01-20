package dev.hyperlynx.pulsetech.feature.console;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C <-> S payload that reports all lines already sent to the Console in the past
/// Unlike {@link ConsoleLinePayload}, this does not provoke execution of the command when sent to the server
/// Instead, the commands are just stored and returned to the player next time they open the screen
/// Also used to clear the screen from the server
public record ConsolePriorLinesPayload(BlockPos pos, String lines) implements CustomPacketPayload {

    public static final Type<ConsolePriorLinesPayload> TYPE = new Type<>(Pulsetech.location("past_console_lines"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsolePriorLinesPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsolePriorLinesPayload::pos,
            ByteBufCodecs.STRING_UTF8, ConsolePriorLinesPayload::lines,
            ConsolePriorLinesPayload::new
    );

    public void clientHandler(IPayloadContext context) {
        ClientWrapper.setPriorConsoleLines(pos, lines);
    }

    public void serverHandler(IPayloadContext context) {
        if(!(context.player().level().getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            Pulsetech.LOGGER.error("Received command for nonexistent console at {}", pos);
            return;
        }
        if(!context.player().level().isLoaded(pos)) {
            return;
        }
        console.savePriorLines(lines);
    }
}
