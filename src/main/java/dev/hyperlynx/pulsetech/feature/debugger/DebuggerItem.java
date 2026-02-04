package dev.hyperlynx.pulsetech.feature.debugger;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.network.PacketDistributor;

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

        PacketDistributor.sendToPlayer((ServerPlayer) context.getPlayer(), source.debuggerInfoManifest());
        return InteractionResult.SUCCESS;
    }
}
