package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.block.*;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlock;
import dev.hyperlynx.pulsetech.pulse.block.SequenceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<SequenceBlock> PATTERN_DETECTOR = BLOCKS.register("pattern_detector", () ->
            new PatternDetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<SequenceBlock> PATTERN_EMITTER = BLOCKS.register("pattern_emitter", () ->
            new PatternEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<PulseBlock> NUMBER_EMITTER = BLOCKS.register("number_emitter", () ->
            new NumberEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<PulseBlock> NUMBER_MONITOR = BLOCKS.register("number_monitor", () ->
            new NumberMonitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<ConsoleBlock> CONSOLE = BLOCKS.register("console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK)));
}
