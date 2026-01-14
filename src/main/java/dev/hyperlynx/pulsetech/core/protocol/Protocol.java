package dev.hyperlynx.pulsetech.core.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.processor.MacroProtocolCommand;
import dev.hyperlynx.pulsetech.util.MapListPairConverter;

import javax.annotation.Nullable;
import java.util.*;

/// Contains a set of associations between {@link Sequence}s and {@link ProtocolCommand}s.
/// These associations allow the Sequences to be used by players to configure various blocks.
public class Protocol {
    private final int sequence_length;
    private final BiMap<ProtocolCommand, Sequence> commands;

    private static final MapListPairConverter<ProtocolCommand, Sequence> converter = new MapListPairConverter<>();

    /// Cheat a little to allow Macro Commands to exist
    public static final Codec<ProtocolCommand> COMMAND_CODEC = Codec.either(
            ProtocolCommands.REGISTRY.byNameCodec(),
            MacroProtocolCommand.CODEC
    ).xmap(either -> {
        if(either.left().isPresent()) {
            return either.left().orElseThrow();
        }
        return either.right().orElseThrow();
    }, command -> {
        if(command instanceof MacroProtocolCommand macro_command) {
            return Either.right(macro_command);
        }
        return Either.left(command);
    });

    public static final Codec<Protocol> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sequence_length").forGetter(Protocol::sequenceLength),
                    Codec.pair(
                            COMMAND_CODEC.fieldOf("command").codec(),
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

    public Protocol(int sequence_length, Map<ProtocolCommand, Sequence> existing) {
        this.sequence_length = sequence_length;
        commands = HashBiMap.create(existing);
    }

    public Map<ProtocolCommand, Sequence> getCommands() {
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

    public @Nullable ProtocolCommand getCommand(Sequence buffer) {
        return commands.inverse().get(buffer);
    }
}
