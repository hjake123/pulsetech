package dev.hyperlynx.pulsetech.feature.orb;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class OrbBlock extends PulseBlock {
    public OrbBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(OrbBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OrbBlockEntity(blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SPAWN = ProtocolCommands.COMMANDS.register("orb/spawn", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        orb_machine.spawnOrb();
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MOVE_RELATIVE_X = ProtocolCommands.COMMANDS.register("orb/move_relative_x", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.addDestination(context.params().getFirst(),0,0, true);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MOVE_RELATIVE_Y = ProtocolCommands.COMMANDS.register("orb/move_relative_y", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.addDestination(0, context.params().getFirst(), 0, true);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MOVE_RELATIVE_Z = ProtocolCommands.COMMANDS.register("orb/move_relative_z", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.addDestination(0, 0, context.params().getFirst(), true);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MOVE_TO = ProtocolCommands.COMMANDS.register("orb/move_to", () ->
            new ProtocolCommand(3) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        BlockPos pos = orb_machine.getBlockPos();
                        orb.addDestination(pos.getX() + context.params().getFirst(), pos.getY() + context.params().get(1), pos.getZ() + context.params().get(2), false);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> GRAB = ProtocolCommands.COMMANDS.register("orb/grab", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.toggleGrab();
                    }
                }
            });
}
