package dev.hyperlynx.pulsetech.gametest;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.number.block.NumberMonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(Pulsetech.MODID)
public class Tests {

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

    @GameTest
    public static void number(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(40, () -> helper.succeedIf(
                () -> {
                    BlockEntity number_detector = helper.getBlockEntity(new BlockPos(6, 2, 0));
                    helper.assertTrue(number_detector instanceof NumberMonitorBlockEntity, "Monitor was not at (6, 2, 0). The test is malformed!");
                    assert number_detector instanceof NumberMonitorBlockEntity;
                    helper.assertTrue(((NumberMonitorBlockEntity) number_detector).getNumber() == 69, "Number was [" + ((NumberMonitorBlockEntity) number_detector).getNumber() + "], should be 69");
                }
        ));
    }

    @GameTest
    public static void numberRepeater(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(40, () -> helper.succeedIf(
                () -> {
                    BlockEntity number_detector = helper.getBlockEntity(new BlockPos(6, 2, 0));
                    helper.assertTrue(number_detector instanceof NumberMonitorBlockEntity, "Monitor was not at (6, 2, 0). The test is malformed!");
                    assert number_detector instanceof NumberMonitorBlockEntity;
                    helper.assertTrue(((NumberMonitorBlockEntity) number_detector).getNumber() == 69, "Number was [" + ((NumberMonitorBlockEntity) number_detector).getNumber() + "], should be 69");
                }
        ));
    }

}
