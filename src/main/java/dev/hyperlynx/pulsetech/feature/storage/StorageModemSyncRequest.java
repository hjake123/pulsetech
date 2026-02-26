package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// A C -> S payload for managing filter syncing.
/// When received by the server, prompts the Storage Modem at the given position to begin synchronizing its filters with
/// attached Retrievers via [FilterSyncMan].
public record StorageModemSyncRequest(BlockPos pos) implements CustomPacketPayload {
    public static final Type<StorageModemSyncRequest> TYPE = new Type<>(Pulsetech.location("storage_modem_sync"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, StorageModemSyncRequest> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StorageModemSyncRequest::pos,
            StorageModemSyncRequest::new
    );

    public void serverHandler(IPayloadContext context) {
        if(context.player().level().getBlockEntity(pos) instanceof StorageModemBlockEntity modem) {
            modem.performFilterSync();
        }
    }
}
