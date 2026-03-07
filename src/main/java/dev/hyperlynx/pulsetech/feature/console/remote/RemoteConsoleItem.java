package dev.hyperlynx.pulsetech.feature.console.remote;

import dev.hyperlynx.pulsetech.Config;
import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlock;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RemoteConsoleItem extends Item {
    public RemoteConsoleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.CONSOLE) && context.getPlayer() != null) {
            ItemStack stack = context.getItemInHand();
            stack.set(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION.get(), context.getClickedPos());
            context.getPlayer().displayClientMessage(Component.translatable("message.pulsetech.bind_console"), true);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(!stack.has(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION)) {
            return InteractionResultHolder.pass(stack);
        }
        BlockPos pos = stack.get(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION);
        if(pos == null || !level.isLoaded(pos) || !rangeCheck(player.blockPosition(), pos)) {
            player.displayClientMessage(Component.translatable("message.pulsetech.console_out_of_range"), true);
            return InteractionResultHolder.success(stack);
        }
        if(!(level.getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            stack.remove(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION);
            player.displayClientMessage(Component.translatable("message.pulsetech.console_out_of_range"), true);
            return InteractionResultHolder.success(stack);
        }
        if(!level.isClientSide()) {
            ConsoleBlock.sendOpenConsolePayload(pos, (ServerPlayer) player, console);
        }
        return InteractionResultHolder.success(stack);
    }

    private boolean rangeCheck(BlockPos player_pos, BlockPos console_pos) {
        return Mth.sqrt((float) player_pos.distSqr(console_pos)) <= Config.REMOTE_CONSOLE_RANGE.get();
    }
}
