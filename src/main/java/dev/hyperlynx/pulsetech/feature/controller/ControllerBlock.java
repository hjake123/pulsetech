package dev.hyperlynx.pulsetech.feature.controller;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ControllerBlock extends PulseBlock implements EntityBlock {
    public ControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(ControllerBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProtocolBlockEntity(ModBlockEntityTypes.CONTROLLER.get(), blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> ON = ProtocolCommands.COMMANDS.register("controller/on", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().output(true);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> OFF = ProtocolCommands.COMMANDS.register("controller/off", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().output(false);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PULSE = ProtocolCommands.COMMANDS.register("controller/pulse", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    context.block().emitRaw(new Sequence(true));
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> LOOP_PULSE = ProtocolCommands.COMMANDS.register("controller/loop_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short loops = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < loops; i++) {
                        output.append(true);
                        output.append(false);
                    }
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DELAY_PULSE = ProtocolCommands.COMMANDS.register("controller/delay_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short ticks = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < ticks; i++) {
                        output.append(false);
                    }
                    output.append(true);
                    context.block().emitRaw(output);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TIMED_PULSE = ProtocolCommands.COMMANDS.register("controller/timed_pulse", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    short ticks = context.params().getFirst();
                    Sequence output = new Sequence();
                    for(int i = 0; i < ticks; i++) {
                        output.append(true);
                    }
                    context.block().emitRaw(output);
                }
            });
}
