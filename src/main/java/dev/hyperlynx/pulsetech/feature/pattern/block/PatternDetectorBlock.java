package dev.hyperlynx.pulsetech.feature.pattern.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PatternDetectorBlock extends PatternBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(6, 2, 2, 10, 3, 4), Block.box(12, 2, 6, 14, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(2, 2, 6, 4, 3, 10), Block.box(6, 2, 12, 10, 3, 14), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(6, 2, 2, 10, 3, 4), Block.box(6, 2, 12, 10, 3, 14), Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(2, 2, 6, 4, 3, 10), Block.box(12, 2, 6, 14, 3, 10), Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));

    public PatternDetectorBlock(BlockBehaviour.Properties properties, SideIO io) {
        super(properties, io, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch(state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.PATTERN_DETECTOR.get().create(pos, state);
    }

    public static final MapCodec<PatternDetectorBlock> CODEC = pulseCodec(PatternDetectorBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

}
