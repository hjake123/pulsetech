package dev.hyperlynx.pulsetech.feature.pattern.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.feature.processor.ProcessorBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PatternEmitterBlock extends PatternBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16));

    public PatternEmitterBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.has(ModComponentTypes.MACROS.get())) {
            level.setBlock(pos, ModBlocks.PROCESSOR.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), Block.UPDATE_ALL_IMMEDIATE);
            if(level.getBlockEntity(pos) instanceof ProcessorBlockEntity processor) {
                processor.setMacros(stack.get(ModComponentTypes.MACROS));
            }
            stack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.PATTERN_EMITTER.get().create(pos, state);
    }

    public static final MapCodec<PatternEmitterBlock> CODEC = BlockBehaviour.simpleCodec(PatternEmitterBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
