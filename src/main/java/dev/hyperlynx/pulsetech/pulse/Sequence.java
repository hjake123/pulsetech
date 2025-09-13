package dev.hyperlynx.pulsetech.pulse;

import java.util.BitSet;

/// Contains a single pulse sequence -- a sequence of on and off that is used by various features.
/// The sequence is stored as a {@link BitSet}, and the Sequence also contains cursors used to traverse itself.
public class Sequence {
    private final BitSet bits = new BitSet();
    private int read_cursor = 0;
    private int write_cursor = 0;

    public boolean get(int index) {
        return bits.get(index);
    }

    public boolean next() {
        return bits.get(read_cursor++);
    }

    public void resetCursor() {
        read_cursor = 0;
    }

    public void append(boolean bit) {
        bits.set(write_cursor, bit);
    }

    public void clear() {
        bits.clear();
        write_cursor = 0;
    }

    public int size() {
        return bits.size();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Sequence other_seq)) {
            return false;
        }
        return bits.equals(other_seq.bits);
    }
}
