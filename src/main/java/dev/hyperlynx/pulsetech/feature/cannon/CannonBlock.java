package dev.hyperlynx.pulsetech.feature.cannon;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.util.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class CannonBlock extends PulseBlock {
    protected static final VoxelShape SHAPE_X = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(2, 2, 6, 4, 3, 10), Block.box(6, 2, 2, 10, 3, 4), Block.box(6, 2, 12, 10, 3, 14), Block.box(4, 2, 4, 12, 7, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(2, 8, 9, 14, 16, 14), Block.box(2, 8, 2, 14, 16, 7), Block.box(7, 7, 3, 9, 8, 13));
    protected static final VoxelShape SHAPE_Z = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(6, 2, 2, 10, 3, 4), Block.box(2, 2, 6, 4, 3, 10), Block.box(12, 2, 6, 14, 3, 10), Block.box(4, 2, 4, 12, 7, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(9, 8, 2, 14, 16, 14), Block.box(2, 8, 2, 7, 16, 14), Block.box(3, 7, 7, 13, 8, 9));

    public CannonBlock(Properties properties, SideIO io) {
        super(properties, io);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(CannonBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? SHAPE_X : SHAPE_Z;
    }


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        ParticleScribe.drawParticleBox(level, ParticleTypes.ELECTRIC_SPARK, state.getValue(FACING).getAxis().equals(Direction.Axis.X)
                ? AABB.ofSize(pos.getCenter().add(0, 0.25, 0), 0.8, 0.5, 0.1)
                : AABB.ofSize(pos.getCenter().add(0, 0.25, 0), 0.1, 0.5, 0.8)
                , 1);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CannonBlockEntity(blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TARGET = ProtocolCommands.COMMANDS.register("cannon/target", () ->
            new ProtocolCommand(3) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof CannonBlockEntity cannon) {
                        cannon.setTargetOffset(context.params().getFirst(), context.params().get(1), context.params().get(2));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> FIRE = ProtocolCommands.COMMANDS.register("cannon/fire", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof CannonBlockEntity cannon) {
                        cannon.fire();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> NUDGE = ProtocolCommands.COMMANDS.register("cannon/nudge", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof CannonBlockEntity cannon) {
                        // packed format is UDNESW##, where each letter is whether to move that direction and ## is a two-bit unsigned int for distance.
                        // HOWEVER! since it is little endian, the format we get is actually ##WSENDU instead!
                        int packed = context.params().getFirst() & 0xFF;
                        int reversed_distance = (packed & 0b11000000) >> 6;
                        int distance = ((reversed_distance & 0b01) << 1 ) | ((reversed_distance & 0b10) >> 1);
                        cannon.resetNudge();
                        if((packed & 0b00000001) != 0) {
                            cannon.addNudge(Direction.UP, distance);
                        }
                        if((packed & 0b00000010) != 0) {
                            cannon.addNudge(Direction.DOWN, distance);
                        }
                        if((packed & 0b00000100) != 0) {
                            cannon.addNudge(Direction.NORTH, distance);
                        }
                        if((packed & 0b00001000) != 0) {
                            cannon.addNudge(Direction.EAST, distance);
                        }
                        if((packed & 0b00010000) != 0) {
                            cannon.addNudge(Direction.SOUTH, distance);
                        }
                        if((packed & 0b00100000) != 0) {
                            cannon.addNudge(Direction.WEST, distance);
                        }
                    }
                }
            });
}
