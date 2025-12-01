package dev.hyperlynx.pulsetech.feature.datasheet;

import dev.hyperlynx.pulsetech.client.ClientWrapper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class DatasheetItem extends Item {
    public DatasheetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().isClientSide()) {
            return InteractionResult.CONSUME;
        }
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DatasheetProvider provider) {
            ClientWrapper.openDatasheetScreen(provider.getDatasheet());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
