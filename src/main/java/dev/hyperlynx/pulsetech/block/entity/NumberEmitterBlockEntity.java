package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

public class NumberEmitterBlockEntity extends ProtocolBlockEntity implements NumberKnower {
    public NumberEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_EMITTER.get(), pos, blockState);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;
    private short number = 0;

    public void adjustNumber(int amount) {
        number = (short) Math.max(0, number + amount);
    }

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        if(!output_initialized) {
            output_cursor = 0;
            output_initialized = true;
            buffer = protocol.fromShort(number);
            output(true);
            delay(2);
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        return output_cursor < buffer.length();
    }

    @Override
    public void reset() {
        super.reset();
        output_initialized = false;
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
