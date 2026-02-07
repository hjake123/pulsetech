package dev.hyperlynx.pulsetech.feature.screen;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ScreenBlock extends PulseBlock {
    protected static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 0, 0, 2, 1, 16), Block.box(0, 1, 0, 2, 15, 1), Block.box(0, 1, 15, 2, 15, 16), Block.box(0, 15, 0, 2, 16, 16), Block.box(14, 0, 0, 16, 2, 16), Block.box(2, 0, 14, 14, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(1, 1, 1, 6, 15, 15), Block.box(2, 2, 0, 6, 6, 1), Block.box(2, 2, 15, 6, 6, 16));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 0, 0, 16, 1, 2), Block.box(0, 1, 0, 1, 15, 2), Block.box(15, 1, 0, 16, 15, 2), Block.box(0, 15, 0, 16, 16, 2), Block.box(0, 0, 14, 16, 2, 16), Block.box(14, 0, 2, 16, 2, 14), Block.box(0, 0, 2, 2, 2, 14), Block.box(1, 1, 1, 15, 15, 6), Block.box(0, 2, 2, 1, 6, 6), Block.box(15, 2, 2, 16, 6, 6));
    protected static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(14, 0, 0, 16, 1, 16), Block.box(14, 1, 15, 16, 15, 16), Block.box(14, 1, 0, 16, 15, 1), Block.box(14, 15, 0, 16, 16, 16), Block.box(0, 0, 0, 2, 2, 16), Block.box(2, 0, 0, 14, 2, 2), Block.box(2, 0, 14, 14, 2, 16), Block.box(10, 1, 1, 15, 15, 15), Block.box(10, 2, 15, 14, 6, 16), Block.box(10, 2, 0, 14, 6, 1));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 0, 14, 16, 1, 16), Block.box(15, 1, 14, 16, 15, 16), Block.box(0, 1, 14, 1, 15, 16), Block.box(0, 15, 14, 16, 16, 16), Block.box(0, 0, 0, 16, 2, 2), Block.box(0, 0, 2, 2, 2, 14), Block.box(14, 0, 2, 16, 2, 14), Block.box(1, 1, 10, 15, 15, 15), Block.box(15, 2, 10, 16, 6, 14), Block.box(0, 2, 10, 1, 6, 14));

    public ScreenBlock(Properties properties, SideIO io) {
        super(properties, io, true);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(ScreenBlock::new);
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (level.getBlockEntity(pos) instanceof ScreenBlockEntity screen && stack.is(ModItems.DATA_CELL)) {
            ScreenData data = screen.getScreenData();
            if (stack.has(ModComponentTypes.SCREEN_DATA)) {
                ScreenData to_be_applied_to_screen = stack.get(ModComponentTypes.SCREEN_DATA);
                if (screen.isNotBlank()) {
                    // If the screen has existing screen data, swap them if they're different.
                    if(data.equals(to_be_applied_to_screen)) {
                        return ItemInteractionResult.SUCCESS;
                    }
                    stack.set(ModComponentTypes.SCREEN_DATA, screen.getScreenData());
                    player.displayClientMessage(Component.translatable("pulsetech.screen_swapped"), true);
                }
                screen.setScreenData(to_be_applied_to_screen);
                screen.sendUpdate();
            } else if(screen.isNotBlank()) {
                stack.set(ModComponentTypes.SCREEN_DATA, data);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> CLEAR_BG = ProtocolCommands.COMMANDS.register("screen/clear_bg", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.setBackgroundColor(Color.black());
                        screen.sendUpdate();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PEN_COLOR = ProtocolCommands.COMMANDS.register("screen/pen_color", () ->
            new ProtocolCommand(3) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.setPenColor(new Color(Byte.toUnsignedInt(context.params().getFirst()), Byte.toUnsignedInt(context.params().get(1)), Byte.toUnsignedInt(context.params().get(2))));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> RESET_PEN_COLOR = ProtocolCommands.COMMANDS.register("screen/reset_pen_color", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.setPenColor(Color.white());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MARK = ProtocolCommands.COMMANDS.register("screen/mark", () ->
            new ProtocolCommand(2) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.drawPixel(context.params().getFirst(), context.params().get(1));
                        screen.sendUpdate();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> BOX = ProtocolCommands.COMMANDS.register("screen/box", () ->
            new ProtocolCommand(4) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.drawBox(context.params().getFirst(), context.params().get(1), context.params().get(2), context.params().get(3));
                        screen.sendUpdate();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TOGGLE_FG = ProtocolCommands.COMMANDS.register("screen/toggle_fg", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.toggleForeground();
                        screen.sendUpdate();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> CLEAR_FG = ProtocolCommands.COMMANDS.register("screen/clear_fg", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScreenBlockEntity screen) {
                        screen.clearForeground();
                        screen.sendUpdate();
                    }
                }
            });
}
