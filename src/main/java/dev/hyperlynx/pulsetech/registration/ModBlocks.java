package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.feature.cannon.CannonBlock;
import dev.hyperlynx.pulsetech.feature.console.ConsoleColor;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlock;
import dev.hyperlynx.pulsetech.feature.controller.ControllerBlock;
import dev.hyperlynx.pulsetech.feature.number.block.NumberEmitterBlock;
import dev.hyperlynx.pulsetech.feature.number.block.NumberMonitorBlock;
import dev.hyperlynx.pulsetech.feature.orb.OrbBlock;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternDetectorBlock;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternEmitterBlock;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternBlock;
import dev.hyperlynx.pulsetech.feature.processor.ProcessorBlock;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerBlock;
import dev.hyperlynx.pulsetech.feature.scope.ScopeBlock;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<PatternBlock> PATTERN_DETECTOR = BLOCKS.register("pattern_detector", () ->
            new PatternDetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<PatternBlock> PATTERN_EMITTER = BLOCKS.register("pattern_emitter", () ->
            new PatternEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<PulseBlock> NUMBER_EMITTER = BLOCKS.register("number_emitter", () ->
            new NumberEmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<PulseBlock> NUMBER_MONITOR = BLOCKS.register("number_monitor", () ->
            new NumberMonitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)));

    public static final DeferredBlock<ConsoleBlock> CONSOLE = BLOCKS.register("console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), ConsoleColor.AMBER));

    public static final DeferredBlock<ConsoleBlock> RED_CONSOLE = BLOCKS.register("red_console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), ConsoleColor.REDSTONE));

    public static final DeferredBlock<ConsoleBlock> GREEN_CONSOLE = BLOCKS.register("green_console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), ConsoleColor.GREEN));

    public static final DeferredBlock<ConsoleBlock> INDIGO_CONSOLE = BLOCKS.register("indigo_console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), ConsoleColor.INDIGO));

    public static final DeferredBlock<ConsoleBlock> WHITE_CONSOLE = BLOCKS.register("white_console", () ->
            new ConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), ConsoleColor.WHITE));

    public static final DeferredBlock<ControllerBlock> CONTROLLER = BLOCKS.register("controller", () ->
            new ControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredBlock<ScannerBlock> SCANNER = BLOCKS.register("scanner", () ->
            new ScannerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredBlock<ScopeBlock> SCOPE = BLOCKS.register("scope", () ->
            new ScopeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BULB).lightLevel(state -> 0)));

    public static final DeferredBlock<ScreenBlock> SCREEN = BLOCKS.register("screen", () ->
            new ScreenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_TRAPDOOR)));

    public static final DeferredBlock<CannonBlock> CANNON = BLOCKS.register("cannon", () ->
            new CannonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<OrbBlock> ORB = BLOCKS.register("orb", () ->
            new OrbBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)));

    public static final DeferredBlock<ProcessorBlock> PROCESSOR = BLOCKS.register("processor", () ->
            new ProcessorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COMPARATOR)));
}
