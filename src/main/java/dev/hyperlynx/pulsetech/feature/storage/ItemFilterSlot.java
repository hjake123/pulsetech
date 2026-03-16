package dev.hyperlynx.pulsetech.feature.storage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ItemFilterSlot extends SlotItemHandler {
    public ItemFilterSlot(IItemHandler handler, int slot) {
        super(handler, slot, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return true;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }
}
