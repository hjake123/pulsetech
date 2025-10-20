package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.Protocol;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/// A {@link ProtocolBlockEntity} that contains a specific single pattern to listen for or produce
public abstract class PatternBlockEntity extends ProtocolBlockEntity {
    public PatternBlockEntity(BlockEntityType<? extends PatternBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    private String pattern = "";

    public void setPattern(String trigger) {
        this.pattern = trigger;
    }

    public String getPattern() {
        return pattern;
    }

    public void rotatePattern() {
        // TODO temporary logic
        pattern = getProtocol().nextKey(pattern);
        if(Objects.equals(pattern, Protocol.NUM)) {
            pattern = getProtocol().nextKey(pattern);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pattern = tag.getString("Trigger");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Trigger", pattern);
    }
}
