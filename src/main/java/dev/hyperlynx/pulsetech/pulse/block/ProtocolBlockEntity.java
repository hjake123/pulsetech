package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.module.EmitterModule;
import dev.hyperlynx.pulsetech.pulse.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.pulse.protocol.Parameter;
import dev.hyperlynx.pulsetech.pulse.protocol.ShortParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/// A block entity capable of hearing and responding to ProtocolCommands.
public class ProtocolBlockEntity extends PulseBlockEntity{
    private final NumberSensorModule number_sensor = new NumberSensorModule();
    private final EmitterModule emitter = new EmitterModule();

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean isActive() {
        return number_sensor.isActive();
    }

    @Override
    public void setActive(boolean active) {
        number_sensor.setActive(active);
    }

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            emitter.tick(slevel, this);
        }
    }

    public void emit(Sequence sequence) {
        emitter.enqueueTransmission(sequence);
    }

    public Parameter<Short> numberParameter() {
        return new ShortParameter(number_sensor);
    }
}
