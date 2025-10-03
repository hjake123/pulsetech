package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.SequenceBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NumberMonitorBlockEntity extends SequenceBlockEntity implements NumberKnower {
    public NumberMonitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_MONITOR.get(), pos, blockState);
    }

    private short number = 0;

    @Override
    protected boolean run() {
        if(buffer.length() < 16) {
            number = 0;
            buffer.append(input());
            if(buffer.length() == 16) {
                number = buffer.toShort();
                assert getLevel() != null;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
            }
            return buffer.length() < 16;
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
    }

    public short getNumber() {
        return number;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        number = (short) tag.getInt("Number");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Number", number);
    }
}
