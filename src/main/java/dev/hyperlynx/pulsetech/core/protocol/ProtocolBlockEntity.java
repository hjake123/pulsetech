package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetProvider;
import dev.hyperlynx.pulsetech.feature.pattern.PatternSensorModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/// A block entity capable of hearing and responding to {@link ProtocolCommand}s.
public class ProtocolBlockEntity extends PulseBlockEntity implements DatasheetProvider {
    private ProtocolExecutorModule executor;
    private EmitterModule emitter = new EmitterModule();

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        executor = new ProtocolExecutorModule();
    }

    @Override
    public boolean isActive() {
        return executor.isActive();
    }

    @Override
    public void setActive(boolean active) {
        executor.setActive(active);
    }

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            executor.tick(slevel, this);
            emitter.tick(slevel, this);
        }
    }

    @Override
    public boolean isDelayed() {
        return executor.getDelay() > 0 || emitter.getDelay() > 0;
    }

    public void emitRaw(Sequence sequence) {
        emitter.enqueueWithoutHeader(sequence);
        emitter.setActive(true);
    }

    @Override
    public Datasheet getDatasheet() {
        return new Datasheet(getBlockState().getBlock(), Component.translatable("description.pulsetech." + getBlockState().getBlock().getDescriptionId()),
                executor.fetchProtocol(this).getCommands().entrySet().stream().map(entry -> {
                    ResourceLocation command_location = ProtocolCommands.REGISTRY.getKey(entry.getKey());
                    Sequence command_sequence = entry.getValue();
                    return new DatasheetEntry(
                            Component.translatable("protocol.pulsetech.name." + command_location.getPath()),
                            Component.translatable("protocol.pulsetech.description." + command_location.getPath()),
                            Component.translatable("protocol.pulsetech.parameters." + command_location.getPath()),
                            command_sequence
                            );
                }).toList());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Executor", ProtocolExecutorModule.CODEC.encodeStart(NbtOps.INSTANCE, executor).getOrThrow());
        tag.put("Emitter", EmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ProtocolExecutorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Executor")).ifSuccess(success -> executor = success.getFirst());
        EmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Emitter")).ifSuccess(success -> emitter = success.getFirst());
    }
}
