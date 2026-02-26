package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class RetrieverBlock extends PulseBlock {
    public RetrieverBlock(Properties properties, SideIO io, boolean pulse_input) {
        super(properties, io, pulse_input);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(RepeaterBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RetrieverBlockEntity(ModBlockEntityTypes.RETRIEVER.value(), blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> SYNC = ProtocolCommands.COMMANDS.register("retriever/sync", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.syncFiltersUsingKey(context.params().getFirst());
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
