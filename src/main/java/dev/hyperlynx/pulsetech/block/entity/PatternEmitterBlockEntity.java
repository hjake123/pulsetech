package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PatternEmitterBlockEntity extends PatternBlockEntity {
    public PatternEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_EMITTER.get(), pos, blockState);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        if(!output_initialized) {
            buffer = protocol.sequenceFor(getPattern());
            if(buffer == null) {
                Pulsetech.LOGGER.error("No sequence for pattern {}. If this keeps repeating, report it!", getPattern());
                return true;
            }
            output_cursor = 0;
            output_initialized = true;
            output(true);
            return true;
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        output_initialized = false;
        return false;
    }
}
