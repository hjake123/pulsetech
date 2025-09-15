package dev.hyperlynx.pulsetech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
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

@EventBusSubscriber
public class ModCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command_builder = Commands.literal("pulsetech-debug")
                        .then(Commands.literal("get-protocol-paper").executes(ModCommands::debugGetCommandSlip));

        event.getDispatcher().register(command_builder);
    }

    private static int debugGetCommandSlip(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if(player == null) {
            return 0;
        }
        ItemStack paper = Items.PAPER.getDefaultInstance();
        paper.set(ModComponentTypes.PROTOCOL.get(), new Protocol(4,
                Map.of(
                        Protocol.ACK, new Sequence(false, false, false, false),
                        Protocol.ERR, new Sequence(true, true, true, true),
                        "A", new Sequence(true, false, true, false),
                        "B", new Sequence(false, true, true, false)
                ))
        );
        player.addItem(paper);
        return 1;
    }
}
