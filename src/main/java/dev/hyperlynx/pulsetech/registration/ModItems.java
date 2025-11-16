package dev.hyperlynx.pulsetech.registration;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<BlockItem> CONSOLE = ITEMS.registerSimpleBlockItem("console", ModBlocks.CONSOLE);
    public static final DeferredItem<BlockItem> PATTERN_DETECTOR = ITEMS.registerSimpleBlockItem("pattern_detector", ModBlocks.PATTERN_DETECTOR);
    public static final DeferredItem<BlockItem> PATTERN_EMITTER = ITEMS.registerSimpleBlockItem("pattern_emitter", ModBlocks.PATTERN_EMITTER);
    public static final DeferredItem<BlockItem> NUMBER_MONITOR = ITEMS.registerSimpleBlockItem("number_monitor", ModBlocks.NUMBER_MONITOR);
    public static final DeferredItem<BlockItem> NUMBER_EMITTER = ITEMS.registerSimpleBlockItem("number_emitter", ModBlocks.NUMBER_EMITTER);
    public static final DeferredItem<BlockItem> CONTROLLER = ITEMS.registerSimpleBlockItem("controller", ModBlocks.CONTROLLER);

}
