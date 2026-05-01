package dev.hyperlynx.pulsetech.feature.number.bulb;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NumberBulbBlock extends PulseBlock {
    private static final BooleanProperty IS_STACK_ADDON = BooleanProperty.create("is_stack_addon");
    public static final IntegerProperty STORED_VALUES = IntegerProperty.create("count", 0, 2);

    public NumberBulbBlock(Properties properties, SideIO io) {
        super(properties, io, true);
        registerDefaultState(defaultBlockState().setValue(IS_STACK_ADDON, false).setValue(STORED_VALUES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_STACK_ADDON);
        builder.add(STORED_VALUES);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean is_atop_another_bulb = level.getBlockState(pos.below()).is(ModBlocks.NUMBER_BULB);
        if(is_atop_another_bulb != state.getValue(IS_STACK_ADDON)) {
            level.setBlock(pos, state.setValue(IS_STACK_ADDON, is_atop_another_bulb), Block.UPDATE_ALL);
            var entity = level.getBlockEntity(pos);
            if(entity != null) {
                entity.setRemoved();
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if(newState.is(ModBlocks.NUMBER_BULB)) {
            return;
        }
        // Scan down to find the Number Bulb block entity.
        BlockPos downward_cursor = pos.below();
        while(level.getBlockState(downward_cursor).is(ModBlocks.NUMBER_BULB)) {
            if(level.getBlockEntity(downward_cursor) instanceof NumberBulbBlockEntity bulb) {
                bulb.fitToSize((byte) getStackSize(level, downward_cursor));
                break;
            }
            downward_cursor = downward_cursor.below();
        }
        updateValueLights(level, pos.above(), 0);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if(state.getValue(IS_STACK_ADDON)) {
            return false;
        }
        return super.canConnectRedstone(state, level, pos, direction);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(IS_STACK_ADDON, context.getLevel().getBlockState(context.getClickedPos().below()).is(ModBlocks.NUMBER_BULB));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(NumberBulbBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if(blockState.getValue(IS_STACK_ADDON)) {
            return null;
        }
        return new NumberBulbBlockEntity(blockPos, blockState);
    }

    public static int getStackSize(Level level, BlockPos pos) {
        // Yet to implement multi-number stacks.
        int stack_size = -1;
        BlockPos upward_cursor = pos;
        while(level.getBlockState(upward_cursor).is(ModBlocks.NUMBER_BULB)) {
            upward_cursor = upward_cursor.above();
            stack_size += 2;
        }
        return stack_size;
    }

    public static void updateValueLights(Level level, BlockPos pos, int count) {
        int unlit_numbers = count;
        BlockPos upward_cursor = pos;
        while(level.getBlockState(upward_cursor).is(ModBlocks.NUMBER_BULB)) {
            BlockState state = level.getBlockState(upward_cursor);
            if(state.getValue(IS_STACK_ADDON)) {
                if(unlit_numbers >= 2) {
                    level.setBlock(upward_cursor, state.setValue(STORED_VALUES, 2), Block.UPDATE_ALL);
                    unlit_numbers -= 2;
                } else if(unlit_numbers == 1) {
                    level.setBlock(upward_cursor, state.setValue(STORED_VALUES, 1), Block.UPDATE_ALL);
                    unlit_numbers--;
                } else {
                    level.setBlock(upward_cursor, state.setValue(STORED_VALUES, 0), Block.UPDATE_ALL);
                }
            } else {
                level.setBlock(upward_cursor, state.setValue(STORED_VALUES, unlit_numbers > 0 ? 1 : 0), Block.UPDATE_ALL);
                unlit_numbers--;
            }
            upward_cursor = upward_cursor.above();
        }
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PUSH = ProtocolCommands.COMMANDS.register("number_bulb/push", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.push(context.params().getFirst());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> POP = ProtocolCommands.COMMANDS.register("number_bulb/pop", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.emit(Sequence.fromByte(bulb.pop()));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PEEK = ProtocolCommands.COMMANDS.register("number_bulb/peek", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.emit(Sequence.fromByte(bulb.peek()));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DROP = ProtocolCommands.COMMANDS.register("number_bulb/drop", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.dropCount(context.params().getFirst());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DUP = ProtocolCommands.COMMANDS.register("number_bulb/dup", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.dup();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SWAP = ProtocolCommands.COMMANDS.register("number_bulb/swap", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.swap();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> COUNT = ProtocolCommands.COMMANDS.register("number_bulb/count", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.emit(Sequence.fromByte(bulb.stackSize()));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> ADD = ProtocolCommands.COMMANDS.register("number_bulb/add", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a + b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SUB = ProtocolCommands.COMMANDS.register("number_bulb/sub", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a - b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MULT = ProtocolCommands.COMMANDS.register("number_bulb/mult", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a * b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DIV = ProtocolCommands.COMMANDS.register("number_bulb/div", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> {
                            if(b == 0) {
                                return (byte) 0;
                            }
                            return (byte) (a / b);
                        });
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> REMAINDER = ProtocolCommands.COMMANDS.register("number_bulb/remainder", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> {
                            if(b == 0) {
                                return (byte) a;
                            }
                            return (byte) (a % b);
                        });
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> LEFT_SHIFT = ProtocolCommands.COMMANDS.register("number_bulb/left_shift", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a << b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> RIGHT_SHIFT = ProtocolCommands.COMMANDS.register("number_bulb/right_shift", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a >> b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> IS_ZERO = ProtocolCommands.COMMANDS.register("number_bulb/is_zero", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.testTop(top -> top == 0);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> IS_NEGATIVE = ProtocolCommands.COMMANDS.register("number_bulb/is_negative", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.testTop(top -> top < 0);
                    }
                }
            });
}
