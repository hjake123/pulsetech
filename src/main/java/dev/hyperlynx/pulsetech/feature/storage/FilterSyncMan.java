package dev.hyperlynx.pulsetech.feature.storage;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

/// A helper class that coordinates the synchronization of item filters between Storage Modems and Retrievers.
/// When Storage Modems are syncing, they should reserve a Sync Key from this class and emit it.
/// When Retrievers are retrieving, they hear a Sync Key and use it to ask this class for the item filters.
public class FilterSyncMan {
    public static List<ItemFilter> fetch(Byte key) {
        ItemStack sword = Items.IRON_SWORD.getDefaultInstance();
        sword.set(DataComponents.CUSTOM_NAME, Component.literal("Test Sword"));
        return List.of(new ItemFilter(Items.COBBLESTONE.getDefaultInstance(), false), new ItemFilter(sword, true));
    }

    public static Byte reserveSyncKey() {
        return 0;
    }

    public static void freeSyncKey(Byte key) {

    }
}
