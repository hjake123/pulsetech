package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.block.PatternDetectorBlock;
import dev.hyperlynx.pulsetech.pulse.Glyph;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PatternDetectorBlockEntity extends ProtocolBlockEntity {
    private boolean reading = false;

    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
        Protocol test_protocol = new Protocol(4);
        test_protocol.define(Protocol.ACK, new Sequence(false, false, false, false));
        test_protocol.define(Protocol.ERR, new Sequence(true, true, true, true));
        test_protocol.define(new Glyph("A"), new Sequence(true, false, true, false));
        this.setProtocol(test_protocol);
    }

    @Override
    public boolean isReading() {
        return reading;
    }

    @Override
    public void setReading(boolean reading) {
        this.reading = reading;
    }

    @Override
    protected boolean input() {
        assert level != null;
        return level.getDirectSignalTo(getBlockPos()) > 0;
    }
}
