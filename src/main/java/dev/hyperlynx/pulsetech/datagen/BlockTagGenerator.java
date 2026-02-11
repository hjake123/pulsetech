package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Pulsetech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        ModBlocks.CANNON.getKey(),
                        ModBlocks.CONTROLLER.getKey(),
                        ModBlocks.SCANNER.getKey(),
                        ModBlocks.ORB.getKey(),
                        ModBlocks.SCREEN.getKey(),
                        ModBlocks.SCOPE.getKey(),
                        ModBlocks.CONSOLE.getKey(),
                        ModBlocks.RED_CONSOLE.getKey(),
                        ModBlocks.GREEN_CONSOLE.getKey(),
                        ModBlocks.INDIGO_CONSOLE.getKey(),
                        ModBlocks.WHITE_CONSOLE.getKey()
                );
    }
}
