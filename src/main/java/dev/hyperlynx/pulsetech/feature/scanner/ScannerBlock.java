package dev.hyperlynx.pulsetech.feature.scanner;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class ScannerBlock extends PulseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_X = Shapes.or(Block.box(12, 2, 6, 14, 3, 10), Block.box(2, 2, 6, 4, 3, 10), Block.box(4, 2, 4, 12, 9, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(7, 8, 7, 9, 12, 9), Block.box(6, 12, 6, 10, 16, 10), Block.box(3, 10, 7, 7, 11, 9), Block.box(3, 10, 2, 13, 14, 3), Block.box(2, 10, 2, 3, 14, 14), Block.box(3, 10, 13, 13, 14, 14), Block.box(13, 10, 2, 14, 14, 14), Block.box(9, 10, 7, 13, 11, 9));
    protected static final VoxelShape SHAPE_Z = Shapes.or(Block.box(6, 2, 12, 10, 3, 14), Block.box(6, 2, 2, 10, 3, 4), Block.box(4, 2, 4, 12, 9, 12), Block.box(0, 0, 0, 16, 2, 16), Block.box(7, 8, 7, 9, 12, 9), Block.box(6, 12, 6, 10, 16, 10), Block.box(7, 10, 3, 9, 11, 7), Block.box(2, 10, 3, 3, 14, 13), Block.box(2, 10, 2, 14, 14, 3), Block.box(13, 10, 3, 14, 14, 13), Block.box(2, 10, 13, 14, 14, 14), Block.box(7, 10, 9, 9, 11, 13));

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

    public ScannerBlock(Properties properties, SideIO io) {
        super(properties, io);
        registerDefaultState(defaultBlockState().setValue(MODE, Mode.ANY));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(ScannerBlock::new);
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

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> FIND_NEAREST = ProtocolCommands.COMMANDS.register("scanner/find_nearest", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScannerBlockEntity scanner) {
                        BlockPos pos = scanner.findNearest();
                        context.block().emit(Sequence.fromByte((byte) pos.getX()));
                        context.block().emit(Sequence.fromByte((byte) pos.getY()));
                        context.block().emit(Sequence.fromByte((byte) pos.getZ()));
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> CHECK = ProtocolCommands.COMMANDS.register("scanner/check", () ->
            new ProtocolCommand(0) {
                @Override
                public void run(ExecutionContext context) {
                    if(context.block() instanceof ScannerBlockEntity scanner) {
                        if(!scanner.testAnyNearby()) {
                            context.block().emitRaw(new Sequence(true, false));
                        }
                    }
                }
            });

}
