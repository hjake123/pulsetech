package dev.hyperlynx.pulsetech.feature.scanner;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ScannerBlock extends PulseBlock implements EntityBlock {
    public static EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public enum Mode implements StringRepresentable {
        ANY("any", 0),
        MONSTER("monster", 1),
        ANIMAL("animal", 2),
        ADULT("adult", 3),
        CHILD("child", 4),
        OBJECT("object", 5),
        ITEM("item", 6),
        PLAYER("player", 7);

        private final String name;
        final int index;

        Mode(String name, int index) {
            this.name = name;
            this.index = index;
        }


        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public ScannerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(MODE, Mode.ANY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
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
