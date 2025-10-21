package dev.hyperlynx.pulsetech.pulse.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProtocolData extends SavedData {
    private final Map<String, Protocol> protocols;
    private final Map<UUID, String> default_protocol_by_player;

    public static final Codec<ProtocolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, Protocol.CODEC).fieldOf("protocols").forGetter(ProtocolData::getProtocols),
                    Codec.pair(
                            UUIDUtil.CODEC.fieldOf("uuid").codec(),
                            Codec.STRING.fieldOf("default").codec()
                    ).listOf().xmap(
                            ProtocolData::toMap,
                            ProtocolData::fromMap
                    ).fieldOf("default_protocol_by_player").forGetter(ProtocolData::getDefaults)
            ).apply(instance, ProtocolData::new)
    );

    private static List<Pair<UUID, String>> fromMap(Map<UUID, String> map) {
        List<Pair<UUID, String>> entries = new ArrayList<>();
        for(UUID uuid : map.keySet()) {
            entries.add(Pair.of(uuid, map.get(uuid)));
        }
        return entries;
    }

    private static Map<UUID, String> toMap(List<Pair<UUID, String>> pairs) {
        Map<UUID, String> map = new HashMap<>();
        for(Pair<UUID, String> pair : pairs) {
            map.put(pair.getFirst(), pair.getSecond());
        }
        return map;
    }

    private ProtocolData(Map<String, Protocol> protocols, Map<UUID, String> default_protocol_by_player) {
        this.protocols = new HashMap<>(protocols);
        this.default_protocol_by_player = new HashMap<>(default_protocol_by_player);
    }

    private Map<String, Protocol> getProtocols() {
        return protocols;
    }

    private Map<UUID, String> getDefaults() {
        return default_protocol_by_player;
    }

    public static ProtocolData retrieve(ServerLevel level) {
        return Objects.requireNonNull(level.getServer().getLevel(ServerLevel.OVERWORLD)).getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(ProtocolData::empty, ProtocolData::load),
                        "pulsetech_protocols");
    }

    private static ProtocolData empty() {
        return new ProtocolData(new HashMap<>(), new HashMap<>());
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        var result = CODEC.encode(this, NbtOps.INSTANCE, tag);
        if(result.hasResultOrPartial() && result.getPartialOrThrow() instanceof CompoundTag encoded_tag) {
            if(result.isError()) {
                Pulsetech.LOGGER.error("Only partially saved protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
            }
            return encoded_tag;
        }
        Pulsetech.LOGGER.error("Couldn't save protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
        Pulsetech.LOGGER.error("The world's protocols will not persist! Please report this error to hyperlynx.");
        return tag;
    }

    public static ProtocolData load(CompoundTag tag, HolderLookup.Provider provider) {
        var result = CODEC.decode(NbtOps.INSTANCE, tag);
        if (result.hasResultOrPartial()) {
            if(result.isError()) {
                Pulsetech.LOGGER.error("Only partially loaded protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
            }
            return result.getPartialOrThrow().getFirst();
        }
        Pulsetech.LOGGER.error("Couldn't load protocol data: {}", result.error().orElse(new DataResult.Error<>(() -> "Unknown error", Optional.empty(), Lifecycle.experimental())));
        Pulsetech.LOGGER.error("The world's protocols will be lost! Please report this error to hyperlynx.");
        return empty();
    }

    public void add(String id, Protocol protocol) {
        protocols.put(id, protocol);
        setDirty();
    }

    public Protocol get(String id) {
        return protocols.get(id);
    }

    public @NotNull String getDefaultFor(Player player) {
        return default_protocol_by_player.getOrDefault(player.getUUID(), "");
    }

    public void setDefaultFor(ServerPlayer player, String id) {
        default_protocol_by_player.put(player.getUUID(), id);
        setDirty();
    }
}
