package dev.hyperlynx.pulsetech.feature.processor;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.core.program.ProgramInterpreter;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternDetectorBlock;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.hyperlynx.pulsetech.core.PulseBlock.FACING;

public class ProcessorBlock extends PulseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(5, 6, 5, 11, 12, 11));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(5, 6, 5, 11, 12, 11));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(5, 6, 5, 11, 12, 11));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(4, 2, 4, 12, 6, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(5, 6, 5, 11, 12, 11));

    public ProcessorBlock(Properties properties, SideIO io) {
        super(properties, io, true);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.PROCESSOR.get().create(pos, state);
    }

    public static final MapCodec<PatternDetectorBlock> CODEC = pulseCodec(PatternDetectorBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
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
        if(stack.isEmpty()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof ProcessorBlockEntity processor) {
            ItemStack data_cell = ModItems.DATA_CELL.toStack();
            data_cell.set(ModComponentTypes.MACROS, new Macros(processor.getMacros()));
            player.addItem(data_cell);
            level.setBlock(pos, ModBlocks.PATTERN_EMITTER.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var drops = new ArrayList<>(super.getDrops(state, params));
        if(params.getParameter(LootContextParams.BLOCK_ENTITY) instanceof ProcessorBlockEntity processor) {
            ItemStack data_cell = ModItems.DATA_CELL.toStack();
            data_cell.set(ModComponentTypes.MACROS, new Macros(processor.getMacros()));
            drops.add(data_cell);
        }
        return drops;
    }
}
