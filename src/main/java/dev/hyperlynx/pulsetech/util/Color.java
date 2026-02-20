package dev.hyperlynx.pulsetech.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Color {
    public int red;
    public int green;
    public int blue;
    public int hex;

    public static final Codec<Color> RGB_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("red").forGetter(Color::red),
            Codec.INT.fieldOf("green").forGetter(Color::green),
            Codec.INT.fieldOf("blue").forGetter(Color::blue))
            .apply(instance, Color::new)
    );

    public static final Codec<Color> HEX_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(Color::hex))
            .apply(instance, Color::new)
    );

    public static final Codec<Color> CODEC = Codec.either(RGB_CODEC, HEX_CODEC).xmap(
            either -> either.left().orElseGet(() -> either.right().orElseThrow()),
            Either::left
    );

    public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Color::hex,
            Color::new
    );

    public Color(int color) {
        hex = color;
        red = (((color >> 16) & 0xFF));
        green = (((color >> 8) & 0xFF));
        blue = ((color & 0xFF));
    }

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        updateHexFromRGB();
    }

    public static Color black() {
        return new Color(0);
    }

    public static Color white() {
        return new Color(0xFFFFFF);
    }

    public int hex() { return hex; }
    private int red() { return red; }
    private int green() { return green; }
    private int blue() { return blue; }

    @Override
    public boolean equals(Object obj) {
        boolean obj_equals = super.equals(obj);
        if(obj instanceof Color){
            return red == ((Color) obj).red && green == ((Color) obj).green && blue == ((Color) obj).blue;
        }
        return obj_equals;
    }

    public void updateHexFromRGB() {
        hex = (red << 16) & 0x00FF0000 | (green << 8) & 0x0000FF00 | (blue & 0x00000FF);
    }

    @Override
    public String toString() {
        return String.format("%02x", hex());
    }
}
