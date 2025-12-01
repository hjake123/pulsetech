package dev.hyperlynx.pulsetech.feature.datasheet;

import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public record DatasheetEntry(Component name, Component description, @Nullable Sequence pattern) {}
