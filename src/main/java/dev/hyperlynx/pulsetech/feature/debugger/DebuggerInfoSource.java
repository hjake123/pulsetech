package dev.hyperlynx.pulsetech.feature.debugger;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

/// Implemented by BlockEntities that can provide information to the Debugger system.
public interface DebuggerInfoSource {
    /// Gets the manifest that shows what types of info can be provided and at which indexes.
    DebuggerInfoManifest getDebuggerInfoManifest();

    /// Gets a list of getters for debug info payloads. The order must match the order promised in the manifest.
    List<Supplier<CustomPacketPayload>> getDebugInfoGetters();

    /// Sends the info at the requested index
    default void sendInfo(ServerPlayer recipient, int id) {
        PacketDistributor.sendToPlayer(recipient, getDebugInfoGetters().get(id).get());
    }
}
