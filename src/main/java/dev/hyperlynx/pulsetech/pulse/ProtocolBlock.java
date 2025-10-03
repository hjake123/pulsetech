package dev.hyperlynx.pulsetech.pulse;

import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/// See {@link ProtocolBlockEntity}
public abstract class ProtocolBlock extends SequenceBlock {
    public ProtocolBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        // Clicking with an item that contains a protocol component updates the protocol.
        if(stack.has(ModComponentTypes.PROTOCOL) && level.getBlockEntity(pos) instanceof ProtocolBlockEntity be) {
            be.setProtocol(stack.get(ModComponentTypes.PROTOCOL));
            player.displayClientMessage(Component.translatable("message.pulsetech.set_protocol"), true);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
