package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.SequenceBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

    // Create an update tag here, like above.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
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
