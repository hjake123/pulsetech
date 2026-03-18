package dev.hyperlynx.pulsetech.feature.datasheet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record Datasheet(Block block, List<DatasheetEntry> entries) {
    public static final Codec<Datasheet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Datasheet::block),
            DatasheetEntry.CODEC.listOf().fieldOf("entries").forGetter(Datasheet::entries)
    ).apply(instance, Datasheet::new));

    public static final StreamCodec<ByteBuf, Datasheet> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
