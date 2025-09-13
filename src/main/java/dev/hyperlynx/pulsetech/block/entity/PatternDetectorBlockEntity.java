package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PatternDetectorBlockEntity extends ProtocolBlockEntity {
    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
        Protocol test_protocol = new Protocol(4);
        test_protocol.define(Protocol.ACK, new Sequence(false, false, false, false));
        test_protocol.define(Protocol.ERR, new Sequence(true, true, true, true));
        test_protocol.define(new Glyph("A"), new Sequence(true, false, true, false));
        this.setProtocol(test_protocol);
    }

    protected boolean input() {
        assert level != null;
        return level.getDirectSignalTo(getBlockPos()) > 0;
    }

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        buffer.append(input());
        if(buffer.length() > protocol.sequenceLength()) {
            buffer.clear();
            Pulsetech.LOGGER.debug("No match found");
            output(false);
            return false;
        } else if (buffer.length() == protocol.sequenceLength()) {
            Pulsetech.LOGGER.debug("Checking for match with {}", buffer);
            Glyph glyph = protocol.glyphFor(buffer);
            if(glyph != null) {
                Pulsetech.LOGGER.debug("Matched glyph {}", glyph.id());
                output(glyph.id().equals("A"));
                return false;
            }
        }
        return true;
    }
}
