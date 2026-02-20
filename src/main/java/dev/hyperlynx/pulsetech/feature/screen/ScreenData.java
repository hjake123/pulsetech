package dev.hyperlynx.pulsetech.feature.screen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.util.Color;
import dev.hyperlynx.pulsetech.util.MapListPairConverter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record ScreenData(Color bg_color, Map<Integer, Color> fg, boolean fg_visible) {
    private static final MapListPairConverter<Integer, Color> converter = new MapListPairConverter<>();

    public static final Codec<ScreenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("bg_color").forGetter(ScreenData::bg_color),
            Codec.pair(
                    Codec.INT.fieldOf("number").codec(),
                    Color.CODEC.fieldOf("color").codec()
            ).listOf().xmap(
                    converter::toMap,
                    converter::fromMap
            ).fieldOf("fg").forGetter(ScreenData::fg),
            Codec.BOOL.fieldOf("fg_visible").forGetter(ScreenData::fg_visible)
    ).apply(instance, ScreenData::new));

    public static final StreamCodec<ByteBuf, ScreenData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static ScreenData blank() {
        return new ScreenData(Color.black(), new HashMap<>(), true);
    }

    public ScreenData withBackgroundColor(Color color) {
        return new ScreenData(color, fg(), fg_visible());
    }

    public void setPixel(Color color, int x, int y) {
        if(x < 0 || y < 0 || x > 13 || y > 13) {
            return;
        }
        fg.put(y * 14 + x, color);
    }

    public ScreenData toggleForegroundVisible() {
        return new ScreenData(bg_color(), fg(), !fg_visible());
    }

    public void clearForeground() {
        fg.clear();
    }
}
