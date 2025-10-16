package dev.hyperlynx.pulsetech.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.block.entity.NumberEmitterBlockEntity;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlock;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlock;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NumberEmitterBlock extends ProtocolBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 2, 16), Block.box(4, 2, 4, 12, 6, 12));

    public NumberEmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return InteractionResult.FAIL;
        }

        // Clicking without an item changes which pattern from the protocol is selected.
        if(level.getBlockEntity(pos) instanceof NumberEmitterBlockEntity emitter) {
            emitter.adjustNumber(player.isShiftKeyDown() ? -1 : 1);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.NUMBER_EMITTER.get().create(pos, state);
    }

    public static final MapCodec<PulseBlock> CODEC = simpleCodec(NumberEmitterBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
