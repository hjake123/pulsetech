package dev.hyperlynx.pulsetech.feature.processor;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.program.*;
import dev.hyperlynx.pulsetech.core.protocol.*;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProcessorBlockEntity extends ProtocolBlockEntity implements ProgramExecutor, DebuggerInfoSource {
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());
    private static final Codec<HashSet<String>> HIDDEN_MACRO_CODEC = Codec.list(Codec.STRING).xmap(HashSet::new, ArrayList::new);

    // Saved in data tags
    private Map<String, List<String>> macros = new HashMap<>();
    private ProgramEmitterModule emitter = new ProgramEmitterModule();
    private CommandMode command_mode = CommandMode.PARSE;
    private OperationMode operation_mode = OperationMode.OUTPUT;
    private int unwrap_count = 0;
    private HashSet<String> hidden_macros = new HashSet<>();

    // Recomputed when needed
    private Protocol computed_protocol;

    public ProcessorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PROCESSOR.get(), pos, blockState);
    }

    public void setMacros(Macros macros) {
        computed_protocol = null;
        this.macros = new HashMap<>(macros.macros());
        this.hidden_macros = new HashSet<>(macros.hidden_macros());
    }

    @Override
    public @Nullable Protocol fetchProtocol() {
        if(computed_protocol == null) {
            // Compute a protocol from the given macros.
            int needed_bits = Math.toIntExact(Math.round(Math.ceil(Math.sqrt(macros.size() - hidden_macros.size()))));
            var builder = ProtocolBuilder.builder(needed_bits);
            for(String key : macros.keySet().stream().filter(key -> !isHidden(key)).sorted().toList()) {
                int parameter_count = Math.toIntExact(macros.get(key).stream().filter(token -> token.equals("?")).count());
                builder.add(() -> new MacroProtocolCommand(parameter_count, key));
            }
            computed_protocol = builder.build();
        }
        return computed_protocol;
    }

    @Override
    public Map<String, List<String>> getMacros() {
        return macros;
    }

    @Override
    public HashSet<String> getHiddenMacros() {
        return hidden_macros;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(level instanceof ServerLevel slevel) {
            switch (operation_mode) {
                case OUTPUT -> {
                    emitter.looping = false;
                }
                case LOOP_OUTPUT -> {
                    emitter.looping = true;
                }
            }
            emitter.tick(slevel, this);
        }
    }

    @Override
    public void setCommandMode(CommandMode commandMode) {
        command_mode = commandMode;
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
    public void addMacro(String noun, List<String> definition) {
        ProgramExecutor.super.addMacro(noun, definition);
        computed_protocol = null;
        setChanged();
    }

    @Override
    public Datasheet getDatasheet() {
        return new Datasheet(getBlockState().getBlock(),
                fetchProtocol().getCommands().entrySet().stream().map(entry -> {
                    if(entry.getKey() instanceof MacroProtocolCommand mcommand) {
                        Sequence command_sequence = entry.getValue();
                        return new DatasheetEntry(
                                Component.literal(mcommand.macro()),
                                Component.literal(macros.get(mcommand.macro()).stream().reduce((a, b) -> a + " " + b).orElse("??MISSING??")),
                                Component.empty(),
                                command_sequence
                        );
                    }
                    return new DatasheetEntry(Component.literal("??INVALID??"), Component.empty(), Component.empty(), null);
                }).toList());
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var encode_result = ProgramEmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter);
        if(encode_result.hasResultOrPartial()) {
            tag.put("ProgramEmitter", encode_result.getPartialOrThrow());
        }
        if(!macros.isEmpty()) {
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros).ifSuccess(encoded -> tag.put("macros", encoded));
        }
        tag.putString("OperationMode", operation_mode.name());
        tag.putInt("UnwrapCount", unwrap_count);
        if(!hidden_macros.isEmpty()) {
            HIDDEN_MACRO_CODEC.encodeStart(NbtOps.INSTANCE, hidden_macros).ifSuccess(encoded -> tag.put("hidden_macros", encoded));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var decode_result = ProgramEmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("ProgramEmitter"));
        if(decode_result.hasResultOrPartial()) {
            emitter = decode_result.getPartialOrThrow().getFirst();
        }
        if(tag.contains("macros")) {
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new HashMap<>(pair.getFirst()));
        }
        if(tag.contains("OperationMode")) {
            operation_mode = OperationMode.valueOf(tag.getString("OperationMode"));
        }
        if(tag.contains("UnwrapCount")) {
            unwrap_count = tag.getInt("UnwrapCount");
        }
        if(tag.contains("hidden_macros")) {
            HIDDEN_MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("hidden_macros")).ifSuccess(pair -> hidden_macros = new HashSet<>(pair.getFirst()));
        }
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        var protocol_manifest_list = new ArrayList<>(super.getDebuggerInfoManifest().entries());
        protocol_manifest_list.removeLast();
        protocol_manifest_list.add(new DebuggerInfoManifest.Entry(
                Component.translatable("debugger.pulsetech.output_buffer").getString(),
                DebuggerInfoTypes.SEQUENCE.value(),
                () -> new DebuggerSequenceInfo(emitter.getBuffer())));
        return new DebuggerInfoManifest(protocol_manifest_list, getBlockPos());
    }
}
