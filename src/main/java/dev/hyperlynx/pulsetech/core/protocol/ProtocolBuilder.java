package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.datagen.ProtocolGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ProtocolBuilder {
    private final int sequence_length;
    private final Map<ProtocolCommand, Sequence> commands = new HashMap<>();

    private ProtocolBuilder(int sequenceLength) {
        sequence_length = sequenceLength;
    }

    public static ProtocolBuilder builder(int sequence_length) {
        return new ProtocolBuilder(sequence_length);
    }

    /// Override to include a command with pre-chosen sequence.
    public ProtocolBuilder add(Supplier<ProtocolCommand> command, Sequence sequence) {
        commands.put(command.get(), sequence);
        return this;
    }

    /// Override to auto determine a sequence.
    public ProtocolBuilder add(Supplier<ProtocolCommand> command) {
        int i = 0;
        while (i < Math.pow(2, sequence_length)) {
            Sequence s = Sequence.truncatedFromInt(i);
            while (s.length() < sequence_length) {
                s.append(false);
            }
            if (!commands.containsValue(s)) {
                return add(command, s);
            }
            i++;
        }
        throw new IllegalStateException("Too many commands registered for the provided sequence length " + sequence_length);
    }

    public Protocol build() {
        return new Protocol(sequence_length, commands);
    }
}
