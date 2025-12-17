package dev.hyperlynx.pulsetech.feature.datacell;

import dev.hyperlynx.pulsetech.feature.console.macros.Macros;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DataCellItem extends Item {
    public DataCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            stack.remove(ModComponentTypes.MACROS);
            stack.remove(ModComponentTypes.SCREEN_DATA);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
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
        if(data) {
            tooltip.add(Component.translatable("pulsetech.data_cell_clear_instructions").withStyle(ChatFormatting.GRAY));
        }
    }
}
