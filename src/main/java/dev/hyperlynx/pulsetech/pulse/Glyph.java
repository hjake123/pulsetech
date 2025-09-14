package dev.hyperlynx.pulsetech.pulse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/// A pattern used to identify a particular {@link Sequence} in a {@link Protocol}.
/// Currently, just a wrapper for a string, but it will be more!
public record Glyph(String id) {
    public static final Codec<Glyph> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(Glyph::id)
            ).apply(instance, Glyph::new)
    );
}
