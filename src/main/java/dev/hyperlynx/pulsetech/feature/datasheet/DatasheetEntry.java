package dev.hyperlynx.pulsetech.feature.datasheet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.util.TextComponentCodec;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public record DatasheetEntry(Component name, Component description, Component params, @Nullable Sequence pattern) implements Comparable<DatasheetEntry>{
    public static final Codec<DatasheetEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextComponentCodec.STRING_ONLY.fieldOf("name").forGetter(DatasheetEntry::name),
            TextComponentCodec.STRING_ONLY.fieldOf("description").forGetter(DatasheetEntry::description),
            TextComponentCodec.STRING_ONLY.fieldOf("params").forGetter(DatasheetEntry::params),
            Sequence.CODEC.optionalFieldOf("sequence").forGetter(DatasheetEntry::boxedPattern)
    ).apply(instance, DatasheetEntry::fromBoxed));

    private static DatasheetEntry fromBoxed(Component name, Component description, Component params, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Sequence> boxed_pattern) {
        return new DatasheetEntry(name, description, params, boxed_pattern.orElse(null));
    }

    private Optional<Sequence> boxedPattern() {
        return Optional.ofNullable(pattern);
    }

    @Override
    public int compareTo(@NotNull DatasheetEntry o) {
        if(pattern != null && o.pattern != null) {
            return pattern.compareTo(o.pattern);
        }
        return name.getString().compareTo(o.name.getString());
    }
}
