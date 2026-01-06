package dev.hyperlynx.pulsetech.core.program;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.feature.console.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Map.entry;

/// Helper class which can run a List of tokens as a program, given a ProgramExecutor instance.
public class ProgramInterpreter {
    public static final Map<String, BiConsumer<ServerPlayer, ProgramExecutor>> BUILT_IN_COMMANDS = Map.ofEntries(
            entry("help", (player, executor) -> {
                StringBuilder help_builder = new StringBuilder();
                addBuiltInInfo(help_builder);
                help_builder.append("\n");
                for(String key : executor.getMacros().macros().keySet()) {
                    help_builder.append(key).append(": ");
                    for(String token : executor.getMacros().macros().get(key)) {
                        help_builder.append(token).append(" ");
                    }
                    help_builder.append("\n");
                }
                executor.sendLineIfConsole(player, help_builder.toString());
            }),
            entry("clear", (player, executor) -> {
                if(executor.isConsole()) {
                    PacketDistributor.sendToPlayer(player, new ConsolePriorLinesPayload(executor.getBlockPos(), ""));
                }
            }),
            entry("stop", (player, executor) -> {
                executor.setOperationMode(OperationMode.OUTPUT);
                executor.setMode(CommandMode.PARSE);
                executor.resetEmitter();
                executor.setChanged();
            }),
            entry("define", (player, executor) -> {
                executor.setMode(CommandMode.DEFINE);
            }),
            entry("forget", (player, executor) -> {
                executor.setMode(CommandMode.FORGET);
            }),
            entry("loop", (player, executor) -> {
                executor.setOperationMode(OperationMode.LOOP_OUTPUT);
                executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.looping").getString());
            }),
            entry("wait", (player, executor) -> {
                executor.setMode(CommandMode.SET_DELAY);
            }),
            entry("emit", (player, executor) -> {
                executor.setMode(CommandMode.EMIT);
            }),
            entry("num", (player, executor) -> {
                executor.setMode(CommandMode.NUM);
            }),
            entry("color", (player, executor) -> {
                executor.setMode(CommandMode.COLOR);
            })
    );

    private static void addBuiltInInfo(StringBuilder help_builder) {
        for (String built_in : BUILT_IN_COMMANDS.keySet().stream().sorted().toList()) {
            help_builder.append(built_in).append(": ").append(Component.translatable("help.pulsetech." + built_in).getString()).append("\n");
        }
    }

