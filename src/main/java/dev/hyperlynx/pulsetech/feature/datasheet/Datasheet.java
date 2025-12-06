package dev.hyperlynx.pulsetech.feature.datasheet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public record Datasheet(Block block, Component block_description, List<DatasheetEntry> entries) {

    /// Return a new Datasheet with the given entry added to the end.
    public Datasheet append(DatasheetEntry entry) {
        List<DatasheetEntry> modifiable_entries = new ArrayList<>(entries);
        modifiable_entries.add(entry);
        return new Datasheet(block, block_description, modifiable_entries);
    }
}
