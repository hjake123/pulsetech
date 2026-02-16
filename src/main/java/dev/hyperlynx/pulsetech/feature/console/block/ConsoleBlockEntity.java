package dev.hyperlynx.pulsetech.feature.console.block;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.program.*;
import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetProvider;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class ConsoleBlockEntity extends PulseBlockEntity implements DatasheetProvider, ProgramExecutor, DebuggerInfoSource {
    ProgramEmitterModule emitter = new ProgramEmitterModule();
    private CommandMode command_mode = CommandMode.PARSE;
    private String saved_lines = "";

    private Map<String, List<String>> macros = new HashMap<>(); // Defined macros for this console. TODO better persistence?
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());
    private int unwrap_count = 0;
    private HashSet<String> hidden_macros = new HashSet<>();
    private static final Codec<HashSet<String>> HIDDEN_MACRO_CODEC = Codec.list(Codec.STRING).xmap(HashSet::new, ArrayList::new);

    public ConsoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONSOLE.get(), pos, blockState);
    }

    public Map<String, List<String>> getMacros() {
        return macros;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    public void addMacros(Map<String, List<String>> other_macros) {
        macros.putAll(other_macros);
    }

    public void processLine(String line, ServerPlayer player) {
        ProgramInterpreter.startProcessTokenList(this, Arrays.stream(line.split(" ")).toList(), player);
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
        var encode_result = ProgramEmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter);
        if(encode_result.hasResultOrPartial()) {
            tag.put("Emitter", encode_result.getPartialOrThrow());
        }
        if(!saved_lines.isEmpty()) {
            tag.putString("saved_lines", saved_lines);
        }
        if(!macros.isEmpty()) {
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros).ifSuccess(encoded -> tag.put("macros", encoded));
        }
        tag.putInt("UnwrapCount", unwrap_count);
        if(!hidden_macros.isEmpty()) {
            HIDDEN_MACRO_CODEC.encodeStart(NbtOps.INSTANCE, hidden_macros).ifSuccess(encoded -> tag.put("hidden_macros", encoded));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var decode_result = ProgramEmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Emitter"));
        if(decode_result.hasResultOrPartial()) {
            emitter = decode_result.getPartialOrThrow().getFirst();
        }
        if(tag.contains("saved_lines")) {
            saved_lines = tag.getString("saved_lines");
        }
        if(tag.contains("macros")) {
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new HashMap<>(pair.getFirst()));
        }
        if(tag.contains("hidden_macros")) {
            HIDDEN_MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("hidden_macros")).ifSuccess(pair -> hidden_macros = new HashSet<>(pair.getFirst()));
        }
        if(tag.contains("UnwrapCount")) {
            unwrap_count = tag.getInt("UnwrapCount");
        }
    }

    @Override
    public boolean isDelayed() {
        return emitter.getDelay() > 0;
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
        return false;
    }

    @Override
    public void setActive(boolean active) {
        // NO-OP
    }

    @Override
    public int getUnwrapCount() {
        return unwrap_count;
    }

    @Override
    public void incrementUnwrapCount() {
        unwrap_count++;
    }

    @Override
    public void resetUnwrapCount() {
        unwrap_count = 0;
    }

    @Override
    public boolean isHidden(String key) {
        return hidden_macros.contains(key);
    }

    @Override
    public Datasheet getDatasheet() {
        return new Datasheet(getBlockState().getBlock(), ProgramInterpreter.BUILT_IN_COMMANDS.keySet().stream().map(command ->
            new DatasheetEntry(
                    Component.literal(command),
                    Component.translatable("console.pulsetech.description." + command),
                    Component.translatable("console.pulsetech.parameters." + command),
                    null)
        ).toList());
    }

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            emitter.tick(slevel, this);
        }
    }

    public void setCommandMode(CommandMode command_mode) {
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
        // TODO use that to omit macros, since they can be used to DC the client if there are too many
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
    }

    @Override
    public CommandMode getCommandMode() {
        return command_mode;
    }

    @Override
    public ProgramEmitterModule getEmitter() {
        return emitter;
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        return new DebuggerInfoManifest(List.of(
                new DebuggerInfoManifest.Entry(
                        Component.translatable("debugger.pulsetech.output_buffer").getString(),
                        DebuggerInfoTypes.SEQUENCE.value(),
                        () -> new DebuggerSequenceInfo(emitter.getBuffer()))
        ), getBlockPos());
    }

    public HashSet<String> getHiddenMacros() {
        return hidden_macros;
    }
}
