package dev.hyperlynx.pulsetech.pulse.protocol;

import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;

import java.util.HashMap;
import java.util.Map;

/// A command that a ProtocolBlockEntity could perform when it hears a certain pulse code from a Protocol.
public abstract class ProtocolCommand {
    protected final Map<String, Parameter<?>> parameters = new HashMap<>();

    public void ownerInit(ProtocolBlockEntity block) {}

    public abstract void run(ProtocolBlockEntity block);
}
