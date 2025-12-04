package dev.hyperlynx.pulsetech.feature.datasheet;

import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record DatasheetEntry(Component name, Component description, @Nullable Sequence pattern) implements Comparable<DatasheetEntry>{

    @Override
    public int compareTo(@NotNull DatasheetEntry o) {
        if(pattern != null && o.pattern != null) {
            return pattern.compareTo(o.pattern);
        }
        return name.getString().compareTo(o.name.getString());
    }
}
