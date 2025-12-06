package dev.hyperlynx.pulsetech.feature.scanner;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScannerBlock extends PulseBlock implements EntityBlock {
    public ScannerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(ScannerBlock::new);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ScannerBlockEntity(blockPos, blockState);
    }

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> MODE_SELECT = ProtocolCommands.COMMANDS.register("scanner/mode_select", () ->
            new ProtocolCommand(1) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScannerBlockEntity scanner) {
                        scanner.setMode(context.params().getFirst());
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> COUNT = ProtocolCommands.COMMANDS.register("scanner/count", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScannerBlockEntity scanner) {
                        byte count = scanner.countNearby();
                        context.block().emit(Sequence.fromByte(count));
                    }
                }
            });
}
