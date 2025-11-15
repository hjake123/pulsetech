package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.PatternHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SequenceBlockEntity extends PulseBlockEntity implements PatternHolder {
    protected Sequence trigger = new Sequence();

    public SequenceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Sequence getPattern() {
        return trigger;
    }

    @Override
    public void setPattern(Sequence sequence) {
        this.trigger = sequence;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(trigger.isEmpty()) {
            var attempt_encode = Sequence.CODEC.encodeStart(NbtOps.INSTANCE, trigger);
            if(attempt_encode.hasResultOrPartial()) {
                tag.put("TriggerSequence", attempt_encode.getPartialOrThrow());
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("TriggerSequence")) {
            var attempt_decode = Sequence.CODEC.decode(NbtOps.INSTANCE, tag.get("TriggerSequence"));
            if(attempt_decode.hasResultOrPartial()) {
                trigger = attempt_decode.getPartialOrThrow().getFirst();
            }
        }
    }
}
