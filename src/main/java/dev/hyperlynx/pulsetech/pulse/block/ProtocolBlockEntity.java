package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.ProtocolData;
import dev.hyperlynx.pulsetech.pulse.Protocols;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/// A {@link PulseBlockEntity} that also contains a particular {@link Protocol}
public abstract class ProtocolBlockEntity extends PulseBlockEntity {
    protected ResourceLocation protocol_id = null;

    public ProtocolBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Protocol getProtocol() {
        if(!(getLevel() instanceof ServerLevel slevel)) {
            throw new IllegalStateException("Can't retrieve protocol on the client!");
        }
        return level.registryAccess().registry(Protocols.KEY).get().get(protocol_id);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(protocol_id != null) {
            tag.put("Protocol", StringTag.valueOf(protocol_id.toString()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("Protocol")) {
            protocol_id = ResourceLocation.parse(tag.getString("Protocol"));
        }
    }

    public void setProtocol(@NotNull ResourceLocation id) {
        protocol_id = id;
        setChanged();
    }
}
