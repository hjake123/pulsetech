package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoType;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/// A record that holds information about which [DebuggerInfoType]s a particular [DebuggerInfoSource] provides, and which integer ID to use to query each.
public record DebuggerInfoManifest(List<Entry> entries, BlockPos pos) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, DebuggerInfoManifest> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), DebuggerInfoManifest::entries,
            BlockPos.STREAM_CODEC, DebuggerInfoManifest::pos,
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
        for(Entry entry : entries) {
            builder.append(i).append(" = ").append(entry.title()).append(": ").append(DebuggerInfoTypes.REGISTRY.getKey(entry.type)).append("; ");
            i++;
        }
        return builder.toString();
    }

    public static class Entry {
        private String title;
        private DebuggerInfoType type;
        private Supplier<CustomPacketPayload> getter;

        static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Entry::title,
                ByteBufCodecs.registry(DebuggerInfoTypes.REGISTRY_KEY), Entry::type,
                Entry::new
        );

        public Entry(String title, DebuggerInfoType type) {
            this(title, type, () -> {
                throw new IllegalStateException("Cannot get S -> C payload from the client side!");
            });
        }

        public Entry(String title, DebuggerInfoType type, Supplier<CustomPacketPayload> getter) {
            this.title = title;
            this.type = type;
            this.getter = getter;
        }

        public String title() {
            return title;
        }

        public DebuggerInfoType type() {
            return type;
        }

        public  Supplier<CustomPacketPayload> payloadGetter() {
            return getter;
        }
    }
}
