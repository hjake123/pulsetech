package dev.hyperlynx.pulsetech.pulse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.util.MapListPairConverter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProtocolData extends SavedData {
    private final Map<UUID, ResourceLocation> default_protocol_by_player;
    private static final MapListPairConverter<UUID, ResourceLocation> converter = new MapListPairConverter<>();

    public static final Codec<ProtocolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.pair(
                            UUIDUtil.CODEC.fieldOf("uuid").codec(),
                            ResourceLocation.CODEC.fieldOf("default").codec()
                    ).listOf().xmap(
                            converter::toMap,
                            converter::fromMap
                    ).fieldOf("default_protocol_by_player").forGetter(ProtocolData::getDefaults)
            ).apply(instance, ProtocolData::new)
    );

    private ProtocolData(Map<UUID, ResourceLocation> default_protocol_by_player) {
        this.default_protocol_by_player = new HashMap<>(default_protocol_by_player);
    }

    private Map<UUID, ResourceLocation> getDefaults() {
        return default_protocol_by_player;
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

    public @NotNull ResourceLocation getDefaultFor(Player player) {
        return default_protocol_by_player.getOrDefault(player.getUUID(), Pulsetech.location("error"));
    }

    public void setDefaultFor(ServerPlayer player, ResourceLocation id) {
        default_protocol_by_player.put(player.getUUID(), id);
        setDirty();
    }
}
