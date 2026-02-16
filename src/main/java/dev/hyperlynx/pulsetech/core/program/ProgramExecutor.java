package dev.hyperlynx.pulsetech.core.program;

import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/// Interface that abstracts Consoles and Processors to allow both (and any future other blocks) to run Programs.
public interface ProgramExecutor {
    Map<String, List<String>> getMacros();
    BlockPos getBlockPos();
    boolean isConsole();
    void setCommandMode(CommandMode commandMode);
    void setChanged();
    CommandMode getCommandMode();
    ProgramEmitterModule getEmitter();
    void setActive(boolean b);
    int getUnwrapCount();
    void incrementUnwrapCount();
    void resetUnwrapCount();

    default void sendLineIfConsole(@Nullable ServerPlayer player, String line) {
        if(isConsole() && player != null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), line));
        }
    }
}
