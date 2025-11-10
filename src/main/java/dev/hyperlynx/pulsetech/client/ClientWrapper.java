package dev.hyperlynx.pulsetech.client;

import net.minecraft.core.BlockPos;

/// The ONLY class within the client package that is safe to call upon from outside the package!
public class ClientWrapper {
    public static void openConsoleScreen(BlockPos pos, String prior_lines) {
        PulsetechClient.openConsoleScreen(pos, prior_lines);
    }

    public static void acceptConsoleLine(BlockPos pos, String line) {
        PulsetechClient.acceptConsoleLine(pos, line);
    }

    public static void setPriorConsoleLines(BlockPos pos, String lines) {
        PulsetechClient.setPriorConsoleLines(pos, lines);
    }
}
