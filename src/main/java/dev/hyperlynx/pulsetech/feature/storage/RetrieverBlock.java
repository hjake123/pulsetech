package dev.hyperlynx.pulsetech.feature.storage;

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

public class RetrieverBlock extends PulseBlock {
    public RetrieverBlock(Properties properties, SideIO io, boolean pulse_input) {
        super(properties, io, pulse_input);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
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
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof RetrieverBlockEntity retriever) {
                        retriever.syncFiltersUsingKey(context.params().getFirst());
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
}
