package dev.hyperlynx.pulsetech.feature.screen;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerBlockEntity;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ScreenBlock extends PulseBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 0, 0, 2, 2, 16), Block.box(0, 2, 0, 2, 14, 2), Block.box(0, 2, 14, 2, 14, 16), Block.box(0, 14, 0, 2, 16, 16), Block.box(14, 0, 0, 16, 2, 16), Block.box(2, 0, 14, 14, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(1, 2, 2, 6, 14, 14), Block.box(2, 2, 0, 6, 6, 2), Block.box(2, 2, 14, 6, 6, 16));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 2, 0, 2, 14, 2), Block.box(14, 2, 0, 16, 14, 2), Block.box(0, 14, 0, 16, 16, 2), Block.box(0, 0, 14, 16, 2, 16), Block.box(14, 0, 2, 16, 2, 14), Block.box(0, 0, 2, 2, 2, 14), Block.box(2, 2, 1, 14, 14, 6), Block.box(0, 2, 2, 2, 6, 6), Block.box(14, 2, 2, 16, 6, 6));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(14, 0, 0, 16, 2, 16), Block.box(14, 2, 14, 16, 14, 16), Block.box(14, 2, 0, 16, 14, 2), Block.box(14, 14, 0, 16, 16, 16), Block.box(0, 0, 0, 2, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(2, 0, 14, 14, 2, 16), Block.box(10, 2, 2, 15, 14, 14), Block.box(10, 2, 14, 14, 6, 16), Block.box(10, 2, 0, 14, 6, 2));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 0, 14, 16, 2, 16), Block.box(14, 2, 14, 16, 14, 16), Block.box(0, 2, 14, 2, 14, 16), Block.box(0, 14, 14, 16, 16, 16), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 0, 2, 2, 2, 14), Block.box(14, 0, 2, 16, 2, 14), Block.box(2, 2, 10, 14, 14, 15), Block.box(14, 2, 10, 16, 6, 14), Block.box(0, 2, 10, 2, 6, 14));

    public ScreenBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(ScreenBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch(state.getValue(FACING)) {
            case SOUTH -> SHAPE_NORTH;
            case WEST -> SHAPE_EAST;
            case EAST -> SHAPE_WEST;
            default -> SHAPE_SOUTH;
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ScreenBlockEntity(blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> BG = ProtocolCommands.COMMANDS.register("screen/bg", () ->
            new ProtocolCommand(3) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.setBackgroundColor(new Color(Byte.toUnsignedInt(context.params().getFirst()), Byte.toUnsignedInt(context.params().get(1)), Byte.toUnsignedInt(context.params().get(2))));
                        screen.sendUpdate();
                    }
                }
            });

}
