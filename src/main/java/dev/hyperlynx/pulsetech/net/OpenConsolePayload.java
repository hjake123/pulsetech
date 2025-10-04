package dev.hyperlynx.pulsetech.net;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.PulsetechClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// S -> C payload that opens the Console screen
public record OpenConsolePayload(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenConsolePayload> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("open_console"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenConsolePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenConsolePayload::pos,
            OpenConsolePayload::new
    );

    public void handler(IPayloadContext context) {
        PulsetechClient.openConsoleScreen(pos);
    }
}
