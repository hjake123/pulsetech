package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class ItemFilterListWidget extends ObjectSelectionList<ItemFilterEntry> {
    private final PlayerCarryingAccessor accessor;

    public ItemFilterListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, PlayerCarryingAccessor accessor) {
        super(minecraft, width, height, y, itemHeight);
        this.accessor = accessor;
    }

    public void updateFilters(List<ItemFilter> filters) {
        clearEntries();
        for(var filter : filters) {
            addEntry(new ItemFilterEntry(filter, accessor));
        }
    }
}
