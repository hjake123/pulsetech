package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/// A block entity capable of hearing and responding to {@link ProtocolCommand}s.
public class ProtocolBlockEntity extends PulseBlockEntity implements DatasheetProvider {
    private final ProtocolExecutorModule executor;
    private final EmitterModule emitter = new EmitterModule();

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
}
