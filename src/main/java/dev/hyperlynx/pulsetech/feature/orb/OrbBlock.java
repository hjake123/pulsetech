package dev.hyperlynx.pulsetech.feature.orb;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class OrbBlock extends PulseBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(12, 2, 6, 14, 3, 10), Block.box(6, 2, 2, 10, 3, 4), Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 7, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(3, 7, 3, 13, 9, 13), Block.box(0, 8, 0, 3, 11, 16), Block.box(13, 8, 0, 16, 11, 16), Block.box(3, 8, 13, 13, 11, 16), Block.box(3, 8, 0, 13, 11, 3), Block.box(11, 9, 11, 13, 15, 13), Block.box(3, 9, 11, 5, 15, 13), Block.box(3, 9, 3, 5, 15, 5), Block.box(11, 9, 3, 13, 15, 5), Block.box(6, 9, 6, 10, 11, 10));

    public OrbBlock(Properties properties, SideIO io) {
        super(properties, io, true);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(OrbBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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

    private static void playCommandSound(ExecutionContext context, Orb orb) {
        context.block().getLevel().playSound(null, context.block().getBlockPos(), ModSounds.ORB_COMMAND.value(), SoundSource.BLOCKS);
        orb.triggerConfirmSound(10);
    }

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
                        playCommandSound(context, orb);
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
                        playCommandSound(context, orb);
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
                        playCommandSound(context, orb);
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
                        BlockPos pos = orb_machine.getOrigin();
                        orb.addDestination(pos.getX() + context.params().getFirst(), pos.getY() + context.params().get(1), pos.getZ() + context.params().get(2), false);
                        playCommandSound(context, orb);
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
                        playCommandSound(context, orb);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TOGGLE_PEN = ProtocolCommands.COMMANDS.register("orb/toggle_pen", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.togglePen();
                        playCommandSound(context, orb);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TOGGLE_PROJECTILE = ProtocolCommands.COMMANDS.register("orb/toggle_projectile", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof OrbBlockEntity orb_machine) {
                        Orb orb = orb_machine.getOrb();
                        if(orb == null) {
                            return;
                        }
                        orb.toggleProjectile();
                        playCommandSound(context, orb);
                    }
                }
            });
}
