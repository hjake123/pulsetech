package dev.hyperlynx.pulsetech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.ProtocolData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Map;
import java.util.Objects;

@EventBusSubscriber
public class ModCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command_builder = Commands.literal("pulsetech-debug")
                        .then(Commands.literal("set-debug-protocol-as-default").executes(ModCommands::debugSetDefaultProtocol));

        event.getDispatcher().register(command_builder);
    }

    private static int debugSetDefaultProtocol(CommandContext<CommandSourceStack> context) {
        ProtocolData.retrieve(context.getSource().getLevel()).setDefaultFor(Objects.requireNonNull(context.getSource().getPlayer()), Pulsetech.location("debug"));
        return 1;
    }
}
