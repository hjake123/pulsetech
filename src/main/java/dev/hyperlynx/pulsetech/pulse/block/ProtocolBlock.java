package dev.hyperlynx.pulsetech.pulse.block;

import dev.hyperlynx.pulsetech.pulse.data.ProtocolData;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/// See {@link ProtocolBlockEntity}
public abstract class ProtocolBlock extends PulseBlock {
    public ProtocolBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        // Clicking sets the protocol to "debug" TODO for now
        if(level.getBlockEntity(pos) instanceof ProtocolBlockEntity be) {
            if(ProtocolData.retrieve((ServerLevel) level).get("debug") == null) {
                player.sendSystemMessage(Component.translatable("pulsetech.needs_protocol"));
                return ItemInteractionResult.SUCCESS;
            }
            be.setProtocol("debug");
        }

        return ItemInteractionResult.SUCCESS;
    }
}
