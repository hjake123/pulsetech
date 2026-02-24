package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class RetrieverBlockEntity extends ProtocolBlockEntity {
    private List<ItemFilter> filters = new ArrayList<>();
    private int selected_filter_index = 0;

    public RetrieverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void syncFiltersUsingKey(Byte key) {
        filters = FilterSyncMan.fetch(key);
    }

    public void retrieveUpToCountMatching(Byte count) {
        ItemFilter filter = filters.get(selected_filter_index);
        var inventory = getConnectedInventory();
        int slots = inventory.getSlots();
        for(int i = 0; i < slots; i++) {
            ItemStack comparison_stack = inventory.getStackInSlot(i);
            if(filter.matches(comparison_stack)) {
                ItemStack transferred_stack = inventory.extractItem(i, count, false);
                sendStackToOutput(transferred_stack);
            }
        }
    }

    public void selectFilter(Byte index) {
        if(index < filters.size()) {
            selected_filter_index = index;
        }
    }

    private ItemStackHandler getConnectedInventory() {
        // TODO
    }

    private void sendStackToOutput(ItemStack stack) {

    }
}
