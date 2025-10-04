package dev.hyperlynx.pulsetech.pulse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/// A block entity which has a buffer (a {@link Sequence}) and the ability to input to or output from that buffer
/// Each tick while active, it will call its run() method.
/// When inactive, if its buffer contains any bits, it will call its reset() method.
/// Also implements synchronization on block updates.
public abstract class SequenceBlockEntity extends BlockEntity {
    protected Sequence buffer = new Sequence();
    protected int delay_timer = 0;
    private boolean active = false;

    /// Performs some action whenever this entity is in an active state.
    /// The boolean value is whether it should stop being active.
    /// For example, this might check for matches with the buffer, output the buffer, or anything else.
    protected abstract boolean run();

    public SequenceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void delay(int amount) {
        delay_timer += amount;
    }

    public void reset() {
        buffer.clear();
        output(false);
    }

    public void tick() {
        assert level != null;
        if(level.isClientSide) {
            return;
        }
        if(delay_timer > 0) {
            delay_timer--;
            return;
        }
        if(isActive()){
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", Sequence.CODEC.encodeStart(NbtOps.INSTANCE, buffer).getOrThrow());
        tag.putBoolean("Active", active);
        tag.putInt("DelayTimer", delay_timer);
    }

    protected boolean input() {
        assert level != null;
        return level.getDirectSignalTo(getBlockPos()) > 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        buffer = Sequence.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("Buffer")).getOrThrow().getFirst();
        active = tag.getBoolean("Active");
        delay_timer = tag.getInt("DelayTimer");
    }

}
