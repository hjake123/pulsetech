package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.pulse.protocol.Protocol;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolDataMap;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ProtocolGenerator extends DataMapProvider {
    protected ProtocolGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ProtocolDataMap.PROTOCOL_MAP)
                // TODO Example debug protocol -- remove this
                .add(ModBlockEntityTypes.NUMBER_MONITOR,
                        ProtocolBuilder.builder(4)
                                .add("A", new Sequence(true, false, true, false))
                                .add("ON", new Sequence(true, true, true, true), ProtocolCommands.ON)
                                .add("OFF", ProtocolCommands.OFF)
                                .build(), false
                );
    }


    private static class ProtocolBuilder {
        private final int sequence_length;
        private final Map<String, Sequence> terms = new HashMap<>();
        private @Nullable Map<String, ProtocolCommand<?>> commands;

        private ProtocolBuilder(int sequenceLength) {
            sequence_length = sequenceLength;
        }

        public static ProtocolBuilder builder(int sequence_length) {
           return new ProtocolBuilder(sequence_length);
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
            int i = 0;
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

        public Protocol build() {
            return new Protocol(sequence_length, terms, commands);
        }
    }
}
