package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class PatternDetectorBlockEntity extends ProtocolBlockEntity {
    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
    }

    private String trigger;

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getTrigger() {
        return trigger;
    }

    public void rotateTrigger() {
        // TODO temporary logic
        trigger = protocol.nextKey(trigger);
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
            String key = protocol.keyFor(buffer);
            if(key != null) {
                Pulsetech.LOGGER.debug("Matched pattern with key {}", key);
                output(key.equals(trigger));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        trigger = tag.getString("Trigger");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Trigger", trigger);
    }
}
