package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class PatternDetectorBlockEntity extends ProtocolBlockEntity {
    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
    }

    private Glyph trigger;

    public void setTrigger(Glyph trigger) {
        this.trigger = trigger;
    }

    public Glyph getTrigger() {
        return trigger;
    }

    public void rotateTrigger(RandomSource random) {
        // TODO temporary logic
        trigger = protocol.randomGlyph(random);
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
                output(glyph.equals(trigger));
                return false;
            }
        }
        return true;
    }

}
