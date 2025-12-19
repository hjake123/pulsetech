package dev.hyperlynx.pulsetech.feature.scanner;

import net.minecraft.core.BlockPos;

/// Block Entities who operate using offsets from another block should implement this to enable them to be linked to the scanner, changing their offsets be based on its location instead.
public interface ScannerLinkable {
    /// Link this block to a scanner at the given position. Returns whether a valid scanner was found within the maximum range to this block.
    boolean setLinkedOrigin(BlockPos scanner_position);
}
