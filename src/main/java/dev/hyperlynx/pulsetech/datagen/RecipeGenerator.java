package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.registration.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.PULSE_MODULE)
                .pattern(" g ")
                .pattern("cqc")
                .pattern(" r ")
                .define('r', Items.REDSTONE)
                .define('g', Items.GLOWSTONE_DUST)
                .define('c', Items.COPPER_INGOT)
                .define('q', Items.QUARTZ)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.PATTERN_DETECTOR)
                .pattern("   ")
                .pattern("cmr")
                .pattern("sss")
                .define('r', Items.REDSTONE)
                .define('s', Items.STONE)
                .define('c', Items.COPPER_INGOT)
                .define('m', ModItems.PULSE_MODULE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.PATTERN_EMITTER)
                .pattern("   ")
                .pattern("rmc")
                .pattern("sss")
                .define('r', Items.REDSTONE)
                .define('c', Items.COPPER_INGOT)
                .define('s', Items.STONE)
                .define('m', ModItems.PULSE_MODULE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.NUMBER_EMITTER)
                .pattern("cGc")
                .pattern("rmc")
                .pattern("sss")
                .define('r', Items.REDSTONE)
                .define('c', Items.COPPER_INGOT)
                .define('s', Items.STONE)
                .define('m', ModItems.PULSE_MODULE)
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.NUMBER_MONITOR)
                .pattern("cGc")
                .pattern("cmc")
                .pattern("sss")
                .define('c', Items.COPPER_INGOT)
                .define('s', Items.STONE)
                .define('m', ModItems.PULSE_MODULE)
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.PROTOCOL_MODULE)
                .requires(Items.COPPER_BULB)
                .requires(ModItems.PULSE_MODULE)
                .requires(ModItems.PULSE_MODULE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.DATASHEET)
                .requires(Items.PAPER)
                .requires(Items.BLACK_DYE)
                .requires(ModItems.DATA_CELL)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.CONTROLLER)
                .pattern("   ")
                .pattern("cMr")
                .pattern("sss")
                .define('r', Items.REDSTONE)
                .define('c', Items.COPPER_INGOT)
                .define('s', Items.STONE)
                .define('M', ModItems.PROTOCOL_MODULE)
                .unlockedBy("has_protocol_module", has(ModItems.PROTOCOL_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.CANNON)
                .pattern("C C")
                .pattern("iMi")
                .pattern("sss")
                .define('C', Items.COPPER_BLOCK)
                .define('i', Items.IRON_INGOT)
                .define('s', Items.STONE)
                .define('M', ModItems.PROTOCOL_MODULE)
                .unlockedBy("has_protocol_module", has(ModItems.PROTOCOL_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.SCANNER)
                .pattern("rLr")
                .pattern("iMi")
                .pattern("sss")
                .define('r', Items.REDSTONE)
                .define('L', Items.LIGHTNING_ROD)
                .define('i', Items.IRON_INGOT)
                .define('s', Items.STONE)
                .define('M', ModItems.PROTOCOL_MODULE)
                .unlockedBy("has_protocol_module", has(ModItems.PROTOCOL_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.ORB)
                .pattern("aQa")
                .pattern("gMg")
                .pattern("sss")
                .define('a', Items.AMETHYST_SHARD)
                .define('g', Items.GOLD_INGOT)
                .define('Q', Items.CHISELED_QUARTZ_BLOCK)
                .define('s', Items.STONE)
                .define('M', ModItems.PROTOCOL_MODULE)
                .unlockedBy("has_protocol_module", has(ModItems.PROTOCOL_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.SCREEN)
                .pattern("c  ")
                .pattern("gr ")
                .pattern("ccM")
                .define('r', Items.REDSTONE)
                .define('g', Items.TINTED_GLASS)
                .define('c', Items.COPPER_INGOT)
                .define('M', ModItems.PROTOCOL_MODULE)
                .unlockedBy("has_protocol_module", has(ModItems.PROTOCOL_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.SCOPE)
                .pattern("   ")
                .pattern("ccc")
                .pattern("gmr")
                .define('r', Items.REDSTONE)
                .define('g', Items.TINTED_GLASS)
                .define('c', Items.COPPER_INGOT)
                .define('m', ModItems.PULSE_MODULE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.DEBUGGER)
                .pattern("dc")
                .pattern("cg")
                .define('g', Items.TINTED_GLASS)
                .define('c', Items.COPPER_INGOT)
                .define('d', ModItems.DATA_CELL)
                .unlockedBy("has_cell", has(ModItems.DATA_CELL))
                .save(output);

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(Items.REDSTONE_BLOCK),
                RecipeCategory.REDSTONE,
                ModItems.DATA_CELL,
                0.5f,
                100
        ).unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output, "data_cell_blasting");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Items.REDSTONE_BLOCK),
                        RecipeCategory.REDSTONE,
                        ModItems.DATA_CELL,
                        0.5f,
                        200
                ).unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output, "data_cell_smelting");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.CONSOLE)
                .pattern("cdc")
                .pattern("gmr")
                .pattern("ccc")
                .define('r', Items.REDSTONE)
                .define('g', Items.TINTED_GLASS)
                .define('c', Items.COPPER_INGOT)
                .define('d', ModItems.DATA_CELL)
                .define('m', ModItems.PULSE_MODULE)
                .unlockedBy("has_module", has(ModItems.PULSE_MODULE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.GREEN_CONSOLE)
                .requires(ModItems.CONSOLE)
                .requires(Items.GREEN_DYE)
                .unlockedBy("has_console", has(ModItems.CONSOLE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.RED_CONSOLE)
                .requires(ModItems.CONSOLE)
                .requires(Items.RED_DYE)
                .unlockedBy("has_console", has(ModItems.CONSOLE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.INDIGO_CONSOLE)
                .requires(ModItems.CONSOLE)
                .requires(Items.BLUE_DYE)
                .requires(Items.PURPLE_DYE)
                .unlockedBy("has_console", has(ModItems.CONSOLE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.WHITE_CONSOLE)
                .requires(ModItems.CONSOLE)
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_console", has(ModItems.CONSOLE))
                .save(output);
    }
}
