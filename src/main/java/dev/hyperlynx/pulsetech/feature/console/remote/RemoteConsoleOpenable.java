package dev.hyperlynx.pulsetech.feature.console.remote;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public interface RemoteConsoleOpenable {
    void openScreen(BlockPos pos, ServerPlayer player);

    /// Colors are defined in the Remote Console's block model.
    /// - 0: Disabled
    /// - 1: Amber Console
    /// - 2: Red Console
    /// - 3: Green Console
    /// - 4: Indigo Console
    /// - 5: White Console
    /// - 6: Storage Modem
    int getColorCode();
}
