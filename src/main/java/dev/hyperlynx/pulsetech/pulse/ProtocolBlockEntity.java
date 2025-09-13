package dev.hyperlynx.pulsetech.pulse;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ProtocolBlockEntity extends BlockEntity {
    protected Protocol protocol = null;
    protected Sequence buffer = new Sequence();
    protected int delay_timer = 0;
    private boolean active = false;

    /// Performs some action whenever this entity is in an active state.
    /// The boolean value is whether it should stop being active.
    /// For example, this might check for matches with the buffer, output the buffer, or anything else.
    protected abstract boolean run();

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void delay(int amount) {
        delay_timer = amount;
    }

    public void reset() {
        buffer.clear();
    }

    public void tick() {
        assert level != null;
        if(level.isClientSide) {
            return;
        }
        if(isActive()){
            if(delay_timer > 0) {
                delay_timer--;
                return;
            }
            setActive(run());
            delay(2);
        } else {
            if(buffer.length() > 0) {
                reset();
            }
        }
    }

    public void output(boolean bit) {
        assert level != null;
        level.setBlock(getBlockPos(), getBlockState().setValue(ProtocolBlock.OUTPUT, bit), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }
}
