package dev.hyperlynx.pulsetech.pulse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.hyperlynx.pulsetech.Pulsetech;

import javax.annotation.Nullable;

/// Contains a set of associations between {@link Sequence}s and {@link Glyph}s.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    public static final Glyph ACK = new Glyph("ACK");
    public static final Glyph ERR = new Glyph("ERR");

    private final BiMap<Glyph, Sequence> sequence_map = HashBiMap.create();
    private int sequence_length;

    public Protocol(int sequence_length) {
        this.sequence_length = sequence_length;
    }

    public void define(Glyph glyph, Sequence sequence) {
        if(sequence.length() != sequence_length) {
            Pulsetech.LOGGER.error("Sequence of invalid length {} was defined for protocol (length is {})", sequence.length(), sequence_length);
        }
        sequence_map.forcePut(glyph, sequence);

    }

    public @Nullable Sequence sequenceFor(Glyph glyph) {
        return sequence_map.getOrDefault(glyph, null);
    }

    public @Nullable Glyph glyphFor(Sequence sequence) {
        return sequence_map.inverse().getOrDefault(sequence, null);
    }

    public int sequenceLength() {
        return sequence_length;
    }
}
