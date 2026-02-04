package dev.hyperlynx.pulsetech.feature.debugger;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/// Implemented by BlockEntities that can provide information to the Debugger system.
public interface DebuggerInfoSource {
    /// Gets the manifest that shows what types of info can be provided and at which indexes.
    DebuggerInfoManifest getDebuggerInfoManifest();
}
