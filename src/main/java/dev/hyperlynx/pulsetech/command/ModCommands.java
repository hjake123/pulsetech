package dev.hyperlynx.pulsetech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.data.ProtocolData;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
                        .then(Commands.literal("create-debug-protocol").executes(ModCommands::debugCreateProtocol))
                        .then(Commands.literal("set-debug-protocl-as-default").executes(ModCommands::debugSetDefaultProtocol));

        event.getDispatcher().register(command_builder);
    }

    private static int debugSetDefaultProtocol(CommandContext<CommandSourceStack> context) {
        ProtocolData.retrieve(context.getSource().getLevel()).setDefaultFor(Objects.requireNonNull(context.getSource().getPlayer()), "debug");
        return 1;
    }

    private static int debugCreateProtocol(CommandContext<CommandSourceStack> context) {
        Protocol debug_protocol = new Protocol(4,
                Map.of(
                        Protocol.ACK, new Sequence(false, false, false, false),
                        Protocol.ERR, new Sequence(true, true, true, true),
                        "A", new Sequence(true, false, true, false),
                        "B", new Sequence(false, true, true, false),
                        "C", new Sequence(false, false, true, true),
                        Protocol.NUM, new Sequence(true, true, false, true)
                )
        );
        ProtocolData.retrieve(context.getSource().getLevel()).add("debug", debug_protocol);
        return 1;
    }
}
