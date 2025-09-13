package dev.hyperlynx.pulsetech.pulse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

/// Contains a set of associations between {@link Sequence}s and {@link Glyph}s.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    public static final Glyph ACK = new Glyph("ACK");
    public static final Glyph ERR = new Glyph("ERR");

    private final BiMap<Glyph, Sequence> sequence_map = HashBiMap.create();

    public void set(Glyph glyph, Sequence sequence) {
        sequence_map.forcePut(glyph, sequence);
    }

    public @Nullable Sequence sequenceFor(Glyph glyph) {
        return sequence_map.getOrDefault(glyph, null);
    }

    public @Nullable Glyph glyphFor(Sequence sequence) {
        return sequence_map.inverse().getOrDefault(sequence, null);
    }
}
