package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NumberMonitorBlockEntity extends ProtocolBlockEntity implements NumberKnower {
    private NumberSensorModule module = new NumberSensorModule();

    public NumberMonitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_MONITOR.get(), pos, blockState);
    }

    @Override
    public boolean isActive() {
        return module.isActive();
    }

    @Override
    public void setActive(boolean active) {
        module.setActive(active);
    }

    @Override
    public void tick() {
        if(!(level instanceof ServerLevel slevel)) {
            return;
        }
        module.tick(slevel, this);
    }

    @Override
    public void handleInput() {
        setChanged();
    }

    public short getNumber() {
        return module.getNumber();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NumberSensorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Sensor")).ifSuccess(success -> module = success.getFirst());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Sensor", NumberSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, module).getOrThrow());
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
    public void setChanged() {
        super.setChanged();
        if(level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
    }
}
