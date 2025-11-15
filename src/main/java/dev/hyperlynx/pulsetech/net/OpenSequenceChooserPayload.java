package dev.hyperlynx.pulsetech.net;

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
public record OpenSequenceChooserPayload(BlockPos pos) implements CustomPacketPayload {

    public static final Type<OpenSequenceChooserPayload> TYPE = new Type<>(Pulsetech.location("open_sequence"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenSequenceChooserPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenSequenceChooserPayload::pos,
            OpenSequenceChooserPayload::new
    );

    public void handler(IPayloadContext context) {
        ClientWrapper.openSequenceScreen(pos);
    }
}
