package dev.hyperlynx.pulsetech.feature.number;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.number.block.NumberEmitterBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// C -> S payload that saves the value from the Number Select screen
public record NumberSelectPayload(BlockPos pos, byte number) implements CustomPacketPayload {
    public static final Type<NumberSelectPayload> TYPE = new Type<>(Pulsetech.location("save_number"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, NumberSelectPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, NumberSelectPayload::pos,
            ByteBufCodecs.BYTE, NumberSelectPayload::number,
            NumberSelectPayload::new
    );

    public void handler(IPayloadContext context) {
        if(context.player().level().isLoaded(pos) && context.player().level().getBlockEntity(pos) instanceof NumberEmitterBlockEntity nebe) {
            nebe.setNumber(number);
        }
    }
}
