package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.block.NumberEmitterBlock;
import dev.hyperlynx.pulsetech.block.PatternDetectorBlock;
import dev.hyperlynx.pulsetech.block.PatternEmitterBlock;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlock;
import dev.hyperlynx.pulsetech.pulse.SequenceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<ProtocolBlock> PATTERN_DETECTOR = BLOCKS.register("pattern_detector", () ->
            new PatternDetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<ProtocolBlock> PATTERN_EMITTER = BLOCKS.register("pattern_emitter", () ->
            new PatternEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<SequenceBlock> NUMBER_EMITTER = BLOCKS.register("number_emitter", () ->
            new NumberEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));
}
