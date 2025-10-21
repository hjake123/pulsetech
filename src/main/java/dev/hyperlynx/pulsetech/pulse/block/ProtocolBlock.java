package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.data.ProtocolData;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// See {@link ProtocolBlockEntity}
public abstract class ProtocolBlock extends PulseBlock {
    public ProtocolBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return InteractionResult.FAIL;
        }

        // Clicking sets the protocol to "debug" TODO for now
        if(level.getBlockEntity(pos) instanceof ProtocolBlockEntity block_entity) {
            if(ProtocolData.retrieve((ServerLevel) level).get("debug") == null) {
                player.sendSystemMessage(Component.translatable("pulsetech.needs_protocol"));
                return InteractionResult.SUCCESS;
            }
            block_entity.setProtocol("debug");
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(placer == null) {
            return;
        }
        if(level instanceof ServerLevel slevel
                && level.getBlockEntity(pos) instanceof ProtocolBlockEntity block_entity
                && placer instanceof Player player) {
            block_entity.setProtocol(ProtocolData.retrieve(slevel).getDefaultFor(player));
        }
    }
}
