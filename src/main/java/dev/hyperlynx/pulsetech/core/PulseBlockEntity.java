package dev.hyperlynx.pulsetech.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PulseBlockEntity extends BlockEntity {
    public PulseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void output(boolean bit) {
        assert level != null;
        level.setBlock(getBlockPos(), getBlockState().setValue(PulseBlock.OUTPUT, bit), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }

    public boolean input() {
        assert level != null;
        return level.getDirectSignalTo(getBlockPos()) > 0;
    }

    public abstract boolean isActive();

    public abstract void setActive(boolean active);

    public abstract void tick();

    public void handleInput() {}
}
