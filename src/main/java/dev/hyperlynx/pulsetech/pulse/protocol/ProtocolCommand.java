package dev.hyperlynx.pulsetech.pulse.protocol;

import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;

import java.util.Map;

/// A command that a ProtocolBlockEntity could perform when it hears a certain pulse code from a Protocol.
public abstract class ProtocolCommand<T extends PulseBlockEntity> {
    protected final Map<String, Parameter<?>> parameters;

    protected ProtocolCommand(Map<String, Parameter<?>> parameters) {
        this.parameters = parameters;
    }

    abstract void run(T block);
}
