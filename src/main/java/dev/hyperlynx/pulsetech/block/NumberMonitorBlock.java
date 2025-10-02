package dev.hyperlynx.pulsetech.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.pulse.SequenceBlock;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NumberMonitorBlock extends SequenceBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 2, 16), Block.box(4, 2, 4, 12, 6, 12));

    public NumberMonitorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.NUMBER_MONITOR.get().create(pos, state);
    }

    public static final MapCodec<SequenceBlock> CODEC = simpleCodec(NumberMonitorBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
