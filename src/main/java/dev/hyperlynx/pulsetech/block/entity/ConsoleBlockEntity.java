package dev.hyperlynx.pulsetech.block.entity;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.ConsoleEmitterModule;
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

    private Mode mode = Mode.PARSE;
    private String saved_lines = "";

    private Map<String, List<String>> macros = new HashMap<>(); // Defined macros for this console. TODO better persistence?
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());

    public ConsoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONSOLE.get(), pos, blockState);
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
                if(emitter.looping) {
                    setLooping(false);
                }
                console.setMode(Mode.PARSE);
                emitter.reset();
                setChanged();
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
           },
            "raw", (player, console) -> {
                console.setMode(Mode.RAW_LISTEN);
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
        if(getProtocol() == null) {
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
                        emitter.delay_points.put(emitter.getBuffer().length(), Short.parseShort(token)); // Add a new delay to the sequence.
                        emitter.getBuffer().append(false); // Add extra bit to be a placeholder for the delay.
                    } catch (NumberFormatException e) {
                        error = true;
                    }
                    mode = Mode.PARSE;
                }
            }
        }
        if(emitter.getBuffer().length() > 0 && !error) {
            setActive(true);
        }
        if(mode.equals(Mode.SET_DELAY)) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.wait_usage").getString()));
        }
        if(mode.equals(Mode.DEFINE)) {
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
            emitter.enqueueTransmission(Objects.requireNonNull(getProtocol().sequenceFor(token)));
            pattern_sensor.delay(emitter.getBuffer().length() * 2 + 1);
            pattern_sensor.getBuffer().clear();
            emitter.setActive(true);
        } else {
            try {
                emitter.enqueueTransmission(getProtocol().fromShort(Short.parseShort(token)));
                pattern_sensor.delay(emitter.getBuffer().length() * 2 + 1);
                pattern_sensor.getBuffer().clear();
                emitter.setActive(true);
            } catch (NumberFormatException ignored) {
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.invalid_token").getString() + token));
                emitter.reset();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var encode_result = ConsoleEmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter);
        if(encode_result.hasResultOrPartial()) {
            tag.put("Emitter", encode_result.getPartialOrThrow());
        }
        var ps_encode_result = PatternSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, pattern_sensor);
        if(ps_encode_result.hasResultOrPartial()) {
            tag.put("PatternSensor", ps_encode_result.getPartialOrThrow());
        }
        if(!saved_lines.isEmpty()) {
            tag.putString("saved_lines", saved_lines);
        }
        if(!macros.isEmpty()) {
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros).ifSuccess(encoded -> tag.put("macros", encoded));
        }
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
        if(tag.contains("saved_lines")) {
            saved_lines = tag.getString("saved_lines");
        }
        if(tag.contains("macros")) {
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new HashMap<>(pair.getFirst()));
        }
    }

    @Override
    public void handleInput() {
        if(mode.equals(Mode.RAW_LISTEN)) {
            // Ignore structured input
            return;
        }
        if(pattern_sensor.getLastPattern().isEmpty()) {
            return;
        }
        if(saved_lines.isEmpty()) {
            saved_lines = pattern_sensor.getLastPattern();
        } else {
            saved_lines += "\n" + pattern_sensor.getLastPattern();
        }
        setChanged();
        PacketDistributor.sendToAllPlayers(new ConsolePriorLinesPayload(getBlockPos(), getPriorLinesOrEmpty())); // TODO FOR TESTING ONLY
    }

    public void savePriorLines(String lines) {
        saved_lines = lines;
        if(saved_lines.length() > 8192) {
            saved_lines = saved_lines.substring(saved_lines.length() - 8192);
        }
        setChanged();
    }

    public String getPriorLinesOrEmpty() {
        return saved_lines;
    }

    @Override
    public boolean isActive() {
        return pattern_sensor.isActive();
    }

    @Override
    public void setActive(boolean active) {
        pattern_sensor.setActive(active);
    }

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            emitter.tick(slevel, this);
            pattern_sensor.tick(slevel, this);
            if(mode.equals(Mode.RAW_LISTEN)) {
                String raw_in = " " + (pattern_sensor.isActive() ? "" : ".") + (pattern_sensor.isActive() && pattern_sensor.getDelay() == 0 ? "*" : "") + (input() ? "1" : "0");
                saved_lines += raw_in;
                setChanged();
            }
        }
    }

    private enum Mode {
        PARSE,
        DEFINE,
        SET_DELAY,
        FORGET,
        RAW_LISTEN
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    private void setLooping(boolean looping) {
        emitter.looping = looping;
        if(!looping) {
            emitter.delay_points.clear();
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
