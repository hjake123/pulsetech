package dev.hyperlynx.pulsetech.feature.scope;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScopeBlock extends PulseBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 2, 0, 16, 10, 16), Block.box(14, 0, 14, 16, 2, 16), Block.box(0, 0, 0, 2, 2, 2), Block.box(0, 0, 14, 2, 2, 16), Block.box(14, 0, 0, 16, 2, 2), Block.box(15, 0, 6, 16, 2, 10), Block.box(7, 1, 7, 15, 2, 9));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 2, 0, 16, 10, 16), Block.box(14, 0, 14, 16, 2, 16), Block.box(0, 0, 0, 2, 2, 2), Block.box(14, 0, 0, 16, 2, 2), Block.box(0, 0, 14, 2, 2, 16), Block.box(6, 0, 15, 10, 2, 16), Block.box(7, 1, 7, 9, 2, 15));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(0, 2, 0, 16, 10, 16), Block.box(0, 0, 0, 2, 2, 2), Block.box(14, 0, 14, 16, 2, 16), Block.box(14, 0, 0, 16, 2, 2), Block.box(0, 0, 14, 2, 2, 16), Block.box(0, 0, 6, 1, 2, 10), Block.box(1, 1, 7, 9, 2, 9));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 2, 0, 16, 10, 16), Block.box(0, 0, 0, 2, 2, 2), Block.box(14, 0, 14, 16, 2, 16), Block.box(0, 0, 14, 2, 2, 16), Block.box(14, 0, 0, 16, 2, 2), Block.box(6, 0, 0, 10, 2, 1), Block.box(7, 1, 1, 9, 2, 9));

    public ScopeBlock(Properties properties, SideIO io) {
        super(properties, io, true);
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
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(ScopeBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ScopeBlockEntity(blockPos, blockState);
    }
}
