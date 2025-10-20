package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.data.ProtocolData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/// A {@link PulseBlockEntity} that also contains a particular {@link Protocol}
public abstract class ProtocolBlockEntity extends PulseBlockEntity {
    protected String protocol_id = "";

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Protocol getProtocol() {
        if(!(getLevel() instanceof ServerLevel slevel)) {
            throw new IllegalStateException("Can't retrieve protocol on the client!");
        }
        return ProtocolData.retrieve(slevel).get(protocol_id);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(!protocol_id.isEmpty()) {
            tag.put("ProtocolId", StringTag.valueOf(protocol_id));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("ProtocolId")) {
            protocol_id = tag.getString("ProtocolId");
        }
    }

    public void setProtocol(String id) {
        protocol_id = id;
        setChanged();
    }
}
