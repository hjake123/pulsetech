package dev.hyperlynx.pulsetech.feature.datacell;

import dev.hyperlynx.pulsetech.feature.console.macros.Macros;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
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

public class DataCellItem extends Item {
    public DataCellItem(Properties properties) {
        super(properties);
    }

    public static float getLoadedProperty(ItemStack stack) {
        return stack.has(ModComponentTypes.MACROS)
                || stack.has(ModComponentTypes.SCREEN_DATA)
                || stack.has(ModComponentTypes.SCANNER_LINK_POSITION)
                ? 1.0F : 0.0F;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            stack.remove(ModComponentTypes.MACROS);
            stack.remove(ModComponentTypes.SCREEN_DATA);
            stack.remove(ModComponentTypes.SCANNER_LINK_POSITION);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if(context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.SCANNER)) {
            stack.set(ModComponentTypes.SCANNER_LINK_POSITION, context.getClickedPos());
            context.getPlayer().displayClientMessage(Component.translatable("pulsetech.stored_scanner_pos"), true);
        } else if(stack.has(ModComponentTypes.SCANNER_LINK_POSITION) && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ScannerLinkable linkable) {
            boolean success = linkable.setLinkedOrigin(stack.get(ModComponentTypes.SCANNER_LINK_POSITION));
            if(context.getPlayer() != null) {
                if(success) {
                    context.getPlayer().displayClientMessage(Component.translatable("pulsetech.linked_to_scanner"), true);
                } else {
                    context.getPlayer().displayClientMessage(Component.translatable("pulsetech.failed_link_to_scanner"), true);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        boolean data = false;
        if(stack.has(ModComponentTypes.MACROS)) {
            Macros macros = stack.get(ModComponentTypes.MACROS);
            tooltip.add(Component.literal(macros.macros().size() + " ").append(Component.translatable("pulsetech.macros_stored")).withStyle(ChatFormatting.GRAY));
            data = true;
        }
        if(stack.has(ModComponentTypes.SCREEN_DATA)) {
            tooltip.add(Component.translatable("pulsetech.contains_screen_data").withStyle(ChatFormatting.GRAY));
            data = true;
        }
        if(stack.has(ModComponentTypes.SCANNER_LINK_POSITION)) {
            tooltip.add(Component.translatable("pulsetech.contains_scanner_link_pos").withStyle(ChatFormatting.GRAY));
            data = true;
        }
        if(data) {
            tooltip.add(Component.translatable("pulsetech.data_cell_clear_instructions").withStyle(ChatFormatting.GRAY));
        }
    }
}
