package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// A C -> S packet that just sends a [StorageModemGUIPayload] to the Client who asks for it.
public record StorageModemFiltersRequest(BlockPos pos) implements CustomPacketPayload {
    public static final Type<StorageModemFiltersRequest> TYPE = new Type<>(Pulsetech.location("storage_modem_filters_request"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, StorageModemFiltersRequest> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StorageModemFiltersRequest::pos,
            StorageModemFiltersRequest::new
    );

    public void serverHandler(IPayloadContext context) {
        if(context.player().level().getBlockEntity(pos) instanceof StorageModemBlockEntity modem) {
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new StorageModemGUIPayload(pos, modem.getFilters(), modem.isGUISyncNeeded()));
        }
    }
}