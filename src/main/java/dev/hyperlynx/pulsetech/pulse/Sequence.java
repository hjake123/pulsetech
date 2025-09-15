package dev.hyperlynx.pulsetech.pulse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/// Contains a single pulse sequence -- a sequence of on and off that is used by various features.
/// The sequence is stored as a {@link BitSet}.
public class Sequence {
    private final BitSet bits;
    private int write_cursor = 0;

    public static final Codec<Sequence> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(Codec.BOOL).fieldOf("bools").forGetter(Sequence::getAsBooleans)
            ).apply(instance, Sequence::new)
    );

    private List<Boolean> getAsBooleans() {
        List<Boolean> bools = new ArrayList<>();
        for(int i = 0; i < length(); i++) {
            bools.add(bits.get(i));
        }
        return bools;
    }

    public Sequence() {
        this(new BitSet());
    }

    public Sequence(boolean... bit_values) {
        this(new BitSet());
        for(boolean bit : bit_values) {
            append(bit);
        }
    }

    public Sequence(BitSet bits) {
        this.bits = (BitSet) bits.clone();
    }

    public Sequence(Sequence other) {
        this.bits = (BitSet) other.bits.clone();
        this.write_cursor = other.write_cursor;
    }

    public Sequence(List<Boolean> booleans) {
        this.bits = new BitSet();
        for(boolean bool : booleans) {
            append(bool);
        }
    }


    private BitSet bitSet() {
        return bits;
    }

    public boolean get(int index) {
        return bits.get(index);
    }

    public void append(boolean bit) {
        bits.set(write_cursor, bit);
        write_cursor++;
    }

    public void clear() {
        bits.clear();
        write_cursor = 0;
    }

    public int length() {
        return write_cursor;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Sequence other_seq)) {
            return false;
        }
        return bits.equals(other_seq.bits);
    }

    @Override
    public int hashCode() {
        return bits.hashCode();
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
        for(int i = length() - 1; i >= 0; i--) {
            boolean b = get(i);
            n = n << 1 | (b ? 1 : 0);
        }
        return n;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(bits.length());
        for(int i = 0; i < length(); i++) {
            builder.append(bits.get(i) ? "1" : "0");
        }
        return builder.toString();
    }
}
