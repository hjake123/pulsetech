package dev.hyperlynx.pulsetech.feature.console.remote;

import dev.hyperlynx.pulsetech.Config;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlock;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class RemoteConsoleItem extends Item {
    public RemoteConsoleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ConsoleBlock && context.getPlayer() != null) {
            ItemStack stack = context.getItemInHand();
            stack.set(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION.get(), GlobalPos.of(context.getLevel().dimension(), context.getClickedPos()));
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
        GlobalPos global_pos = stack.get(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION);
        if(global_pos == null || !level.dimension().equals(global_pos.dimension())) {
            player.displayClientMessage(Component.translatable("message.pulsetech.console_out_of_range"), true);
            return InteractionResultHolder.pass(stack);
        }
        BlockPos pos = global_pos.pos();
        if(!level.isLoaded(pos) || outOfRange(player.blockPosition(), pos)) {
            player.displayClientMessage(Component.translatable("message.pulsetech.console_out_of_range"), true);
            return InteractionResultHolder.fail(stack);
        }
        if(!(level.getBlockEntity(pos) instanceof ConsoleBlockEntity console)) {
            stack.remove(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION);
            player.displayClientMessage(Component.translatable("message.pulsetech.console_missing"), true);
            return InteractionResultHolder.fail(stack);
        }
        if(!level.isClientSide()) {
            ConsoleBlock.sendOpenConsolePayload(pos, (ServerPlayer) player, console);
        }
        return InteractionResultHolder.success(stack);
    }

    private static boolean outOfRange(BlockPos player_pos, BlockPos console_pos) {
        if(Config.REMOTE_CONSOLE_RANGE.get() == 0) {
            // Special case to disable range check.
            return false;
        }
        return !(Mth.sqrt((float) player_pos.distSqr(console_pos)) <= Config.REMOTE_CONSOLE_RANGE.get());
    }

    /// Color properties should be arranged by the sequence in [dev.hyperlynx.pulsetech.feature.console.ConsoleColor]
    public static float getColorProperty(ItemStack stack, ClientLevel level, LivingEntity player) {
        if(!stack.has(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION)) {
            return 0;
        }
        GlobalPos global_pos = stack.get(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION);
        if(global_pos == null || !level.dimension().equals(global_pos.dimension())) {
            return 0;
        }
        BlockPos pos = global_pos.pos();
        if(!level.isLoaded(pos) || outOfRange(player.blockPosition(), pos)) {
            return 0;
        }
        if(!(level.getBlockState(pos).getBlock() instanceof ConsoleBlock console)) {
            return 0;
        }
        return switch(console.getColor()) {
            case AMBER -> 1;
            case REDSTONE -> 2;
            case GREEN -> 3;
            case INDIGO -> 4;
            case WHITE -> 5;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.has(ModComponentTypes.REMOTE_CONSOLE_LINK_POSITION)) {
            tooltipComponents.add(Component.translatable("tooltip.pulsetech.remote_console_bound"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
