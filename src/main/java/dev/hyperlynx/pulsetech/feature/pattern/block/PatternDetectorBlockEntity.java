package dev.hyperlynx.pulsetech.feature.pattern.block;

import dev.hyperlynx.pulsetech.core.PatternHolder;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.pattern.PatternSensorModule;
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

import java.util.List;

public class PatternDetectorBlockEntity extends PatternBlockEntity implements PatternHolder, DebuggerInfoSource {
    private PatternSensorModule detector = new PatternSensorModule();

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
    public void handleInput() {
        output(detector.bufferMatchesPattern());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Detector", PatternSensorModule.CODEC.encodeStart(NbtOps.INSTANCE, detector).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        PatternSensorModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Detector")).ifSuccess(success -> detector = success.getFirst());
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
    public void setPattern(Sequence sequence) {
        detector.setPattern(sequence);
        setChanged();
    }

    @Override
    public Sequence getPattern() {
        return detector.getPattern();
    }

    @Override
    public boolean isDelayed() {
        return detector.getDelay() > 0;
    }

    @Override
    public DebuggerInfoManifest debuggerInfoManifest() {
        return new DebuggerInfoManifest(List.of(DebuggerInfoTypes.SEQUENCE.value()));
    }
}
