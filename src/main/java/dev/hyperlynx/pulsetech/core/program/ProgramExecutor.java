package dev.hyperlynx.pulsetech.core.program;

import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;

/// Interface that abstracts Consoles and Processors to allow both (and any future other blocks) to run Programs.
public interface ProgramExecutor {
    Macros getMacros();
    BlockPos getBlockPos();
    boolean isConsole();
    void setOperationMode(OperationMode operationMode);
    void setMode(CommandMode commandMode);
    void resetEmitter();
    void setChanged();
    CommandMode getCommandMode();
    ProgramEmitterModule getEmitter();
    void setActive(boolean b);

    default void sendLineIfConsole(ServerPlayer player, String line) {
        if(isConsole()) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), line));
        }
    }
}
