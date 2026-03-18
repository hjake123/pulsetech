package dev.hyperlynx.pulsetech.feature.datasheet;

import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class DatasheetItem extends Item {
    public DatasheetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DatasheetProvider provider)) {
            if(context.getItemInHand().has(ModComponentTypes.DATASHEET) && context.getLevel().isClientSide()) {
                ClientWrapper.openDatasheetScreen(context.getItemInHand().get(ModComponentTypes.DATASHEET));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        Datasheet sheet = provider.getDatasheet();

        if(context.getLevel().isClientSide()) {
            ClientWrapper.openDatasheetScreen(sheet);
        }
        context.getItemInHand().set(ModComponentTypes.DATASHEET, sheet);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(stack.has(ModComponentTypes.DATASHEET)) {
            ClientWrapper.openDatasheetScreen(stack.get(ModComponentTypes.DATASHEET));
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pulsetech.datasheet").withStyle(ChatFormatting.GRAY));
        if(stack.has(ModComponentTypes.DATASHEET)) {
            tooltipComponents.add(Component.translatable("tooltip.pulsetech.datasheet_bound").withStyle(ChatFormatting.GRAY).append(stack.get(ModComponentTypes.DATASHEET).block().getName().withStyle(ChatFormatting.WHITE)));
        }
    }
}
