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

/// S -> C payload that opens the Console screen
public record OpenConsolePayload(BlockPos pos, String prior_lines, String command_box_text) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenConsolePayload> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("open_console"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenConsolePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenConsolePayload::pos,
            ByteBufCodecs.STRING_UTF8, OpenConsolePayload::prior_lines,
            ByteBufCodecs.STRING_UTF8, OpenConsolePayload::command_box_text,
            OpenConsolePayload::new
    );

    public void handler(IPayloadContext ignored) {
        ClientWrapper.openConsoleScreen(pos, prior_lines, command_box_text);
    }
}
