package dev.hyperlynx.pulsetech.pulse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/// Contains a set of associations between {@link Sequence}s and String keys.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    public static final String ACK = "ACK";
    public static final String ERR = "ERR";
    public static final String NUM = "#";

    private final BiMap<String, Sequence> sequence_map;
    private final int sequence_length;
    private final List<String> key_list = new ArrayList<>();

    public static final Codec<Protocol> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sequence_length").forGetter(Protocol::sequenceLength),
                    Codec.unboundedMap(Codec.STRING, Sequence.CODEC).fieldOf("sequence_map").forGetter(Protocol::getSequenceMap)
            ).apply(instance, Protocol::new)
    );

    public Protocol(int sequence_length) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create();
    }

    public Protocol(int sequence_length, Map<String, Sequence> existing) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create(existing);
        key_list.addAll(sequence_map.keySet());
    }

    private Map<String, Sequence> getSequenceMap() {
        return sequence_map;
    }

    public void define(String key, Sequence sequence) {
        if(sequence.length() != sequence_length) {
            Pulsetech.LOGGER.error("Sequence of invalid length {} was defined for protocol (length is {})", sequence.length(), sequence_length);
        }
        sequence_map.forcePut(key, sequence);
        key_list.add(key);
    }

    public @Nullable Sequence sequenceFor(String key) {
        Sequence seq = sequence_map.getOrDefault(key, null);
        if(seq == null) {
            return null;
        }
        return new Sequence(seq);
    }

    public @Nullable String keyFor(Sequence sequence) {
        return sequence_map.inverse().getOrDefault(sequence, null);
    }

    public int sequenceLength() {
        return sequence_length;
    }

    public String nextKey(String current_key) {
        for(int i = 0; i < key_list.size(); i++) {
            if(key_list.get(i).equals(current_key)) {
                if(i+1 >= key_list.size()) {
                    return key_list.getFirst();
                }
                return key_list.get(i+1);
            }
        }
        return key_list.getFirst();
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

    public boolean hasKey(String token) {
        return sequence_map.containsKey(token);
    }

    public List<String> keys() {
        return key_list;
    }

    /// Returns a little endian Sequence containing the bits of the provided short
    /// Prepends the NUM code first to signal that the following 16 bits will be a number
    public Sequence fromShort(short n) {
        if(sequenceFor(NUM) == null) {
            Pulsetech.LOGGER.error("No NUM sequence defined for this protocol. Bad!");
            return new Sequence();
        }
        Sequence sequence = new Sequence(Objects.requireNonNull(sequenceFor(NUM)));
        while(n > 0) {
            boolean b = n % 2 == 1;
            n = (short) (n >> 1);
            sequence.append(b);
        }
        while(sequence.length() < numberSequenceLength()) {
            sequence.append(false);
        }
        return sequence;
    }

    /// Returns the short value of this Sequence as a little endian number.
    /// Affects the read cursor.
    public short toShort(Sequence sequence) {
        short n = 0;
        for(int i = sequence.length() - 1; i >= sequenceLength(); i--) {
            // Repeat until we reach the NUM sequence
            boolean b = sequence.get(i);
            n = (short) (n << 1 | (b ? 1 : 0));
        }
        return n;
    }

    public int numberSequenceLength() {
        return 16 + sequenceLength();
    }
}
