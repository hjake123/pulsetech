package dev.hyperlynx.pulsetech.gametest;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolExecutorModule;
import dev.hyperlynx.pulsetech.feature.cannon.CannonBlockEntity;
import dev.hyperlynx.pulsetech.feature.number.block.NumberMonitorBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(Pulsetech.MODID)
public class Tests {

    @GameTest(setupTicks = 5)
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

    @GameTest(setupTicks = 5)
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

    @GameTest(setupTicks = 5)
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

    @GameTest(setupTicks = 5)
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

    @GameTest(setupTicks = 5)
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

    @GameTest(setupTicks = 5)
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

    /// Tests for issue 1
    @GameTest(setupTicks = 5)
    public static void processorNumberIssue(GameTestHelper helper) {
        helper.setBlock(4, 3, 1, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(10, () -> helper.setBlock(4, 3, 1, Blocks.AIR));
        helper.runAfterDelay(100, () -> helper.succeedIf(
                () -> {
                    BlockEntity number_detector = helper.getBlockEntity(new BlockPos(1, 3, 2));
                    helper.assertTrue(number_detector instanceof NumberMonitorBlockEntity, "Monitor was not at (-4, 1, 3). The test is malformed!");
                    assert number_detector instanceof NumberMonitorBlockEntity;
                    helper.assertTrue(((NumberMonitorBlockEntity) number_detector).getNumber() == 1, "Number was [" + ((NumberMonitorBlockEntity) number_detector).getNumber() + "], should be 1");
                }
        ));
    }

    @GameTest(setupTicks = 5)
    public static void processorNumberIssueRepeater(GameTestHelper helper) {
        helper.setBlock(4, 3, 1, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(10, () -> helper.setBlock(4, 3, 1, Blocks.AIR));
        helper.runAfterDelay(100, () -> helper.succeedIf(
                () -> {
                    BlockEntity number_detector = helper.getBlockEntity(new BlockPos(1, 3, 2));
                    helper.assertTrue(number_detector instanceof NumberMonitorBlockEntity, "Monitor was not at (-4, 0, 3). The test is malformed!");
                    assert number_detector instanceof NumberMonitorBlockEntity;
                    helper.assertTrue(((NumberMonitorBlockEntity) number_detector).getNumber() == 1, "Number was [" + ((NumberMonitorBlockEntity) number_detector).getNumber() + "], should be 1");
                }
        ));
    }

    /// Tests for issue 32... I think
    @GameTest(setupTicks = 5, timeoutTicks = 200)
    public static void screenIssue(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(120, () -> helper.succeedWhen(
                () -> {
                    BlockEntity screen = helper.getBlockEntity(new BlockPos(8, 2, 0));
                    helper.assertTrue(screen instanceof ScreenBlockEntity, "Monitor was not at (8, 2, 0). The test is malformed!");
                    assert screen instanceof ScreenBlockEntity;
                    helper.assertTrue(((ScreenBlockEntity) screen).getScreenData().bg_color().equals(Color.white()), "Screen did not get set to white background");
                    helper.assertTrue(((ScreenBlockEntity) screen).getExecutionState().equals(ProtocolExecutorModule.State.AWAIT_COMMAND), "Screen execution state did not return to Await Command");
                }
        ));
    }

    @GameTest(setupTicks = 5, timeoutTicks = 200)
    public static void screenIssueWait(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(120, () -> helper.succeedWhen(
                () -> {
                    BlockEntity screen = helper.getBlockEntity(new BlockPos(8, 2, 0));
                    helper.assertTrue(screen instanceof ScreenBlockEntity, "Monitor was not at (8, 2, 0). The test is malformed!");
                    assert screen instanceof ScreenBlockEntity;
                    helper.assertTrue(((ScreenBlockEntity) screen).getScreenData().bg_color().equals(Color.white()), "Screen did not get set to white background");
                    helper.assertTrue(((ScreenBlockEntity) screen).getExecutionState().equals(ProtocolExecutorModule.State.AWAIT_COMMAND), "Screen execution state did not return to Await Command");
                }
        ));
    }

    @GameTest(setupTicks = 5, timeoutTicks = 40)
    public static void indirectSignal(GameTestHelper helper) {
        helper.setBlock(1, 2, 3, Blocks.REDSTONE_BLOCK);
        helper.runAfterDelay(20, () -> helper.succeedIf(
                () -> {
                    BlockState bulb = helper.getBlockState(new BlockPos(4, 2, 1));
                    helper.assertTrue(bulb.is(Blocks.WAXED_COPPER_BULB), "Bulb was not at (3, 2, 0). The test is malformed!");
                    helper.assertTrue(bulb.getValue(CopperBulbBlock.LIT), "Did not correctly detect the pattern");
                }
        ));
    }

    @GameTest(setupTicks = 5, timeoutTicks = 200)
    public static void junctionParams(GameTestHelper helper) {
        helper.setBlock(6, 3, 0, Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(
                () -> {
                    BlockEntity cannon = helper.getBlockEntity(new BlockPos(0, 3, 0));
                    helper.assertTrue(cannon instanceof CannonBlockEntity, "Subject was not at (0, 3, 0). The test is malformed!");
                    assert cannon instanceof CannonBlockEntity;
                    helper.assertTrue(((CannonBlockEntity) cannon).getExecutionState().equals(ProtocolExecutorModule.State.AWAIT_COMMAND), "Cannon execution state did not return to Await Command");
                    helper.assertTrue(((CannonBlockEntity) cannon).getTargetOffset().equals(new BlockPos(-5, 0, 0)), "Cannon never got the correct offset (was " + ((CannonBlockEntity) cannon).getTargetOffset() + ")");
                }
        );
    }

    @GameTest(setupTicks = 5, timeoutTicks = 200)
    public static void junctionParamsControl(GameTestHelper helper) {
        helper.setBlock(6, 3, 0, Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(
                () -> {
                    BlockEntity cannon = helper.getBlockEntity(new BlockPos(0, 3, 0));
                    helper.assertTrue(cannon instanceof CannonBlockEntity, "Subject was not at (0, 3, 0). The test is malformed!");
                    assert cannon instanceof CannonBlockEntity;
                    helper.assertTrue(((CannonBlockEntity) cannon).getExecutionState().equals(ProtocolExecutorModule.State.AWAIT_COMMAND), "Cannon execution state did not return to Await Command");
                    helper.assertTrue(((CannonBlockEntity) cannon).getTargetOffset().equals(new BlockPos(-5, 0, 0)), "Cannon never got the correct offset (was " + ((CannonBlockEntity) cannon).getTargetOffset() + ")");
                }
        );
    }

    @GameTest(setupTicks = 5)
    public static void controllerIsolateSides(GameTestHelper helper) {
        BlockPos wire_pos = new BlockPos(4, 3, 2);
        helper.pulseRedstone(new BlockPos(0, 3, 2), 5);
        helper.runAfterDelay(40, () -> {
            helper.assertRedstoneSignal(wire_pos, Direction.NORTH, i -> i > 0, () -> "Controller did not turn on when commanded");
            helper.pulseRedstone(new BlockPos(0, 3, 0), 5);
        });
        helper.runAfterDelay(80, () -> {
            helper.assertRedstoneSignal(wire_pos, Direction.NORTH, i -> i == 0, () -> "Controller did not turn off when commanded");
        });
        helper.runAfterDelay(82, helper::succeed);
    }

}
