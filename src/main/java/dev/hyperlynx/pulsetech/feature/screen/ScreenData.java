package dev.hyperlynx.pulsetech.feature.screen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.util.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ScreenData(BlockPos pos, Color bg_color) implements CustomPacketPayload {
    public static final Codec<ScreenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ScreenData::pos),
            Color.CODEC.fieldOf("bg_color").forGetter(ScreenData::bg_color)
    ).apply(instance, ScreenData::new));

    public static final StreamCodec<ByteBuf, ScreenData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static final Type<ScreenData> TYPE = new Type<>(Pulsetech.location("screen_data"));

    public ScreenData withBackgroundColor(Color color) {
        return new ScreenData(pos(), color);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
