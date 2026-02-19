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
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C <-> S packet that handles copying the Console's contents from the user's clipboard.
/// When Copy is pressed, the client sends this to the server (with an empty string), and the Server sends Command.COPY
/// and the contents to be copied. The client then actually copies the contents to the clipboard.
public record ConsoleClipboardCopyPayload(BlockPos pos, String contents) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConsoleClipboardCopyPayload> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("console_clipboard_copy"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ConsoleClipboardCopyPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConsoleClipboardCopyPayload::pos,
            ByteBufCodecs.STRING_UTF8, ConsoleClipboardCopyPayload::contents,
            ConsoleClipboardCopyPayload::new
    );

    public void serverHandler(IPayloadContext context) {
        if(!(context.player().level().getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            Pulsetech.LOGGER.error("Received copy request nonexistent console at {}", pos);
            return;
        }
        if(!context.player().level().isLoaded(pos)) {
            return;
        }
        PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new ConsoleClipboardCopyPayload(pos, console.getEncodedMacroData((ServerPlayer) context.player())));
    }

    public void clientHandler(IPayloadContext context) {
        ClientWrapper.copyToClipboard(contents);
    }

}
