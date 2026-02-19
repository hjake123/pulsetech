package dev.hyperlynx.pulsetech.feature.console;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C -> S packet that handles pasting the Console's contents from the user's clipboard.
/// When Paste is pressed, the client sends this to the Server along with the contents to paste.
public record ConsoleClipboardPastePayload(BlockPos pos, String contents) implements CustomPacketPayload {
    public static final Type<ConsoleClipboardPastePayload> TYPE = new Type<>(Pulsetech.location("console_clipboard_paste"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsoleClipboardPastePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsoleClipboardPastePayload::pos,
            ByteBufCodecs.STRING_UTF8, ConsoleClipboardPastePayload::contents,
            ConsoleClipboardPastePayload::new
    );

    public void serverHandler(IPayloadContext context) {
        if(!(context.player().level().getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            Pulsetech.LOGGER.error("Received paste data for nonexistent console at {}", pos);
            return;
        }
        if(!context.player().level().isLoaded(pos)) {
            return;
        }
        console.ingestClipboardData(contents, (ServerPlayer) context.player());
    }

}
