package dev.hyperlynx.pulsetech.feature.scope;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.PatternHolder;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ScopeBlockEntity extends PulseBlockEntity implements PatternHolder {
    RawSensorModule module = new RawSensorModule();

    public ScopeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.SCOPE.get(), pos, blockState);
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
        if(getLevel() instanceof ServerLevel slevel) {
            module.tick(slevel, this);
            setChanged();
        }
    }

    @Override
    public boolean isDelayed() {
        return module.getDelay() > 0;
    }

    @Override
    public Sequence getPattern() {
        return module.getBuffer();
    }

    @Override
    public void setPattern(Sequence sequence) {
        Pulsetech.LOGGER.error("Can't set scope buffer like that!");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Scanner", RawSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, module).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        RawSensorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Scanner")).ifSuccess(success -> module = success.getFirst());
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
        // TODO use that to omit macros, since they can be used to DC the client if there are too many
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
    }
}
