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

    public static void acceptScreenBlockPayload(ScreenUpdatePayload payload, IPayloadContext context) {
        PulsetechClient.updateScreenBlock(payload.data(), payload.pos());
    }

    public static void openNumberChooseScreen(BlockPos pos) {
        PulsetechClient.openNumberChooseScreen(pos);
    }

    public static void openDebuggerScreen(DebuggerInfoManifest manifest, IPayloadContext context) {
        PulsetechClient.openDebuggerScreen(manifest);
    }

    public static void acceptDebuggerSequenceInfo(DebuggerSequenceInfo info, IPayloadContext iPayloadContext) {
        PulsetechClient.acceptDebuggerSequenceInfo(info);
    }

    public static void acceptDebuggerByteInfo(DebuggerByteInfo info, IPayloadContext context) {
        PulsetechClient.acceptDebuggerByteInfo(info);
    }

    public static void acceptDebuggerTextInfo(DebuggerTextInfo info, IPayloadContext context) {
        PulsetechClient.acceptDebuggerTextInfo(info);
    }

    public static void acceptDebuggerPosInfo(DebuggerPosInfo info, IPayloadContext context) {
        PulsetechClient.acceptDebuggerPosInfo(info);
    }
}
