package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.module.NumberSensorModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/// A block entity capable of hearing and responding to {@link ProtocolCommand}s.
public class ProtocolBlockEntity extends PulseBlockEntity {
    private final ProtocolExecutorModule executor;
    private final NumberSensorModule number_sensor = new NumberSensorModule();
    private final EmitterModule emitter = new EmitterModule();
    public final List<ShortParameter> parameters; // TODO replace this with parameter logic inside of the Protocol Executor Module

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        parameters = new ArrayList<>();
        executor = new ProtocolExecutorModule(this);
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

    public void emit(Sequence sequence) {
        emitter.enqueueTransmission(sequence);
        emitter.setActive(true);
    }

    /// Set the number of required parameters.
    public void requireParameters(int count) {
        while(parameters.size() < count) {
            parameters.add(new ShortParameter(number_sensor));
        }
    }

    public ShortParameter getParameter(int index) {
        if(index > parameters.size()) {
            throw new RuntimeException("Requested a parameter beyond the maximum reserved count");
        }
        return parameters.get(index);
    }
}
