package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerByteInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerPosInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerTextInfo;
import dev.hyperlynx.pulsetech.feature.screen.ScreenUpdatePayload;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/// The ONLY class within the client package that is safe to call upon from outside the package!
/// Wraps all the capabilities of the client.
public class ClientWrapper {
    public static void openConsoleScreen(BlockPos pos, String prior_lines, String command_box_text, List<String> extra_names) {
        PulsetechClient.openConsoleScreen(pos, prior_lines, command_box_text, extra_names);
    }

    public static void acceptConsoleLine(BlockPos pos, String line) {
        PulsetechClient.acceptConsoleLine(pos, line);
    }

    public static void setPriorConsoleLines(BlockPos pos, String lines, String command_box_text) {
        PulsetechClient.setPriorConsoleLines(pos, lines, command_box_text);
    }

    public static void openSequenceScreen(BlockPos pos) {
        PulsetechClient.openSequenceScreen(pos);
    }

    public static void openDatasheetScreen(Datasheet datasheet) {
        PulsetechClient.openDatasheetScreen(datasheet);
    }

    public static void acceptScreenBlockPayload(ScreenUpdatePayload payload, IPayloadContext ignored) {
        PulsetechClient.updateScreenBlock(payload.data(), payload.pos());
    }

    public static void openNumberChooseScreen(BlockPos pos) {
        PulsetechClient.openNumberChooseScreen(pos);
    }

    public static void openDebuggerScreen(DebuggerInfoManifest manifest, IPayloadContext ignored) {
        PulsetechClient.openDebuggerScreen(manifest);
    }

    public static void acceptDebuggerSequenceInfo(DebuggerSequenceInfo info, IPayloadContext ignored) {
        PulsetechClient.acceptDebuggerSequenceInfo(info);
    }

    public static void acceptDebuggerByteInfo(DebuggerByteInfo info, IPayloadContext ignored) {
        PulsetechClient.acceptDebuggerByteInfo(info);
    }

    public static void acceptDebuggerTextInfo(DebuggerTextInfo info, IPayloadContext ignored) {
        PulsetechClient.acceptDebuggerTextInfo(info);
    }

    public static void acceptDebuggerPosInfo(DebuggerPosInfo info, IPayloadContext ignored) {
        PulsetechClient.acceptDebuggerPosInfo(info);
    }

    public static void copyToClipboard(String contents) {
        PulsetechClient.copyToClipboard(contents);
    }

    public static void acceptConsoleCompletionData(BlockPos pos, List<String> macros) {
        PulsetechClient.acceptConsoleCompletionData(pos, macros);
    }
}
