package dev.hyperlynx.pulsetech.pulse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/// Contains a set of associations between {@link Sequence}s and {@link Glyph}s.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    public static final Glyph ACK = new Glyph("ACK");
    public static final Glyph ERR = new Glyph("ERR");

    private final BiMap<Glyph, Sequence> sequence_map;
    private final int sequence_length;

    public static final Codec<Protocol> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sequence_length").forGetter(Protocol::sequenceLength),
                    Codec.unboundedMap(Glyph.CODEC, Sequence.CODEC).fieldOf("sequence_map").forGetter(Protocol::getSequenceMap)
            ).apply(instance, Protocol::new)
    );

    public Protocol(int sequence_length) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create();
    }

    public Protocol(int sequence_length, Map<Glyph, Sequence> existing) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create(existing);
    }

    private Map<Glyph, Sequence> getSequenceMap() {
        return sequence_map;
    }

    public void define(Glyph glyph, Sequence sequence) {
        if(sequence.length() != sequence_length) {
            Pulsetech.LOGGER.error("Sequence of invalid length {} was defined for protocol (length is {})", sequence.length(), sequence_length);
        }
        sequence_map.forcePut(glyph, sequence);

    }

    public @Nullable Sequence sequenceFor(Glyph glyph) {
        Sequence seq = sequence_map.getOrDefault(glyph, null);
        if(seq == null) {
            return null;
        }
        return new Sequence(seq);
    }

    public @Nullable Glyph glyphFor(Sequence sequence) {
        return sequence_map.inverse().getOrDefault(sequence, null);
    }

    public int sequenceLength() {
        return sequence_length;
    }

    public Glyph randomGlyph(RandomSource random) {
        return sequence_map.keySet().stream().toList().get(random.nextInt(0, sequence_map.size()));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Protocol other) {
            return other.sequence_map.equals(sequence_map);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence_map, sequence_length);
    }
}
