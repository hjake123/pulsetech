package dev.hyperlynx.pulsetech.feature.number.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NumberEmitterBlock extends PulseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public NumberEmitterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
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
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return InteractionResult.FAIL;
        }

        // Clicking without an item changes which pattern from the protocol is selected.
        if(level.getBlockEntity(pos) instanceof NumberEmitterBlockEntity emitter) {
            emitter.adjustNumber(player.isShiftKeyDown() ? -1 : 1);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.NUMBER_EMITTER.get().create(pos, state);
    }

    public static final MapCodec<PulseBlock> CODEC = simpleCodec(NumberEmitterBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
