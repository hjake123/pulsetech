package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PatternEmitterBlockEntity extends ProtocolBlockEntity {
    public PatternEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_EMITTER.get(), pos, blockState);
        Protocol test_protocol = new Protocol(4);
        test_protocol.define(Protocol.ACK, new Sequence(false, false, false, false));
        test_protocol.define(Protocol.ERR, new Sequence(true, true, true, true));
        test_protocol.define(new Glyph("A"), new Sequence(true, false, true, false));
        this.setProtocol(test_protocol);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        if(!output_initialized) {
            buffer = protocol.sequenceFor(new Glyph("A"));
            if(buffer == null) {
                throw new RuntimeException("ploopy");
            }
            output_cursor = 0;
            output_initialized = true;
            output(true);
            delay(1);
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

    @Override
    public void reset() {
        super.reset();
        output(false);
    }
}
