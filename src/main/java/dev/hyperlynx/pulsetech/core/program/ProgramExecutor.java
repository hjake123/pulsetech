package dev.hyperlynx.pulsetech.core.program;

import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

/// Interface that abstracts Consoles and Processors to allow both (and any future other blocks) to run Programs.
public interface ProgramExecutor {
    Macros getMacros();
    BlockPos getBlockPos();
    boolean isConsole();
    void setCommandMode(CommandMode commandMode);
    void setChanged();
    CommandMode getCommandMode();
    ProgramEmitterModule getEmitter();
    void setActive(boolean b);

    default void sendLineIfConsole(@Nullable ServerPlayer player, String line) {
        if(isConsole() && player != null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), line));
        }
    }
}
