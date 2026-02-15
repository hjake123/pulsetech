package dev.hyperlynx.pulsetech.core.program;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Macros(Map<String, List<String>> macros) {
    public static final Codec<Macros> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).fieldOf("macros").forGetter(Macros::macros)
            ).apply(instance, Macros::new));

    public static final StreamCodec<ByteBuf, Macros> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Macros(Map<String, List<String>> other) && other.equals(macros());
    }

    public Macros mergeWith(@Nullable Macros other) {
        if(other == null) {
            return this;
        }
        HashMap<String, List<String>> merged_macros = new HashMap<>(macros);
        for(String key : other.macros.keySet()) {
            merged_macros.put(key, other.macros.get(key));
        }
        return new Macros(merged_macros);
    }

    public void add(String noun, ArrayList<String> definition) {
        macros().put(noun, new ArrayList<>(definition));
    }
}
