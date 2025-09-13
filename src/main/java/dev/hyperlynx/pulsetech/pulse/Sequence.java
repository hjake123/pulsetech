package dev.hyperlynx.pulsetech.pulse;

import java.util.BitSet;

/// Contains a single pulse sequence -- a sequence of on and off that is used by various features.
/// The sequence is stored as a {@link BitSet}.
public class Sequence {
    private final BitSet bits;
    private int write_cursor = 0;

    public Sequence() {
        this(new BitSet());
    }

    public Sequence(BitSet bits) {
        this.bits = bits;
    }

    public boolean get(int index) {
        return bits.get(index);
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

    /// Returns a little endian Sequence containing the bits of the provided integer with all leading zeros truncated.
    public static Sequence fromInt(int n) {
        Sequence sequence = new Sequence();
        while(n > 0) {
            boolean b = n % 2 == 1;
            n = n >> 1;
            sequence.append(b);
        }
        return sequence;
    }

    /// Returns the integer value of this Sequence as a little endian number.
    /// Affects the read cursor.
    public int toInt() {
        int n = 0;
        for(int i = size() - 1; i >= 0; i--) {
            boolean b = get(i);
            n = n << 1 | (b ? 1 : 0);
        }
        return n;
    }
}
