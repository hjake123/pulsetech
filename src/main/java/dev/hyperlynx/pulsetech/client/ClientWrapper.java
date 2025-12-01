package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
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

    public static void openSequenceScreen(BlockPos pos) {
        PulsetechClient.openSequenceScreen(pos);
    }

    public static void openDatasheetScreen(Datasheet datasheet) {
        PulsetechClient.openDatasheetScreen(datasheet);
    }


}
