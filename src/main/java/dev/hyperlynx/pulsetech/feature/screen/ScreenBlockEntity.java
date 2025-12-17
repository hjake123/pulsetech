package dev.hyperlynx.pulsetech.feature.screen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class ScreenBlockEntity extends ProtocolBlockEntity {
    private ScreenData data = ScreenData.blank();
    private Color pen_color = Color.white();

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
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(getBlockPos()), new ScreenUpdatePayload(data, getBlockPos()));
    }

    public void setScreenData(ScreenData screenData) {
        this.data = screenData;
    }

    public ScreenData getScreenData() {
        return data;
    }

    public void setBackgroundColor(Color bg_color) {
        this.data = data.withBackgroundColor(bg_color);
    }

    public void setPenColor(Color color) {
        this.pen_color = color;
    }

    public void drawPixel(byte x, byte y) {
        data.setPixel(pen_color, x, y);
    }

    public void drawBox(Byte x1, Byte y1, Byte x2, Byte y2) {
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                data.setPixel(pen_color, x, y);
            }
        }
    }

    public void clearForeground() {
        data.clearForeground();
    }

    public void toggleForeground() {
        data = data.toggleForegroundVisible();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        data = ScreenData.CODEC.decode(NbtOps.INSTANCE, tag.get("ScreenData")).getPartialOrThrow().getFirst();
        pen_color = Color.CODEC.decode(NbtOps.INSTANCE, tag.get("PenColor")).getPartialOrThrow().getFirst();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("ScreenData", ScreenData.CODEC.encodeStart(NbtOps.INSTANCE, data).getPartialOrThrow());
        tag.put("PenColor", Color.CODEC.encodeStart(NbtOps.INSTANCE, pen_color).getPartialOrThrow());
    }
    // Create an update tag here. For block entities with only a few fields, this can just call #saveAdditional.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    // Handle a received update tag here. The default implementation calls #loadAdditional here,

    // so you do not need to override this method if you don't plan to do anything beyond that.

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }
}
