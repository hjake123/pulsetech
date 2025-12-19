package dev.hyperlynx.pulsetech.feature.cannon;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class CannonBlock extends PulseBlock {
    public CannonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(CannonBlock::new);
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
