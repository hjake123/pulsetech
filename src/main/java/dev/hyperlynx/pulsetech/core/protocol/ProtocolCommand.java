package dev.hyperlynx.pulsetech.core.protocol;

/// A command that a ProtocolBlockEntity could perform when it hears a certain pulse code from a Protocol.
public abstract class ProtocolCommand {
    public void ownerInit(ProtocolBlockEntity block) {
        block.requireParameters(parameterCount());
    }

    public int parameterCount() {
        return 0;
    }

    public abstract void run(ProtocolBlockEntity block);
}
