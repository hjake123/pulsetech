package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlockLootTableGenerator extends BlockLootSubProvider {

    protected BlockLootTableGenerator(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // The contents of our DeferredRegister.
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.CONSOLE.get());
        dropSelf(ModBlocks.WHITE_CONSOLE.get());
        dropSelf(ModBlocks.RED_CONSOLE.get());
        dropSelf(ModBlocks.GREEN_CONSOLE.get());
        dropSelf(ModBlocks.INDIGO_CONSOLE.get());
        dropSelf(ModBlocks.CANNON.get());
        dropSelf(ModBlocks.CONTROLLER.get());
        dropSelf(ModBlocks.SCOPE.get());
        dropSelf(ModBlocks.SCREEN.get());
        dropSelf(ModBlocks.NUMBER_MONITOR.get());
        dropSelf(ModBlocks.PATTERN_DETECTOR.get());
        dropSelf(ModBlocks.NUMBER_EMITTER.get());
        dropSelf(ModBlocks.PATTERN_EMITTER.get());
        dropSelf(ModBlocks.ORB.get());
        dropSelf(ModBlocks.SCANNER.get());
        dropOther(ModBlocks.PROCESSOR.get(), ModItems.PATTERN_EMITTER.asItem());

    }

}
