package dev.hyperlynx.pulsetech.pulse.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Sequence;

import javax.annotation.Nullable;
import java.util.*;

/// Contains a set of associations between {@link Sequence}s and String keys.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    private final BiMap<String, Sequence> sequence_map;
    private final int sequence_length;
    private final List<String> key_list = new ArrayList<>();
    private final Map<String, ProtocolCommand<?>> commands = new HashMap<>();

    public static final Codec<Protocol> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sequence_length").forGetter(Protocol::sequenceLength),
                    Codec.unboundedMap(Codec.STRING, Sequence.CODEC).fieldOf("sequence_map").forGetter(Protocol::getSequenceMap),
                    Codec.unboundedMap(Codec.STRING, ProtocolCommands.REGISTRY.byNameCodec()).optionalFieldOf("commands").forGetter(Protocol::getCommands)
            ).apply(instance, Protocol::new)
    );

    public Protocol(int sequence_length) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Protocol(int sequence_length, Map<String, Sequence> existing, Optional<Map<String, ProtocolCommand<?>>> possible_commands) {
        this.sequence_length = sequence_length;
        sequence_map = HashBiMap.create(existing);
        key_list.addAll(sequence_map.keySet());
        possible_commands.ifPresent(commands::putAll);
    }

    public Protocol(int sequence_length, Map<String, Sequence> existing, @Nullable Map<String, ProtocolCommand<?>> possible_commands) {
        this(sequence_length, existing, possible_commands == null ? Optional.empty() : Optional.of(possible_commands));
    }

    private Map<String, Sequence> getSequenceMap() {
        return sequence_map;
    }

    private Optional<Map<String, ProtocolCommand<?>>> getCommands() {
        return Optional.of(commands);
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
}
