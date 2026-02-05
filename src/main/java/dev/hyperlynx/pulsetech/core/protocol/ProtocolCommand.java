package dev.hyperlynx.pulsetech.core.protocol;

import net.minecraft.network.chat.Component;

/// A command that a ProtocolBlockEntity could perform when it hears a certain pulse code from a Protocol.
public abstract class ProtocolCommand {
    private final int parameter_count;

    protected ProtocolCommand(int parameter_count) {
        this.parameter_count = parameter_count;
    }

    public int parameterCount() {
        return parameter_count;
    }

    /// Method to run to perform this command. Only runs after each required Parameter is ready.
    public abstract void run(ExecutionContext context);

    public Component getNameComponent() {
        return Component.translatable("protocol.pulsetech.name." + ProtocolCommands.REGISTRY.getKey(this).getPath());
    }
}
