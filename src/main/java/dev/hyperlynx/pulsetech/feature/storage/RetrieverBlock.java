package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import dev.hyperlynx.pulsetech.registration.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetrieverBlock extends PulseBlock {
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(14, 0, 0, 16, 2, 16), Block.box(2, 0, 14, 14, 2, 16), Block.box(2, 0, 0, 14, 4, 4), Block.box(0, 0, 0, 2, 2, 16), Block.box(14, 14, 0, 16, 16, 16), Block.box(2, 14, 14, 14, 16, 16), Block.box(2, 14, 0, 14, 16, 2), Block.box(0, 14, 0, 2, 16, 16), Block.box(11, 0, 6, 12, 16, 10), Block.box(4, 0, 6, 5, 16, 10), Block.box(2, 14, 6, 4, 16, 10), Block.box(12, 14, 6, 14, 16, 10), Block.box(2, 0, 6, 4, 2, 10), Block.box(12, 0, 6, 14, 2, 10));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 14, 16, 2, 16), Block.box(14, 0, 2, 16, 2, 14), Block.box(0, 0, 2, 4, 4, 14), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 14, 14, 16, 16, 16), Block.box(14, 14, 2, 16, 16, 14), Block.box(0, 14, 2, 2, 16, 14), Block.box(0, 14, 0, 16, 16, 2), Block.box(6, 0, 11, 10, 16, 12), Block.box(6, 0, 4, 10, 16, 5), Block.box(6, 14, 2, 10, 16, 4), Block.box(6, 14, 12, 10, 16, 14), Block.box(6, 0, 2, 10, 2, 4), Block.box(6, 0, 12, 10, 2, 14));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 0, 2, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(2, 0, 12, 14, 4, 16), Block.box(14, 0, 0, 16, 2, 16), Block.box(0, 14, 0, 2, 16, 16), Block.box(2, 14, 0, 14, 16, 2), Block.box(2, 14, 14, 14, 16, 16), Block.box(14, 14, 0, 16, 16, 16), Block.box(4, 0, 6, 5, 16, 10), Block.box(11, 0, 6, 12, 16, 10), Block.box(12, 14, 6, 14, 16, 10), Block.box(2, 14, 6, 4, 16, 10), Block.box(12, 0, 6, 14, 2, 10), Block.box(2, 0, 6, 4, 2, 10));
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 0, 2, 2, 2, 14), Block.box(12, 0, 2, 16, 4, 14), Block.box(0, 0, 14, 16, 2, 16), Block.box(0, 14, 0, 16, 16, 2), Block.box(0, 14, 2, 2, 16, 14), Block.box(14, 14, 2, 16, 16, 14), Block.box(0, 14, 14, 16, 16, 16), Block.box(6, 0, 4, 10, 16, 5), Block.box(6, 0, 11, 10, 16, 12), Block.box(6, 14, 12, 10, 16, 14), Block.box(6, 14, 2, 10, 16, 4), Block.box(6, 0, 12, 10, 2, 14), Block.box(6, 0, 2, 10, 2, 4));

    protected static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(14, 0, 0, 16, 2, 16), Block.box(2, 0, 14, 14, 2, 16), Block.box(2, 0, 0, 14, 4, 4), Block.box(0, 0, 0, 2, 2, 16), Block.box(14, 14, 0, 16, 16, 16), Block.box(2, 14, 14, 14, 16, 16), Block.box(2, 14, 0, 14, 16, 2), Block.box(0, 14, 0, 2, 16, 16), Block.box(13, 0, 6, 14, 16, 10), Block.box(2, 0, 6, 3, 16, 10));
    protected static final VoxelShape SHAPE_OPEN_WEST = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 14, 16, 2, 16), Block.box(14, 0, 2, 16, 2, 14), Block.box(0, 0, 2, 4, 4, 14), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 14, 14, 16, 16, 16), Block.box(14, 14, 2, 16, 16, 14), Block.box(0, 14, 2, 2, 16, 14), Block.box(0, 14, 0, 16, 16, 2), Block.box(6, 0, 13, 10, 16, 14), Block.box(6, 0, 2, 10, 16, 3));
    protected static final VoxelShape SHAPE_OPEN_SOUTH = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 0, 2, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(2, 0, 12, 14, 4, 16), Block.box(14, 0, 0, 16, 2, 16), Block.box(0, 14, 0, 2, 16, 16), Block.box(2, 14, 0, 14, 16, 2), Block.box(2, 14, 14, 14, 16, 16), Block.box(14, 14, 0, 16, 16, 16), Block.box(2, 0, 6, 3, 16, 10), Block.box(13, 0, 6, 14, 16, 10));
    protected static final VoxelShape SHAPE_OPEN_EAST = Shapes.or(Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 0, 2, 2, 2, 14), Block.box(12, 0, 2, 16, 4, 14), Block.box(0, 0, 14, 16, 2, 16), Block.box(0, 14, 0, 16, 16, 2), Block.box(0, 14, 2, 2, 16, 14), Block.box(14, 14, 2, 16, 16, 14), Block.box(0, 14, 14, 16, 16, 16), Block.box(6, 0, 2, 10, 16, 3), Block.box(6, 0, 13, 10, 16, 14));

    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public RetrieverBlock(Properties properties, SideIO io) {
        super(properties, io, true);
        registerDefaultState(defaultBlockState().setValue(OPEN, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(RetrieverBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(OPEN)) {
            return switch(state.getValue(FACING)) {
                case SOUTH -> SHAPE_OPEN_SOUTH;
                case WEST -> SHAPE_OPEN_WEST;
                case EAST -> SHAPE_OPEN_EAST;
                default -> SHAPE_OPEN_NORTH;
            };
        }
        return switch(state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    public static boolean isOpen(BlockState state) {
        return state.getValue(OPEN);
    }

    public static void toggleOpen(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), Block.UPDATE_CLIENTS);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        toggleOpen(level, pos, state);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(ModItems.DATA_CELL) && stack.has(ModComponentTypes.ITEM_FILTERS) && level.getBlockEntity(pos) instanceof RetrieverBlockEntity retriever) {
            level.playSound(null, pos, ModSounds.BEEP.value(), SoundSource.PLAYERS, 1.0F, 1.0F + level.random.nextFloat() * 0.05F);
            List<ItemFilter> stored_filters = stack.get(ModComponentTypes.ITEM_FILTERS);
            if(retriever.getFilters().equals(stored_filters)) {
                player.displayClientMessage(Component.translatable("pulsetech.filters_already_match"), true);
                return ItemInteractionResult.FAIL;
            }
            retriever.setFilters(stack.get(ModComponentTypes.ITEM_FILTERS));
            player.displayClientMessage(Component.translatable("pulsetech.filters_updated"), true);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RetrieverBlockEntity(blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SYNC = ProtocolCommands.COMMANDS.register("retriever/sync", () ->
            new ProtocolCommand(2) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.syncFiltersUsingKey(context.params().getFirst(), context.params().get(1));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SELECT_FILTER = ProtocolCommands.COMMANDS.register("retriever/select_filter", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.selectFilter(context.params().getFirst());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> COUNT = ProtocolCommands.COMMANDS.register("retriever/count", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.emitMatchingCount();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> COUNT_STACKS = ProtocolCommands.COMMANDS.register("retriever/count_stacks", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.emitMatchingStackCount();
                    }
                }
            });


    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> RETRIEVE = ProtocolCommands.COMMANDS.register("retriever/retrieve", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.retrieveUpToCountMatching(context.params().getFirst());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TOGGLE_OPEN = ProtocolCommands.COMMANDS.register("retriever/toggle_open", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.toggleFreeFlowing();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DETECT_FILTER = ProtocolCommands.COMMANDS.register("retriever/detect_filter", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.detectFilter();
                    }
                }
            });
}
