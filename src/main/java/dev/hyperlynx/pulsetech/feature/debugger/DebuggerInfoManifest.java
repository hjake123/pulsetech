package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// A record that holds information about which [DebuggerInfoType]s a particular [DebuggerInfoSource] provides, and which integer ID to use to query each.
public record DebuggerInfoManifest(List<DebuggerInfoType<?>> types) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, DebuggerInfoManifest> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(DebuggerInfoTypes.REGISTRY_KEY).apply(ByteBufCodecs.list()), DebuggerInfoManifest::types,
            DebuggerInfoManifest::new
    );

    public static final CustomPacketPayload.Type<DebuggerInfoManifest> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("debugger_info_manifest"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for(DebuggerInfoType<?> type : types) {
            builder.append(i).append(": ").append(DebuggerInfoTypes.REGISTRY.getKey(type));
            i++;
        }
        return builder.toString();
    }
}
