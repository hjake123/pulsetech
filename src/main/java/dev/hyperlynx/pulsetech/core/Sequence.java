package dev.hyperlynx.pulsetech.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/// Contains a single pulse sequence -- a sequence of on and off that is used by various features.
/// The sequence is stored as a {@link BitSet}.
public class Sequence implements Comparable<Sequence> {
    private final BitSet bits;
    private int write_cursor = 0;

    public static final Codec<Sequence> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(Codec.BOOL).fieldOf("bools").forGetter(Sequence::getAsBooleans)
            ).apply(instance, Sequence::new)
    );

    public static final StreamCodec<ByteBuf, Sequence> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(bits.length());
        for(int i = 0; i < length(); i++) {
            builder.append(bits.get(i) ? "1" : "0");
        }
        return builder.toString();
    }

    public void appendAll(Sequence sequence) {
        for(int i = 0; i < sequence.length(); i++) {
            append(sequence.get(i));
        }
    }

    public static Sequence truncatedFromInt(int n) {
        Sequence sequence = new Sequence();
        while(n > 0) {
            boolean b = n % 2 == 1;
            n = n >> 1;
            sequence.append(b);
        }
        return sequence;
    }

    /// Returns a little endian Sequence containing the bits of the provided byte
    public static Sequence fromByte(byte n) {
        Sequence sequence = truncatedFromInt(Math.abs(n));
        while(sequence.length() < 7) {
            sequence.append(false);
        }
        sequence.append(n < 0);
        return sequence;
    }

    /// Returns the short value of this Sequence as a little endian signed byte.
    /// Affects the read cursor.
    public byte toByte() {
        byte n = 0;
        boolean negative = get(7);
        for(int i = length() - 2; i >= 0; i--) {
            boolean b = get(i);
            n = (byte) (n << 1 | (b ? 1 : 0));
        }
        if(negative) {
            n *= -1;
        }
        return n;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public void set(int index, boolean b) {
        bits.set(index, b);
    }

    public void removeLast() {
        write_cursor--;
        bits.clear(write_cursor);
    }

    @Override
    public int compareTo(Sequence other) {
        if(other.length() != length()) {
            return (other.length() - length());
        }
        for(int i = 0; i < length(); i++) {
            boolean my_bit = get(i);
            boolean other_bit = other.get(i);
            if(my_bit != other_bit) {
                if(my_bit) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return 0;
    }

    /// Completely reverse this Sequence. Expensive!
    public void reverse() {
        Sequence reversed = new Sequence();
        for(int i = length() - 1; i >= 0; i--) {
            reversed.append(get(i));
        }
        clear();
        appendAll(reversed);
    }

    /// Remove the first item in the sequence by copying. Expensive!
    /// TODO Consider replacing with some kind of wrap-around write buffer.
    public void removeFirst() {
        Sequence truncated = new Sequence();
        for (int i = 1; i < length(); i++) {
            truncated.append(get(i));
        }
        clear();
        appendAll(truncated);
    }
}
