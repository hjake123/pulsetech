package dev.hyperlynx.pulsetech.block.entity;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.ConsoleEmitterModule;
import dev.hyperlynx.pulsetech.pulse.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.pulse.module.PatternSensorModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.function.BiConsumer;

public class ConsoleBlockEntity extends ProtocolBlockEntity {
    private ConsoleEmitterModule emitter = new ConsoleEmitterModule();
    private PatternSensorModule pattern_sensor = new PatternSensorModule();
    private NumberSensorModule number_sensor = new NumberSensorModule();

    private CommandMode command_mode = CommandMode.PARSE;
    private OperationMode operation_mode = OperationMode.OUTPUT;
    private String saved_lines = "";

    private Map<String, List<String>> macros = new HashMap<>(); // Defined macros for this console. TODO better persistence?
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());

    public ConsoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONSOLE.get(), pos, blockState);
    }

    // The mode of the current command being parsed. Resets with each new command.
    private enum CommandMode {
        PARSE,
        DEFINE,
        SET_DELAY,
        FORGET
    }

    // The mode of the entire Console block. Only changed by specific commands.
    private enum OperationMode {
        OUTPUT,
        LOOP_OUTPUT,
        LISTEN,
        OUTPUT_THEN_LISTEN
    }

    private final Map<String, BiConsumer<ServerPlayer, ConsoleBlockEntity>> BUILT_IN_COMMANDS = Map.of(
            "help", (player, console) -> {
                StringBuilder help_builder = new StringBuilder();
                addBuiltInInfo(help_builder);
                help_builder.append("\n");
                for(String key : getProtocol().keys()) {
                    help_builder.append(key).append(": ").append(getProtocol().sequenceFor(key)).append("\n");
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
                console.setOperationMode(OperationMode.OUTPUT);
                console.setMode(CommandMode.PARSE);
                emitter.reset();
                setChanged();
            },
            "define", (player, console) -> {
                console.setMode(CommandMode.DEFINE);
            },
            "forget", (player, console) -> {
                console.setMode(CommandMode.FORGET);
            },
            "loop", (player, console) -> {
                console.setOperationMode(OperationMode.LOOP_OUTPUT);
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.looping").getString()));
            },
            "wait", (player, console) -> {
                console.setMode(CommandMode.SET_DELAY);
           },
            "listen", (player, console) -> {
                console.setOperationMode(OperationMode.OUTPUT_THEN_LISTEN);
            }
    );

    private void setOperationMode(OperationMode operation_mode) {
        this.operation_mode = operation_mode;
    }

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
        if(getProtocol() == null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.no_protocol").getString()));
            return;
        }
        // If this function was recursively called by a macro too many times, don't execute.
        if(depth > MAX_STACK_DEPTH) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.stack_overflow").getString()));
            return;
        }
        command_mode = CommandMode.PARSE;
        boolean error = false; // Tracks invalid tokens during parsing
        String noun = ""; // Used for define operations
        List<String> definition = new ArrayList<>(); // Used for define operations
        for(String token : tokens) {
            switch(command_mode) {
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
                        emitter.delay_points.put(emitter.getBuffer().length(), Short.parseShort(token)); // Add a new delay to the sequence.
                        emitter.getBuffer().append(false); // Add extra bit to be a placeholder for the delay.
                    } catch (NumberFormatException e) {
                        error = true;
                    }
                    command_mode = CommandMode.PARSE;
                }
            }
        }
        if(emitter.getBuffer().length() > 0 && !error) {
            setActive(true);
        }
        if(command_mode.equals(CommandMode.SET_DELAY)) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.wait_usage").getString()));
        }
        if(command_mode.equals(CommandMode.DEFINE)) {
            if(BUILT_IN_COMMANDS.containsKey(noun) || getProtocol().hasKey(noun) || macros.containsKey(noun)) {
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
        else if(getProtocol().hasKey(token)) {
            setOperationMode(OperationMode.OUTPUT);
            emitter.enqueueTransmission(Objects.requireNonNull(getProtocol().sequenceFor(token)));
            emitter.setActive(true);
        } else {
            try {
                setOperationMode(OperationMode.OUTPUT);
                emitter.enqueueTransmission(getProtocol().fromShort(Short.parseShort(token)));
                emitter.setActive(true);
            } catch (NumberFormatException ignored) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.invalid_token").getString() + token));
                emitter.reset();
                return true;
            }
        }
        return false;
    }

    private void limitPriorLineLength() {
        if(saved_lines.length() > 8192) {
            saved_lines = saved_lines.substring(saved_lines.length() - 8192);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        limitPriorLineLength();
        var encode_result = ConsoleEmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter);
        if(encode_result.hasResultOrPartial()) {
            tag.put("Emitter", encode_result.getPartialOrThrow());
        }
        var ps_encode_result = PatternSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, pattern_sensor);
        if(ps_encode_result.hasResultOrPartial()) {
            tag.put("PatternSensor", ps_encode_result.getPartialOrThrow());
        }
        var ns_encode_result = NumberSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, number_sensor);
        if(ns_encode_result.hasResultOrPartial()) {
            tag.put("NumberSensor", ns_encode_result.getPartialOrThrow());
        }
        if(!saved_lines.isEmpty()) {
            tag.putString("saved_lines", saved_lines);
        }
        if(!macros.isEmpty()) {
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros).ifSuccess(encoded -> tag.put("macros", encoded));
        }
        tag.putString("OperationMode", operation_mode.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var decode_result = ConsoleEmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Emitter"));
        if(decode_result.hasResultOrPartial()) {
            emitter = decode_result.getPartialOrThrow().getFirst();
        }
        var ps_decode_result = PatternSensorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("PatternSensor"));
        if(ps_decode_result.hasResultOrPartial()) {
            pattern_sensor = ps_decode_result.getPartialOrThrow().getFirst();
        }
        var ns_decode_result = NumberSensorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("NumberSensor"));
        if(ns_decode_result.hasResultOrPartial()) {
            number_sensor = ns_decode_result.getPartialOrThrow().getFirst();
        }
        if(tag.contains("saved_lines")) {
            saved_lines = tag.getString("saved_lines");
        }
        if(tag.contains("macros")) {
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new HashMap<>(pair.getFirst()));
        }
        if(tag.contains("OperationMode")) {
            operation_mode = OperationMode.valueOf(tag.getString("OperationMode"));
        }
    }

    @Override
    public void handleInput() {
        if(!operation_mode.equals(OperationMode.LISTEN)) {
            Pulsetech.LOGGER.warn("Handling input within a console that is not in LISTEN mode...? Ignoring.");
            return;
        }
        String input;
        if(!pattern_sensor.getLastPattern().isEmpty()) {
            input = pattern_sensor.getLastPattern();
        } else if(number_sensor.checkNewNumberReady()){
            input = number_sensor.getNumber() + "";
        } else {
            return;
        }
        if(saved_lines.isEmpty()) {
            saved_lines = input;
        } else {
            saved_lines += "\n" + input;
        }
        setChanged();
        PacketDistributor.sendToAllPlayers(new ConsoleLinePayload(getBlockPos(), input)); // TODO FOR TESTING ONLY
    }

    public void savePriorLines(String lines) {
        saved_lines = lines;
        setChanged();
    }

    public String getPriorLinesOrEmpty() {
        return saved_lines;
    }

    @Override
    public boolean isActive() {
        return pattern_sensor.isActive() || number_sensor.isActive();
    }

    @Override
    public void setActive(boolean active) {
        if(operation_mode.equals(OperationMode.LISTEN)) {
            // Don't change the activation outside of LISTEN mode.
            if(!(pattern_sensor.getDelay() > 0))
                pattern_sensor.setActive(active);
            if(!(number_sensor.getDelay() > 0))
                number_sensor.setActive(active);
        }
    }

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            switch (operation_mode) {
                case OUTPUT -> {
                    emitter.looping = false;
                    emitter.tick(slevel, this);
                }
                case LOOP_OUTPUT -> {
                    emitter.looping = true;
                    emitter.tick(slevel, this);
                }
                case OUTPUT_THEN_LISTEN -> {
                    emitter.looping = false;
                    emitter.tick(slevel, this);
                    if(emitter.getBuffer().length() == 0) {
                        setOperationMode(OperationMode.LISTEN);
                        number_sensor.reset();
                        number_sensor.delay(2);
                        pattern_sensor.reset();
                        pattern_sensor.delay(2);
                    }
                }
                case LISTEN -> {
                    pattern_sensor.tick(slevel, this);
                    number_sensor.tick(slevel, this);
                }
            }
        }
    }

    private void setMode(CommandMode command_mode) {
        this.command_mode = command_mode;
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
