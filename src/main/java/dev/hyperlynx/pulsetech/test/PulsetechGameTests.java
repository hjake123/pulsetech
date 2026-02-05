package dev.hyperlynx.pulsetech.test;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(Pulsetech.MODID)
public class PulsetechGameTests {

    @GameTest
    public static void patternBlockMatch(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(20, () -> helper.succeedIf(
                () -> {
                    BlockState bulb = helper.getBlockState(new BlockPos(7, 2, 0));
                    helper.assertTrue(bulb.is(Blocks.WAXED_COPPER_BULB), "Bulb was not at (7, 2, 0). The test is malformed!");
                    helper.assertTrue(bulb.getValue(CopperBulbBlock.LIT), "Did not correctly detect the pattern");
                }
        ));
    }

    @GameTest
    public static void patternBlockDifferent(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(20, () -> helper.succeedIf(
                () -> {
                    BlockState bulb = helper.getBlockState(new BlockPos(7, 2, 0));
                    helper.assertTrue(bulb.is(Blocks.WAXED_COPPER_BULB), "Bulb was not at (7, 2, 0). The test is malformed!");
                    helper.assertFalse(bulb.getValue(CopperBulbBlock.LIT), "Detected a mismatched pattern");
                }
        ));
    }

    @GameTest
    public static void patternBlockMatchRepeater(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(20, () -> helper.succeedIf(
                () -> {
                    BlockState bulb = helper.getBlockState(new BlockPos(7, 2, 0));
                    helper.assertTrue(bulb.is(Blocks.WAXED_COPPER_BULB), "Bulb was not at (7, 2, 0). The test is malformed!");
                    helper.assertTrue(bulb.getValue(CopperBulbBlock.LIT), "Did not correctly detect the pattern");
                }
        ));
    }

    @GameTest
    public static void patternBlockDifferentRepeater(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(20, () -> helper.succeedIf(
                () -> {
                    BlockState bulb = helper.getBlockState(new BlockPos(7, 2, 0));
                    helper.assertTrue(bulb.is(Blocks.WAXED_COPPER_BULB), "Bulb was not at (7, 2, 0). The test is malformed!");
                    helper.assertFalse(bulb.getValue(CopperBulbBlock.LIT), "Detected a mismatched pattern");
                }
        ));
    }

}
