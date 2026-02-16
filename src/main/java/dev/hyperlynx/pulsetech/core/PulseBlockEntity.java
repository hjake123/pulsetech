package dev.hyperlynx.pulsetech.core;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PulseBlockEntity extends BlockEntity {
    boolean wake_triggered = false;

    public PulseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void output(boolean bit) {
        assert level != null;
        level.setBlock(getBlockPos(), getBlockState().setValue(PulseBlock.OUTPUT, bit), Block.UPDATE_CLIENTS);
        PulseBlock.updateOutputNeighbors(level, getBlockPos(), getBlockState());
    }

    public boolean input() {
        return PulseBlock.measureAllInputs(level, getBlockPos());
    }

    public abstract boolean isActive();

    public abstract void setActive(boolean active);

    public abstract void tick();

    public void handleInput() {}

    public abstract boolean isDelayed();
}
