package dev.hyperlynx.pulsetech.net;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.SequenceBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C -> S payload that saves the value from the Sequence Select screen
public record SequenceSelectPayload(BlockPos pos, Sequence sequence) implements CustomPacketPayload {

    public static final Type<SequenceSelectPayload> TYPE = new Type<>(Pulsetech.location("save_sequence"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, SequenceSelectPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SequenceSelectPayload::pos,
            Sequence.STREAM_CODEC, SequenceSelectPayload::sequence,
            SequenceSelectPayload::new
    );

    public void handler(IPayloadContext context) {
        if(context.player().level().getBlockEntity(pos) instanceof SequenceBlockEntity sbe) {
            sbe.setPattern(sequence);
        }
    }
}

