package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class NumberMonitorBlockEntity extends ProtocolBlockEntity implements NumberKnower {
    public NumberMonitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_MONITOR.get(), pos, blockState);
    }

    private short number = 0;

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        if(buffer.length() < protocol.numberSequenceLength()) {
            number = 0;
            buffer.append(input());
            if(buffer.length() == protocol.sequenceLength()) {
                // We can now test for the NUM sequence. If it's absent, we need to fail out.
                if(!Objects.equals(protocol.sequenceFor(Protocol.NUM), buffer)) {
                    buffer.clear();
                    return false;
                }
            }
            if(buffer.length() == protocol.numberSequenceLength()) {
                number = protocol.toShort(buffer);
                assert getLevel() != null;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
            }
            return buffer.length() < protocol.numberSequenceLength();
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        number = 0;
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
}
