package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.net.OpenSequenceChooserPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public abstract class PatternBlock extends PulseBlock {
    public PatternBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return InteractionResult.FAIL;
        }
        PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenSequenceChooserPayload(pos));
        return InteractionResult.SUCCESS;
    }
}
