package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.function.BiConsumer;

public class ConsoleBlockEntity extends ProtocolBlockEntity {
    private Mode mode = Mode.PARSE;
    private String saved_lines = "";

    private Map<String, List<String>> macros = new HashMap<>(); // Defined macros for this console. TODO better persistence?

    public ConsoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONSOLE.get(), pos, blockState);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;

    @Override
    protected boolean run() {
        if(!output_initialized) {
            output_cursor = 0;
            output_initialized = true;
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        output_initialized = false;
        return false;
    }

    private final Map<String, BiConsumer<ServerPlayer, ConsoleBlockEntity>> BUILT_IN_COMMANDS = Map.of(
            "help", (player, console) -> {
                StringBuilder help_builder = new StringBuilder();
                addBuiltInInfo(help_builder);
                for(String key : protocol.keys()) {
                    help_builder.append(key).append(": ").append(protocol.sequenceFor(key)).append("\n");
                }
                for(String key : macros.keySet()) {
                    help_builder.append(key).append(": ");
                    for(String token : macros.get(key)) {
                        help_builder.append(token).append(" ");
                    }
                    help_builder.append("\n");
                }
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), help_builder.toString()));
            },
            "clear", (player, console) -> {
                PacketDistributor.sendToPlayer(player, new ConsolePriorLinesPayload(getBlockPos(), ""));
            },
            "stop", (player, console) -> {
                buffer.clear();
            },
            "define", (player, console) -> {
                console.setMode(Mode.DEFINE);
            },
            "forget", (player, console) -> {
                console.setMode(Mode.FORGET);
            }
    );

    private void addBuiltInInfo(StringBuilder help_builder) {
        for (String built_in : BUILT_IN_COMMANDS.keySet().stream().sorted().toList()) {
            help_builder.append(built_in).append(": ").append(Component.translatable("help.pulsetech." + built_in).getString()).append("\n");
        }
    }

    public void processLine(String line, ServerPlayer player) {
        if(protocol == null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.no_protocol").getString()));
            return;
        }
        mode = Mode.PARSE;
        boolean error = false; // Tracks invalid tokens during parsing
        String noun = ""; // Used for define operations
        List<String> definition = new ArrayList<>(); // Used for define operations
        for(String token : Arrays.stream(line.split(" ")).toList()) {
            switch(mode) {
                case PARSE -> error = processToken(player, token, 0);
                case DEFINE -> {
                    if(noun.isEmpty()) {
                        noun = token;
                    } else {
                        definition.add(token);
                    }
                }
                case FORGET -> {
                    if(macros.remove(token) != null) {
                        PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.forgot").getString() + token));
                    }
                }
            }
        }
        if(buffer.length() > 0 && !error) {
            setActive(true);
        }
        if(mode.equals(Mode.DEFINE)) {
            if(BUILT_IN_COMMANDS.containsKey(noun) || protocol.hasKey(noun) || macros.containsKey(noun)) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.macro_name_taken").getString()));
            } else if(noun.isEmpty() || definition.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.define_help").getString() + noun));
            }
            else {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.defined").getString() + noun));
                macros.put(noun, new ArrayList<>(definition));
            }
        }
    }

    private static final int MAX_STACK_DEPTH = 16;
    private boolean processToken(ServerPlayer player, String token, int depth) {
        if(BUILT_IN_COMMANDS.containsKey(token.toLowerCase())) {
            BUILT_IN_COMMANDS.get(token.toLowerCase()).accept(player, this);
        } else if(macros.containsKey(token)) {
            // recursively process macros up to a set depth.
            if(depth > MAX_STACK_DEPTH) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.stack_overflow").getString()));
                return false;
            }
            boolean macros_return_value = false;
            for(String subtoken : macros.get(token)) {
                macros_return_value = macros_return_value || processToken(player, subtoken, depth + 1);
            }
            return macros_return_value;
        }
        else if(protocol.hasKey(token)) {
            buffer.append(true);
            buffer.appendAll(Objects.requireNonNull(protocol.sequenceFor(token)));
            buffer.append(false);
        } else {
            try {
                buffer.append(true);
                buffer.appendAll(protocol.fromShort(Short.parseShort(token)));
                buffer.append(false);
            } catch (NumberFormatException ignored) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.invalid_token").getString() + token));
                buffer.clear();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(mode.equals(Mode.PARSE)) {
            tag.putBoolean("output_mode", true);
        }
        if(!saved_lines.isEmpty()) {
            tag.putString("saved_lines", saved_lines);
        }
        if(!macros.isEmpty()) {
//            ListTag macros_tag = new ListTag(ListTag.TAG_COMPOUND);
//            for(Map.Entry<String, List<String>> macro : macros.entrySet()) {
//                CompoundTag macro_tag = new CompoundTag();
//                macro_tag.putString("noun", macro.getKey());
//                ListTag definition_tag = new ListTag(ListTag.TAG_STRING);
//                for(String token : macro.getValue()) {
//                    definition_tag.add(StringTag.valueOf(token));
//                }
//                macro_tag.put("definition", definition_tag);
//                macros_tag.add(ListTag.TAG_COMPOUND, macro_tag);
//            }
//            tag.put("macros", macros_tag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("output_mode")) {
            mode = Mode.PARSE;
        }
        if(tag.contains("saved_lines")) {
            saved_lines = tag.getString(saved_lines);
        }
        if(tag.contains("macros")) {
            tag.getList("macros", ListTag.TAG_COMPOUND).forEach(compound -> {
                assert compound instanceof CompoundTag;
                String noun = ((CompoundTag) compound).getString("noun");
                List<String> definition = new ArrayList<>();
                for(Tag token_tag : ((CompoundTag) compound).getList("definition", ListTag.TAG_STRING)) {
                    assert token_tag instanceof StringTag;
                    definition.add(token_tag.getAsString());
                };
                macros.put(noun, definition);
            });
        }
    }

    public void savePriorLines(String lines) {
        saved_lines = lines;
        setChanged();
    }

    public String getPriorLinesOrEmpty() {
        return saved_lines;
    }

    private enum Mode {
        PARSE,
        DEFINE,
        FORGET
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    // Create an update tag here, like above.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
