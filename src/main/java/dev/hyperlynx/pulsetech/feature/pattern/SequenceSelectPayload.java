package dev.hyperlynx.pulsetech.feature.pattern;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternBlockEntity;
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
        if(context.player().level().isLoaded(pos) && context.player().level().getBlockEntity(pos) instanceof PatternBlockEntity sbe) {
            sbe.setPattern(sequence);
        }
    }
}

