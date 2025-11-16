package dev.hyperlynx.pulsetech.pulse.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.util.MapListPairConverter;

import javax.annotation.Nullable;
import java.util.*;

/// Contains a set of associations between {@link Sequence}s and {@link ProtocolCommand}s.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    private final int sequence_length;
    private final BiMap<ProtocolCommand<?>, Sequence> commands;

    private static final MapListPairConverter<ProtocolCommand<?>, Sequence> converter = new MapListPairConverter<>();

    public static final Codec<Protocol> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sequence_length").forGetter(Protocol::sequenceLength),
                    Codec.pair(
                            ProtocolCommands.REGISTRY.byNameCodec().fieldOf("command").codec(),
                            Sequence.CODEC.fieldOf("sequence").codec()
                    ).listOf().xmap(
                            converter::toMap,
                            converter::fromMap)
                    .fieldOf("commands").forGetter(Protocol::getCommands)
            ).apply(instance, Protocol::new)
    );

    public Protocol(int sequence_length) {
        this.sequence_length = sequence_length;
        commands = HashBiMap.create();
    }

    public Protocol(int sequence_length, Map<ProtocolCommand<?>, Sequence> existing) {
        this.sequence_length = sequence_length;
        commands = HashBiMap.create(existing);
    }

    private Map<ProtocolCommand<?>, Sequence> getCommands() {
        return commands;
    }

    public int sequenceLength() {
        return sequence_length;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Protocol other) {
            return other.commands.equals(commands);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands, sequence_length);
    }
}
