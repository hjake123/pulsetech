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
        builder(ProtocolDataMap.TYPE)
                .add(ModBlockEntityTypes.CONTROLLER,
                        ProtocolBuilder.builder(4)
                                .add(ProtocolCommands.OFF)
                                .add(ProtocolCommands.ON)
                                .add(ProtocolCommands.PULSE)
                                .add(ProtocolCommands.LOOP_PULSE)
                                .add(ProtocolCommands.DELAY_PULSE)
                                .add(ProtocolCommands.TIMED_PULSE)
                                .build(), false
                );
    }


    private static class ProtocolBuilder {
        private final int sequence_length;
        private final Map<ProtocolCommand, Sequence> commands = new HashMap<>();

        private ProtocolBuilder(int sequenceLength) {
            sequence_length = sequenceLength;
        }

        public static ProtocolBuilder builder(int sequence_length) {
           return new ProtocolBuilder(sequence_length);
        }

        /// Override to include a command with pre-chosen sequence.
        ProtocolBuilder add(Sequence sequence, Supplier<ProtocolCommand> command) {
            commands.put(command.get(), sequence);
            return this;
        }

        /// Override to auto determine a sequence.
        ProtocolBuilder add(Supplier<ProtocolCommand> command) {
            int i = 0;
            while(i < Math.pow(2, sequence_length)) {
                Sequence s = Sequence.truncatedFromInt(i);
                while(s.length() < sequence_length) {
                    s.append(false);
                }
                if(!commands.containsValue(s)) {
                    return add(s, command);
                }
                i++;
            }
            throw new IllegalStateException("Too many commands registered for the provided sequence length " + sequence_length);
        }

        public Protocol build() {
            return new Protocol(sequence_length, commands);
        }
    }
}
