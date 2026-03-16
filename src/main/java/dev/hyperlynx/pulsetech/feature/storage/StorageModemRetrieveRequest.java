package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/// A C -> S payload for emitting the retrieval command.
/// When received by the server, prompts the Storage Modem at the given position to emit a command sequence
/// to retrieve a chosen count of a chosen item.
public record StorageModemRetrieveRequest(BlockPos pos, byte filter_index, byte count) implements CustomPacketPayload {
    public static final Type<StorageModemRetrieveRequest> TYPE = new Type<>(Pulsetech.location("storage_modem_retrieve"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, StorageModemRetrieveRequest> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StorageModemRetrieveRequest::pos,
            ByteBufCodecs.BYTE, StorageModemRetrieveRequest::filter_index,
            ByteBufCodecs.BYTE, StorageModemRetrieveRequest::count,
            StorageModemRetrieveRequest::new
    );

    public void serverHandler(IPayloadContext context) {
        if(context.player().level().getBlockEntity(pos) instanceof StorageModemBlockEntity modem) {
            modem.sendRetrieveCommand(filter_index, count);
        }
    }
}
