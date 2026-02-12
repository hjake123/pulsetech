package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.registration.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class DebuggerItem extends Item {
    public DebuggerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DebuggerInfoSource source)) {
            return InteractionResult.PASS;
        }

        if(context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        context.getLevel().playSound(null, context.getClickedPos(), ModSounds.BEEP.value(), SoundSource.PLAYERS, 1.0F, 0.8F + context.getLevel().random.nextFloat() * 0.05F);
        context.getLevel().playSound(null, context.getClickedPos(), ModSounds.BEEP.value(), SoundSource.PLAYERS, 1.0F, 1.2F + context.getLevel().random.nextFloat() * 0.05F);
        PacketDistributor.sendToPlayer((ServerPlayer) context.getPlayer(), source.getDebuggerInfoManifest());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pulsetech.debugger").withStyle(ChatFormatting.GRAY));
    }
}
