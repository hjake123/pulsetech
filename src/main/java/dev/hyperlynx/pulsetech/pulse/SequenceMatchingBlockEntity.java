package dev.hyperlynx.pulsetech.pulse;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class SequenceMatchingBlockEntity extends BlockEntity {
    private Protocol protocol = null;
    private final Sequence input_buffer = new Sequence();

    abstract @Nullable Consumer<? super SequenceMatchingBlockEntity> getVerb(Glyph glyph);
    abstract boolean isReading();
    abstract void setReading(boolean reading);

    public SequenceMatchingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
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

    public static void tick(Level level, BlockPos pos, BlockState state, SequenceMatchingBlockEntity matcher) {
        if(matcher.isReading()){
            matcher.setReading(matcher.listen());
        } else {
            if(matcher.input_buffer.size() > 0) {
                matcher.clearInputBuffer();
            }
        }
    }

    /// Scans for input and check against the input buffer against the sequences of the protocol.
    /// If a glyph is matched, it runs that glyph's verb. TODO: verbs not implemented
    ///
    /// Returns: true iff reading should continue
    public boolean listen() {
        if(protocol == null) {
            return false;
        }
        assert level != null;
        input_buffer.append(level.getDirectSignalTo(getBlockPos()) > 0);
        if(input_buffer.size() > protocol.maxSequenceLength()) {
            return false;
        }
        Glyph glyph = protocol.glyphFor(input_buffer);
        if(glyph != null) {
            Pulsetech.LOGGER.debug("Matched glyph {}", glyph.id());
            return false;
        }
        return true;
    }
}
