package dev.hyperlynx.pulsetech.feature.number.bulb;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class NumberBulbBlock extends PulseBlock {
    public NumberBulbBlock(Properties properties, SideIO io) {
        super(properties, io, true);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(NumberBulbBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        // TODO: Try not providing a block entity if the block is an addon for stacking
        return new NumberBulbBlockEntity(blockPos, blockState);
    }

    public static int getStackSize(Level level, BlockPos pos) {
        // Yet to implement multi-number stacks.
        return 3;
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
                        bulb.operate((a, b) -> (byte) (a / b));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> REMAINDER = ProtocolCommands.COMMANDS.register("number_bulb/remainder", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof NumberBulbBlockEntity bulb) {
                        bulb.operate((a, b) -> (byte) (a % b));
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
