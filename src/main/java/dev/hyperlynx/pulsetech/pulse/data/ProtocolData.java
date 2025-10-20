package dev.hyperlynx.pulsetech.pulse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ProtocolData extends SavedData {
    private final Map<String, Protocol> protocols;

    public static final Codec<ProtocolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, Protocol.CODEC).fieldOf("protocols").forGetter(ProtocolData::getProtocols)
            ).apply(instance, ProtocolData::new)
    );

    private ProtocolData(Map<String, Protocol> protocols) {
        this.protocols = new HashMap<>(protocols);
    }

    private Map<String, Protocol> getProtocols() {
        return protocols;
    }

    public static ProtocolData retrieve(ServerLevel level) {
        return Objects.requireNonNull(level.getServer().getLevel(ServerLevel.OVERWORLD)).getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(ProtocolData::empty, ProtocolData::load),
                        "pulsetech_protocols");
    }

    private static ProtocolData empty() {
        return new ProtocolData(new HashMap<>());
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        var result = CODEC.encode(this, NbtOps.INSTANCE, tag);
        if(result.hasResultOrPartial() && result.getPartialOrThrow() instanceof CompoundTag encoded_tag) {
            return encoded_tag;
        }
        Pulsetech.LOGGER.error("Couldn't save protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
        Pulsetech.LOGGER.error("The world's protocols will not persist!\nPlease report this error to hyperlynx.");
        return tag;
    }

    public static ProtocolData load(CompoundTag tag, HolderLookup.Provider provider) {
        var result = CODEC.decode(NbtOps.INSTANCE, tag);
        if (result.hasResultOrPartial()) {
            return result.getPartialOrThrow().getFirst();
        }
        Pulsetech.LOGGER.error("Couldn't load protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
        Pulsetech.LOGGER.error("The world's protocols will be lost!\nPlease report this error to hyperlynx.");
        return empty();
    }

    public void add(String id, Protocol protocol) {
        protocols.put(id, protocol);
        setDirty();
    }

    public Protocol get(String id) {
        return protocols.get(id);
    }
}
