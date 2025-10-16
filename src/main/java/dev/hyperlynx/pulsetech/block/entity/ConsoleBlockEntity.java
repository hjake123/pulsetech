package dev.hyperlynx.pulsetech.block.entity;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.pulse.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
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
    private boolean looping = false;

    private Map<String, List<String>> macros = new HashMap<>(); // Defined macros for this console. TODO better persistence?
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());

    private Map<Integer, Short> delay_points = new HashMap<>();
    private static final Codec<Map<Integer, Short>> DELAYS_CODEC = Codec.unboundedMap(Codec.INT, Codec.SHORT);

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
        if(delay_points.containsKey(output_cursor)) {
            delay(delay_points.get(output_cursor));
            output_cursor++;
            return true;
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        if(looping) {
            delay(4);
            output_cursor = 0;
            return true;
        }
        output_initialized = false;
        delay_points.clear();
        return false;
    }

    private final Map<String, BiConsumer<ServerPlayer, ConsoleBlockEntity>> BUILT_IN_COMMANDS = Map.of(
            "help", (player, console) -> {
                StringBuilder help_builder = new StringBuilder();
                addBuiltInInfo(help_builder);
                help_builder.append("\n");
                for(String key : protocol.keys()) {
                    help_builder.append(key).append(": ").append(protocol.sequenceFor(key)).append("\n");
                }
                help_builder.append("\n");
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
                if(looping) {
                    setLooping(false);
                }
                buffer.clear();
            },
            "define", (player, console) -> {
                console.setMode(Mode.DEFINE);
            },
            "forget", (player, console) -> {
                console.setMode(Mode.FORGET);
            },
            "loop", (player, console) -> {
                console.setLooping(true);
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.looping").getString()));
            },
            "wait", (player, console) -> {
                console.setMode(Mode.SET_DELAY);
           }
    );

    private void addBuiltInInfo(StringBuilder help_builder) {
        for (String built_in : BUILT_IN_COMMANDS.keySet().stream().sorted().toList()) {
            help_builder.append(built_in).append(": ").append(Component.translatable("help.pulsetech." + built_in).getString()).append("\n");
        }
    }

    public void processLine(String line, ServerPlayer player) {
        processTokenList(Arrays.stream(line.split(" ")).toList(), player, 0);
    }

    private static final int MAX_STACK_DEPTH = 16;
    public void processTokenList(List<String> tokens, ServerPlayer player, int depth) {
        if(protocol == null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.no_protocol").getString()));
            return;
        }
        // If this function was recursively called by a macro too many times, don't execute.
        if(depth > MAX_STACK_DEPTH) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.stack_overflow").getString()));
            return;
        }
        mode = Mode.PARSE;
        boolean error = false; // Tracks invalid tokens during parsing
        String noun = ""; // Used for define operations
        List<String> definition = new ArrayList<>(); // Used for define operations
        for(String token : tokens) {
            switch(mode) {
                case PARSE -> error = processToken(player, token, depth);
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
                case SET_DELAY -> {
                    try {
                        delay_points.put(this.buffer.length(), Short.parseShort(token)); // Add a new delay to the sequence.
                        buffer.append(false); // Add extra bit to be a placeholder for the delay.
                    } catch (NumberFormatException e) {
                        error = true;
                    }
                    mode = Mode.PARSE;
                }
            }
        }
        if(buffer.length() > 0 && !error) {
            setActive(true);
        }
        if(mode.equals(Mode.SET_DELAY)) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.wait_usage").getString()));
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

    private boolean processToken(ServerPlayer player, String token, int depth) {
        if(BUILT_IN_COMMANDS.containsKey(token.toLowerCase())) {
            BUILT_IN_COMMANDS.get(token.toLowerCase()).accept(player, this);
        } else if(macros.containsKey(token)) {
            // recursively process macros up to a set depth.
            processTokenList(macros.get(token), player, depth + 1);
            return false;
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
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros).ifSuccess(encoded -> tag.put("macros", encoded));
        }
        if(!delay_points.isEmpty()) {
            DELAYS_CODEC.encodeStart(NbtOps.INSTANCE, delay_points).ifSuccess(encoded -> tag.put("delays", encoded));
        }
        if(looping) {
            tag.putBoolean("looping", true);
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
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new HashMap<>(pair.getFirst()));
        }
        if(tag.contains("delays")) {
            DELAYS_CODEC.decode(NbtOps.INSTANCE, tag.get("delays")).ifSuccess(pair -> delay_points = new HashMap<>(pair.getFirst()));
        }
        looping = tag.contains("looping");
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
        SET_DELAY,
        FORGET
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    private void setLooping(boolean looping) {
        this.looping = looping;
        if(!looping) {
            delay_points.clear();
        }
        setChanged();
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