    private static final int MAX_STACK_DEPTH = 16;
    public static void processTokenList(ProgramExecutor executor, List<String> tokens, ServerPlayer player, int depth) {
        // If this function was recursively called by a macro too many times, don't execute.
        if(depth > MAX_STACK_DEPTH) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(executor.getBlockPos(), Component.translatable("console.pulsetech.stack_overflow").getString()));
            return;
        }
        executor.setMode(CommandMode.PARSE);
        boolean error = false; // Tracks invalid tokens during parsing
        String noun = ""; // Used for define operations
        List<String> definition = new ArrayList<>(); // Used for define operations
        Iterator<String> token_iterator = tokens.iterator();
        while (token_iterator.hasNext()) {
            String token = token_iterator.next();
            switch(executor.getCommandMode()) {
                case PARSE -> error = processToken(executor, player, token, depth, token_iterator);
                case DEFINE -> {
                    if(noun.isEmpty()) {
                        noun = token;
                    } else {
                        definition.add(token);
                    }
                }
                case FORGET -> {
                    if(executor.getMacros().macros().remove(token) != null) {
                        executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.forgot").getString() + token);
                    }
                }
                case SET_DELAY -> {
                    try {
                        executor.getEmitter().delay_points.put(executor.getEmitter().getBuffer().length(), Short.parseShort(token)); // Add a new delay to the sequence.
                        executor.getEmitter().getBuffer().append(false); // Add extra bit to be a placeholder for the delay.
                    } catch (NumberFormatException e) {
                        error = true;
                    }
                    executor.setMode(CommandMode.PARSE);
                }
                case EMIT -> {
                    Sequence sequence = new Sequence();
                    for(char c : token.toCharArray()) {
                        if(c == '0') {
                            sequence.append(false);
                        } else if (c == '1') {
                            sequence.append(true);
                        } else {
                            error = true;
                            break;
                        }
                    }
                    if(!error) {
                        executor.getEmitter().enqueueTransmission(sequence);
                        executor.getEmitter().setActive(true);
                    }
                    executor.setMode(CommandMode.PARSE);

                }
                case NUM -> {
                    try {
                        Sequence sequence = Sequence.fromByte(Byte.parseByte(token));
                        executor.getEmitter().enqueueTransmission(sequence);
                        executor.getEmitter().setActive(true);
                    }
                    catch (NumberFormatException e) {
                        executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.invalid_number").getString());
                    }
                    executor.setMode(CommandMode.PARSE);
                }
                case COLOR -> {
                    try {
                        Color color = new Color(Integer.parseInt(token, 16));
                        executor.getEmitter().enqueueTransmission(Sequence.fromByte((byte) color.red));
                        executor.getEmitter().enqueueTransmission(Sequence.fromByte((byte) color.green));
                        executor.getEmitter().enqueueTransmission(Sequence.fromByte((byte) color.blue));
                        executor.getEmitter().setActive(true);
                    }
                    catch (NumberFormatException e) {
                        executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.invalid_color").getString());
                    }
                    executor.setMode(CommandMode.PARSE);
                }
            }
        }
        if(!executor.getEmitter().getBuffer().isEmpty() && !error) {
            executor.setActive(true);
        }
        if(executor.getCommandMode().equals(CommandMode.SET_DELAY)) {
            executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.wait_usage").getString());
        }
        if(executor.getCommandMode().equals(CommandMode.EMIT)) {
            executor.sendLineIfConsole(player,  Component.translatable("console.pulsetech.emit_usage").getString());
        }
        if(executor.getCommandMode().equals(CommandMode.NUM)) {
            executor.sendLineIfConsole(player,  Component.translatable("console.pulsetech.num_usage").getString());
        }
        if(executor.getCommandMode().equals(CommandMode.DEFINE)) {
            if(BUILT_IN_COMMANDS.containsKey(noun)) {
                executor.sendLineIfConsole(player,  Component.translatable("console.pulsetech.macro_name_taken").getString());
            } else if(noun.isEmpty() || definition.isEmpty()) {
                executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.define_help").getString() + noun);
            }
            else {
                if (executor.getMacros().macros().containsKey(noun)) {
                    executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.redefined").getString() + noun);
                } else {
                    executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.defined").getString() + noun);
                }
                executor.getMacros().macros().put(noun, new ArrayList<>(definition));
            }
        }
    }

    private static boolean processToken(ProgramExecutor executor, ServerPlayer player, String token, int depth, Iterator<String> tokens) {
        if(BUILT_IN_COMMANDS.containsKey(token.toLowerCase())) {
            BUILT_IN_COMMANDS.get(token.toLowerCase()).accept(player, executor);
        } else if(executor.getMacros().macros().containsKey(token)) {
            // Check for '?' in the macro definition. If they're present, this is a macro with parameters, and we'll need to sub those in.
            List<String> definition = new ArrayList<>(executor.getMacros().macros().get(token));
            while(definition.stream().anyMatch(subtoken -> subtoken.equals("?"))) {
                int unresolved_parameter_index = definition.indexOf("?");
                if(!tokens.hasNext()) {
                    executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.too_few_parameters").getString() + token);
                    executor.getEmitter().reset();
                    return true;
                }
                String parameter = tokens.next();
                definition.set(unresolved_parameter_index, parameter);
            }

            // recursively process macros up to a set depth.
            processTokenList(executor, definition, player, depth + 1);
            return false;
        } else {
            executor.sendLineIfConsole(player, Component.translatable("console.pulsetech.invalid_token").getString() + token);
            executor.getEmitter().reset();
            return true;
        }
        return false;
    }
}
