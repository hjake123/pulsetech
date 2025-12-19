package dev.hyperlynx.pulsetech.feature.datasheet;

import net.minecraft.world.level.block.Block;

import java.util.List;

public record Datasheet(Block block, List<DatasheetEntry> entries) {}
