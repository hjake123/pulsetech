package dev.hyperlynx.pulsetech.core.program;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// A complete set of Macro data to be saved into the Data Cell and moved between Consoles or Program Emitters.
public record Macros(Map<String, List<String>> macros, HashSet<String> hidden_macros) {
    public static final Codec<Macros> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).fieldOf("macros").forGetter(Macros::macros),
                    Codec.list(Codec.STRING).xmap(HashSet::new, ArrayList::new).optionalFieldOf("hidden_macros").xmap(optional -> optional.orElse(new HashSet<>()), Optional::of).forGetter(Macros::hidden_macros)
            ).apply(instance, Macros::new));

    public static final StreamCodec<ByteBuf, Macros> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Macros(Map<String, List<String>> other, var hidden) && other.equals(macros()) && hidden.equals(hidden_macros());
    }

    public Macros mergeWith(@Nullable Macros other) {
        if(other == null) {
            return this;
        }
        HashMap<String, List<String>> merged_macros = new HashMap<>(macros);
        for(String key : other.macros.keySet()) {
            merged_macros.put(key, other.macros.get(key));
        }
        HashSet<String> merged_hidden_macros = new HashSet<>(hidden_macros);
        merged_hidden_macros.addAll(other.hidden_macros());
        return new Macros(merged_macros, merged_hidden_macros);
    }
}
