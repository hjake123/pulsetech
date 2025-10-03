package dev.hyperlynx.pulsetech.pulse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/// A {@link SequenceBlockEntity} that also contains a particular {@link Protocol}
public abstract class ProtocolBlockEntity extends SequenceBlockEntity {
    protected Protocol protocol = null;

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
        setChanged();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(protocol != null) {
            tag.put("Protocol", Protocol.CODEC.encodeStart(NbtOps.INSTANCE, getProtocol()).getOrThrow());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("Protocol")) {
            setProtocol(Protocol.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("Protocol")).getOrThrow().getFirst());
        }
    }
}
