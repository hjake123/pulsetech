package dev.hyperlynx.pulsetech.feature.screen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class ScreenBlockEntity extends ProtocolBlockEntity {
    private ScreenData data = new ScreenData(getBlockPos(), Color.black());

    public ScreenBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.SCREEN.get(), pos, blockState);
    }

    /// Only used on the Server to send screen updates to clients, for example when a pulse code causes a change
    public void sendUpdate() {
        assert level != null;
        if (level.isClientSide()) {
            Pulsetech.LOGGER.error("Tried to send screen update from client! Ignoring.");
            return;
        }
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(getBlockPos()), data);
    }

    /// Only used on the Client to receive screen updates for rendering
    public void setScreenData(ScreenData screenData) {
        this.data = screenData;
    }

    public ScreenData getScreenData() {
        return data;
    }

    public void setBackgroundColor(Color bg_color) {
        this.data = data.withBackgroundColor(bg_color);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        data = ScreenData.CODEC.decode(NbtOps.INSTANCE, tag.get("Data")).getPartialOrThrow().getFirst();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Data", ScreenData.CODEC.encodeStart(NbtOps.INSTANCE, data).getPartialOrThrow());
    }
}
