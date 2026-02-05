package dev.hyperlynx.pulsetech.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/// See {@link PulseBlockEntity}
public abstract class PulseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public final SideIO io;

    public PulseBlock(Properties properties, SideIO io) {
        super(properties.isRedstoneConductor((state, getter, pos) -> false));
        this.io = io;
        registerDefaultState(defaultBlockState().setValue(OUTPUT, false).setValue(FACING, Direction.NORTH));
    }

    public static <B extends PulseBlock> MapCodec<B> pulseCodec(BiFunction<Properties, SideIO, B> factory) {
        return RecordCodecBuilder.mapCodec((instance) -> instance.group
                (
                        propertiesCodec(),
                        SideIO.CODEC.fieldOf("io").forGetter(PulseBlock::getIOLayout)
                ).apply(instance, factory));
    }

    public SideIO getIOLayout() {
        return io;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OUTPUT);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if(direction == null) {
            return true;
        }
        return !io.isDisabled(direction, state);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if(io.isOutput(direction.getOpposite(), state)) {
            return 0;
        }
        return state.getValue(OUTPUT) ? 15 : 0;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(OUTPUT);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        Direction change_direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        assert change_direction != null;
        if(!io.isInput(change_direction.getOpposite(), state)) {
            return;
        }
        if(level.getDirectSignal(pos.relative(change_direction), change_direction) > 0 && level.getBlockEntity(pos) instanceof PulseBlockEntity entity && !entity.isActive() && !entity.isDelayed() && !entity.wake_triggered) {
            entity.wake_triggered = true;
            level.scheduleTick(pos, this, 3, TickPriority.VERY_HIGH);
        }
        if(level.getBlockEntity(pos) instanceof PulseBlockEntity entity) {
            entity.last_detected_input = level.getDirectSignalTo(pos) > 0;
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(level.getBlockEntity(pos) instanceof PulseBlockEntity entity) {
            entity.setActive(true);
            entity.wake_triggered = false;
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof PulseBlockEntity pulser)) {
            return 0;
        }
        return pulser.isActive() ? 15 : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (l, p, s, e) -> {
            if(e instanceof PulseBlockEntity entity) {
                entity.tick();
            }
        };
    }

    /// Please return the super if you want it to go to the default interaction!
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(PulsetechTags.preventNormalUsage)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public enum SideFunction implements StringRepresentable {
        INPUT,
        OUTPUT,
        DISABLED;

        @Override
        public @NotNull String getSerializedName() {
            return switch(this) {
                case INPUT -> "input";
                case OUTPUT -> "output";
                case DISABLED -> "disabled";
            };
        }

        public static final Codec<SideFunction> CODEC = StringRepresentable.fromEnum(SideFunction::values);
    }

    /// Defines which sides of the PulseBlock are for input, output, or do not connect. Use [SideFunction]s to populate.
    public record SideIO(SideFunction facingFunction, SideFunction leftFunction, SideFunction rightFunction, SideFunction backFunction) {
        public static final Codec<SideIO> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SideFunction.CODEC.fieldOf("facing").forGetter(SideIO::facingFunction),
                SideFunction.CODEC.fieldOf("left").forGetter(SideIO::leftFunction),
                SideFunction.CODEC.fieldOf("right").forGetter(SideIO::rightFunction),
                SideFunction.CODEC.fieldOf("back").forGetter(SideIO::backFunction))
                .apply(instance, SideIO::new));

        boolean isOutput(Direction side, BlockState state) {
            return sideMatches(side, state, SideFunction.OUTPUT);
        }

        boolean isInput(Direction side, BlockState state) {
            return sideMatches(side, state, SideFunction.INPUT);

        }

        boolean isDisabled(Direction side, BlockState state) {
            return sideMatches(side, state, SideFunction.DISABLED);
        }

        private boolean sideMatches(Direction side, BlockState state, SideFunction function) {
            Direction facing = state.getValue(FACING);
            if(facing.equals(side)) {
                return function.equals(facingFunction);
            } else if (facing.getClockWise(Direction.Axis.Y).equals(side)) {
                return function.equals(rightFunction);
            } else if (facing.getCounterClockWise(Direction.Axis.Y).equals(side)) {
                return function.equals(leftFunction);
            } else {
                return function.equals(backFunction);
            }
        }
    }

    public static final SideIO FRONT_OUT_OTHER_IN = new SideIO(SideFunction.OUTPUT, SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT);
    public static final SideIO FRONT_OUT_BACK_IN = new SideIO(SideFunction.OUTPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.INPUT);
    public static final SideIO ALL_SIDES_INPUT = new SideIO(SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT);
    public static final SideIO MAIN_OUTPUT_ONLY = new SideIO(SideFunction.OUTPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.DISABLED);
    public static final SideIO MAIN_INPUT_ONLY = new SideIO(SideFunction.INPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.DISABLED);
}
