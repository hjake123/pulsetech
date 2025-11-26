package dev.hyperlynx.pulsetech.registration;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.hyperlynx.pulsetech.core.PatternHolder;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class ModCommands {
    private static final CommandExceptionType BAD_SEQUENCE = new SimpleCommandExceptionType(() -> "Invalid bit sequence");

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command_builder = Commands.literal("pulsetech-debug")
                        .then(Commands.literal("set-pattern")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("pattern", StringArgumentType.word())
                                .executes(ModCommands::setPattern))));
        event.getDispatcher().register(command_builder);
    }

    private static int setPattern(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        if(!(context.getSource().getLevel().getBlockEntity(pos) instanceof PatternHolder holder)) {
            return 0;
        }
        String pattern = StringArgumentType.getString(context, "pattern");
        Sequence input = new Sequence();
        for(char c : pattern.toCharArray()) {
            if(c == '1') {
                input.append(true);
            } else if (c == '0') {
                input.append(false);
            } else {
                throw new CommandSyntaxException(BAD_SEQUENCE, () -> "pattern contains unknown characters");
            }
        }
        holder.setPattern(input);
        return 1;
    }
}
