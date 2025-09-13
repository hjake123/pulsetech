package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.block.PatternDetectorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<PatternDetectorBlock> PATTERN_DETECTOR = BLOCKS.register("pattern_detector", () ->
            new PatternDetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));

}
