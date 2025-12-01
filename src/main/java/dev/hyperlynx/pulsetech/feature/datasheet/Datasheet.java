package dev.hyperlynx.pulsetech.feature.datasheet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record Datasheet(Block block, Component block_description, List<DatasheetEntry> entries) {}
