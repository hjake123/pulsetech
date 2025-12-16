package dev.hyperlynx.pulsetech.feature.screen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.util.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public record ScreenData(BlockPos pos, Color bg_color, List<Pixel> fg) implements CustomPacketPayload {
    public static final Codec<ScreenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ScreenData::pos),
            Color.CODEC.fieldOf("bg_color").forGetter(ScreenData::bg_color),
            Pixel.CODEC.listOf().fieldOf("fg").forGetter(ScreenData::fg)
    ).apply(instance, ScreenData::new));

    public static final StreamCodec<ByteBuf, ScreenData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static final Type<ScreenData> TYPE = new Type<>(Pulsetech.location("screen_data"));

    public static ScreenData blank(BlockPos pos) {
        return new ScreenData(pos, Color.black(), new ArrayList<>());
    }

    public ScreenData withBackgroundColor(Color color) {
        return new ScreenData(pos(), color, fg());
    }

    public ScreenData withForegroundPixels(Color color, List<Tuple<Integer, Integer>> coords) {
        List<Pixel> modified_fg = new ArrayList<>(fg);
        for(var xy : coords) {
            modified_fg.add(new Pixel(color, xy.getA(), xy.getB()));
        }
        return new ScreenData(pos(), bg_color(), modified_fg);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record Pixel(Color color, int x, int y) {
        public static final Codec<Pixel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Color.CODEC.fieldOf("color").forGetter(Pixel::color),
                Codec.INT.fieldOf("x").forGetter(Pixel::x),
                Codec.INT.fieldOf("y").forGetter(Pixel::y)
        ).apply(instance, Pixel::new));
    }
}
