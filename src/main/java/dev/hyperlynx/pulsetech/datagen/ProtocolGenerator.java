package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.protocol.Protocol;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.pulse.protocol.Protocols;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ProtocolGenerator {
    public static RegistrySetBuilder get() {
        return new RegistrySetBuilder().add(
                Protocols.KEY,
                bootstrap -> {
                    ProtocolBuilder.builder("debug", 4)
                            .add("A", new Sequence(true, false, true, false))
                            .add("ON", ProtocolCommands.ON)
                            .add("OFF", ProtocolCommands.OFF)
                            .build(bootstrap);
                }
        );
    }

    private static class ProtocolBuilder {
        private final String id;
        private final int sequence_length;
        private final Map<String, Sequence> terms = new HashMap<>();
        private @Nullable Map<String, ProtocolCommand<?>> commands;

        private ProtocolBuilder(String id, int sequenceLength) {
            this.id = id;
            sequence_length = sequenceLength;
        }

        public static ProtocolBuilder builder(String id, int sequence_length) {
           return new ProtocolBuilder(id, sequence_length);
        }

        /// Override to just add a symbol. Useless?
        ProtocolBuilder add(String key, Sequence sequence) {
            terms.put(key, sequence);
            return this;
        }

        /// Override to include a command.
        ProtocolBuilder add(String key, Sequence sequence, Supplier<ProtocolCommand<?>> command) {
            if(commands == null) {
                commands = new HashMap<>();
            }
            commands.put(key, command.get());
            return add(key, sequence);
        }

        /// Override to auto determine a sequence.
        ProtocolBuilder add(String key, Supplier<ProtocolCommand<?>> command) {
            int i = 1;
            while(i < Math.pow(2, sequence_length)) {
                Sequence s = Sequence.truncatedFromInt(i);
                while(s.length() < sequence_length) {
                    s.append(false);
                }
                if(!terms.containsValue(s)) {
                    return add(key, s, command);
                }
                i++;
            }
            throw new IllegalStateException("Too many commands registered for the provided sequence length " + sequence_length);
        }

        public void build(BootstrapContext<Protocol> context) {
            context.register(ResourceKey.create(Protocols.KEY, Pulsetech.location(id)),
                    new Protocol(sequence_length, terms, commands)
            );
        }
    }
}
