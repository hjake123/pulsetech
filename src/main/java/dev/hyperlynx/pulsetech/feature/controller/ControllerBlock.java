package dev.hyperlynx.pulsetech.feature.controller;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ControllerBlock extends PulseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_X = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 9, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(1, 2, 1, 3, 3, 3), Block.box(1, 2, 13, 3, 3, 15), Block.box(13, 2, 13, 15, 3, 15), Block.box(13, 2, 1, 15, 3, 3));
    protected static final VoxelShape SHAPE_Z = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 9, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(1, 2, 1, 3, 3, 3), Block.box(13, 2, 1, 15, 3, 3), Block.box(13, 2, 13, 15, 3, 15), Block.box(1, 2, 13, 3, 3, 15));

    public ControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(ControllerBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProtocolBlockEntity(ModBlockEntityTypes.CONTROLLER.get(), blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> ON = ProtocolCommands.COMMANDS.register("controller/on", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().output(true);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> OFF = ProtocolCommands.COMMANDS.register("controller/off", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().output(false);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PULSE = ProtocolCommands.COMMANDS.register("controller/pulse", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().emitRaw(new Sequence(true));
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> LOOP_PULSE = ProtocolCommands.COMMANDS.register("controller/loop_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short loops = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < loops; i++) {
                        output.append(true);
                        output.append(false);
                    }
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DELAY_PULSE = ProtocolCommands.COMMANDS.register("controller/delay_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short ticks = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < ticks; i++) {
                        output.append(false);
                    }
                    output.append(true);
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TIMED_PULSE = ProtocolCommands.COMMANDS.register("controller/timed_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short ticks = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < ticks; i++) {
                        output.append(true);
                    }
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> LOOP_DELAY_PULSE = ProtocolCommands.COMMANDS.register("controller/loop_delay_pulse", () ->
            new ProtocolCommand(2) {
                @Override
                public void run(ExecutionContext context) {
                    short ticks = context.params().getFirst();
                    short delay_length = context.params().get(1);
                    Sequence output = new Sequence();
                    for(int i = 0; i < ticks; i++) {
                        output.append(true);
                        if(i != (ticks-1)) {
                            for(int j = 0; j < delay_length; j++) {
                                output.append(false);
                            }
                        }
                    }
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> RANDOMS = ProtocolCommands.COMMANDS.register("controller/randoms", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short loops = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < loops; i++) {
                        assert context.block().getLevel() != null;
                        output.append(context.block().getLevel().random.nextBoolean());
                    }
                    context.block().emitRaw(output);
                }
            });
}
