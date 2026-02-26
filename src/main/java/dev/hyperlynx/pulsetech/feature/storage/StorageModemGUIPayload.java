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

/// An C <-> S payload for the Storage Modem's GUI. When sent to the client, it opens the relevant screen. When sent
/// to a server, it tells the Storage Modem block to persist the changes.
public record StorageModemGUIPayload(BlockPos pos, List<ItemFilter> filters) implements CustomPacketPayload {
    public static final Type<StorageModemGUIPayload> TYPE = new Type<>(Pulsetech.location("open_storage_modem_gui"));
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, StorageModemGUIPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StorageModemGUIPayload::pos,
            ByteBufCodecs.fromCodec(ItemFilter.CODEC).apply(ByteBufCodecs.list()), StorageModemGUIPayload::filters,
            StorageModemGUIPayload::new
    );

    public void clientHandler(IPayloadContext ignored) {
        ClientWrapper.openStorageModemScreen(pos, filters);
    }

    public void serverHandler(IPayloadContext context) {
        if(context.player().level().getBlockEntity(pos) instanceof StorageModemBlockEntity modem) {
            modem.setFilters(filters);
        }
    }
}
