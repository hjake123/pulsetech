package dev.hyperlynx.pulsetech.feature.processor;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.program.*;
import dev.hyperlynx.pulsetech.core.protocol.*;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessorBlockEntity extends ProtocolBlockEntity implements ProgramExecutor  {
    private static final Codec<Map<String, List<String>>> MACRO_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf());

    // Saved in data tags
    private Macros macros = new Macros(new HashMap<>());
    private ProgramEmitterModule emitter = new ProgramEmitterModule();
    private CommandMode command_mode = CommandMode.PARSE;
    private OperationMode operation_mode = OperationMode.OUTPUT;

    // Recomputed when needed
    private Protocol computed_protocol;

    public ProcessorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PROCESSOR.get(), pos, blockState);
    }

    public void setMacros(Macros macros) {
        computed_protocol = null;
        this.macros = macros;
    }

    @Override
    public @Nullable Protocol fetchProtocol() {
        if(computed_protocol == null) {
            // Compute a protocol from the given macros.
            int needed_bits = Math.toIntExact(Math.round(Math.ceil(Math.sqrt(macros.macros().size()))));
            var builder = ProtocolBuilder.builder(needed_bits);
            for(String key : macros.macros().keySet()) {
                int parameter_count = Math.toIntExact(macros.macros().get(key).stream().filter(token -> token.equals("?")).count());
                builder.add(() -> new MacroProtocolCommand(parameter_count, key));
            }
            computed_protocol = builder.build();
        }
        return computed_protocol;
    }

    @Override
    public Macros getMacros() {
        return macros;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void setOperationMode(OperationMode operationMode) {
        operation_mode = operationMode;
    }

    @Override
    public void tick() {
        super.tick();
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
            }
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
    public Datasheet getDatasheet() {
        return new Datasheet(getBlockState().getBlock(),
                fetchProtocol().getCommands().entrySet().stream().map(entry -> {
                    if(entry.getKey() instanceof MacroProtocolCommand mcommand) {
                        Sequence command_sequence = entry.getValue();
                        return new DatasheetEntry(
                                Component.literal(mcommand.macro()),
                                Component.literal(macros.macros().get(mcommand.macro()).stream().reduce((a, b) -> a + " " + b).orElse("??MISSING??")),
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
        if(!macros.macros().isEmpty()) {
            MACRO_CODEC.encodeStart(NbtOps.INSTANCE, macros.macros()).ifSuccess(encoded -> tag.put("macros", encoded));
        }
        tag.putString("OperationMode", operation_mode.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var decode_result = ProgramEmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("ProgramEmitter"));
        if(decode_result.hasResultOrPartial()) {
            emitter = decode_result.getPartialOrThrow().getFirst();
        }
        if(tag.contains("macros")) {
            MACRO_CODEC.decode(NbtOps.INSTANCE, tag.get("macros")).ifSuccess(pair -> macros = new Macros(new HashMap<>(pair.getFirst())));
        }
        if(tag.contains("OperationMode")) {
            operation_mode = OperationMode.valueOf(tag.getString("OperationMode"));
        }
    }
}
