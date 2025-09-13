package dev.hyperlynx.pulsetech.pulse;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ProtocolBlockEntity extends BlockEntity {
    private Protocol protocol = null;
    private final Sequence input_buffer = new Sequence();
    protected int input_delay_timer = 0;

    // abstract @Nullable Consumer<? super SequenceMatchingBlockEntity> getVerb(Glyph glyph);
    protected abstract boolean isReading();
    protected abstract void setReading(boolean reading);
    protected abstract boolean input();

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void clearInputBuffer() {
        input_buffer.clear();
    }

    public void delayInput(int amount) {
        input_delay_timer = amount;
    }

    public void tick() {
        assert level != null;
        if(level.isClientSide) {
            return;
        }
        if(isReading()){
            if(input_delay_timer > 0) {
                input_delay_timer--;
                return;
            }
            setReading(listen());
            delayInput(1);
        } else {
            if(input_buffer.length() > 0) {
                clearInputBuffer();
            }
        }
    }

    /// Scans for input and check against the input buffer against the sequences of the protocol.
    /// If a glyph is matched, it runs that glyph's verb. TODO: verbs not implemented
    ///
    /// Returns: true iff reading should continue
    private boolean listen() {
        if(protocol == null) {
            return false;
        }
        input_buffer.append(input());
        if(input_buffer.length() > protocol.sequenceLength()) {
            input_buffer.clear();
            Pulsetech.LOGGER.debug("No match found");
            return false;
        } else if (input_buffer.length() == protocol.sequenceLength()) {
            Pulsetech.LOGGER.debug("Checking for match with {}", input_buffer);
            Glyph glyph = protocol.glyphFor(input_buffer);
            if(glyph != null) {
                Pulsetech.LOGGER.debug("Matched glyph {}", glyph.id());
                return false;
            }
        }
        return true;
    }
}
