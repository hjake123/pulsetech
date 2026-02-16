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
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.BiFunction;

/// See {@link PulseBlockEntity}
public abstract class PulseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public final SideIO io;

    /// Defines whether this block should wait an extra tick after waking to remain synchronized with the output signal
    public final boolean pulse_input;

    public PulseBlock(Properties properties, SideIO io, boolean pulse_input) {
        super(properties.isRedstoneConductor((state, getter, pos) -> false));
        this.io = io;
        this.pulse_input = pulse_input;
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
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if(!io.isOutput(direction, state)) {
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
        if(!io.isInput(change_direction.getOpposite(), state) || neighborPos.equals(pos)) {
            return;
        }
        if(measureInput(level, pos, change_direction) && level.getBlockEntity(pos) instanceof PulseBlockEntity entity && !entity.isActive() && !entity.isDelayed() && !entity.wake_triggered) {
            entity.wake_triggered = true;
            level.scheduleTick(pos, this, pulse_input ? 4 : 3, TickPriority.VERY_HIGH);
            // If this block is a pulse input, we wait an additional tick so that, if this device ends up before the
            // output in the processing order, the output will always run a tick ahead of this input. This allows the
            // output to set its next bit before this block reads a stale signal.
            // This addresses issue #1, I hope.
        }
    }

    static boolean measureInput(Level level, BlockPos pos, Direction change_direction) {
        return level.getSignal(pos.relative(change_direction), change_direction) > 0;
    }

    public static boolean measureAllInputs(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if(block instanceof PulseBlock pulse_block) {
            return pulse_block.getIOLayout().measureInputSignal(level, pos, level.getBlockState(pos));
        }
        throw new AssertionError("Can't use PulseBlock methods on non PulseBlocks!");
    }

    public static void updateOutputNeighbors(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if(block instanceof PulseBlock pulse_block) {
            pulse_block.getIOLayout().updateOutputNeighbors(level, pos, state);
            return;
        }
        throw new AssertionError("Can't use PulseBlock methods on non PulseBlocks!");
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

        private boolean signalFromSide(Level level, BlockPos pos, BlockState state, Direction direction) {
            if(PulseBlock.measureInput(level, pos, direction.getOpposite())) {
                return isInput(direction, state);
            }
            return false;
        }

        public boolean measureInputSignal(Level level, BlockPos pos, BlockState state) {
            return signalFromSide(level, pos, state, Direction.NORTH) || signalFromSide(level, pos, state, Direction.SOUTH) || signalFromSide(level, pos, state, Direction.WEST) || signalFromSide(level, pos, state, Direction.EAST);
        }

        public void updateOutputNeighbors(Level level, BlockPos pos, BlockState state) {
            if(isOutput(Direction.NORTH, state)) {
                Direction direction = Direction.NORTH;
                BlockPos blockpos = pos.relative(direction.getOpposite());
                if (!EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), EnumSet.of(direction.getOpposite()), false).isCanceled()) {
                    level.neighborChanged(blockpos, state.getBlock(), pos);
                    level.updateNeighborsAtExceptFromFacing(blockpos, state.getBlock(), direction);
                }
            }
            if(isOutput(Direction.EAST, state)) {
                Direction direction = Direction.EAST;
                BlockPos blockpos = pos.relative(direction.getOpposite());
                if (!EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), EnumSet.of(direction.getOpposite()), false).isCanceled()) {
                    level.neighborChanged(blockpos, state.getBlock(), pos);
                    level.updateNeighborsAtExceptFromFacing(blockpos, state.getBlock(), direction);
                }
            }
            if(isOutput(Direction.SOUTH, state)) {
                Direction direction = Direction.SOUTH;
                BlockPos blockpos = pos.relative(direction.getOpposite());
                if (!EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), EnumSet.of(direction.getOpposite()), false).isCanceled()) {
                    level.neighborChanged(blockpos, state.getBlock(), pos);
                    level.updateNeighborsAtExceptFromFacing(blockpos, state.getBlock(), direction);
                }
            }
            if(isOutput(Direction.WEST, state)) {
                Direction direction = Direction.WEST;
                BlockPos blockpos = pos.relative(direction.getOpposite());
                if (!EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), EnumSet.of(direction.getOpposite()), false).isCanceled()) {
                    level.neighborChanged(blockpos, state.getBlock(), pos);
                    level.updateNeighborsAtExceptFromFacing(blockpos, state.getBlock(), direction);
                }
            }
        }
    }

    public static final SideIO FRONT_OUT_OTHER_IN = new SideIO(SideFunction.OUTPUT, SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT);
    public static final SideIO FRONT_OUT_BACK_IN = new SideIO(SideFunction.OUTPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.INPUT);
    public static final SideIO ALL_SIDES_INPUT = new SideIO(SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT, SideFunction.INPUT);
    public static final SideIO MAIN_OUTPUT_ONLY = new SideIO(SideFunction.OUTPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.DISABLED);
    public static final SideIO MAIN_INPUT_ONLY = new SideIO(SideFunction.INPUT, SideFunction.DISABLED, SideFunction.DISABLED, SideFunction.DISABLED);
}
