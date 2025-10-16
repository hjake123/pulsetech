package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.block.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.PatternDetectorModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class PatternDetectorBlockEntity extends PatternBlockEntity {
    private PatternDetectorModule detector = new PatternDetectorModule();

    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
    }

    @Override
    public boolean isActive() {
        return detector.isActive();
    }

    @Override
    public void setActive(boolean active) {
        detector.setActive(active);
    }

    @Override
    public void tick() {
        if(!(level instanceof ServerLevel slevel)) {
            return;
        }
        detector.tick(slevel, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Detector", PatternDetectorModule.CODEC.encodeStart(NbtOps.INSTANCE, detector).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        PatternDetectorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Detector")).ifSuccess(success -> detector = success.getFirst());
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
