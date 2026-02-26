package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Config;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoType;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerTextInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class RetrieverBlockEntity extends ProtocolBlockEntity implements DebuggerInfoSource {
    private List<ItemFilter> filters = List.of(new ItemFilter(Items.COBBLESTONE.getDefaultInstance(), false));;
    private int selected_filter_index = 0;
    private boolean free_flowing = false;
    private int flow_timer = 0;

    public RetrieverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    private boolean matchesFilter(ItemStack stack) {
        if(filters.size() <= selected_filter_index) {
            return false;
        }
        return filters.get(selected_filter_index).matches(stack);
    }

    /// Request an up-to-date set of filters from [FilterSyncMan].
    public void syncFiltersUsingKey(Byte key) {
        filters = FilterSyncMan.fetch(key);
        setChanged();
    }

    /// Move the specified number of items from the top to the bottom that match its filters (or as many as it can if there's not enough).
    public void retrieveUpToCountMatching(Byte count) {
        if(count <= 0) {
            // For now, just ignore negative and 0 counts.
            return;
        }

        // Keep track of how many items we've moved so far.
        int left_to_move = count;
        var input = getConnectedInventory();
        if(input == null) {
            return;
        }
        int slots = input.getSlots();
        for(int i = 0; i < slots; i++) {
            // Check all slots in the connected inventory for a matching item in order.
            ItemStack comparison_stack = input.getStackInSlot(i);
            if(matchesFilter(comparison_stack)) {
                // It's a match! We're doing the transfer! First, take the whole stack out of the input.
                ItemStack transfer_stack = input.extractItem(i, Math.min(left_to_move, 64), false);
                int count_extracted = transfer_stack.getCount();

                // Send the items. This should return a stack of any leftover items that couldn't be sent.
                ItemStack remaining_stack = sendToOutput(transfer_stack);

                // Take note of how many items were transferred...
                left_to_move -= (count_extracted - remaining_stack.getCount());

                // ...and put any extras back.
                if(remaining_stack.getCount() > 0) {
                    input.insertItem(i, remaining_stack, false);
                }
            }
            if(left_to_move <= 0) {
                return;
            }
        }
    }

    /// Choose an active filter from the stored set.
    public void selectFilter(Byte index) {
        if(index < filters.size()) {
            selected_filter_index = index;
        }
    }

    /// Sets the retriever to constantly move items from the top the bottom that match its filters.
    public void toggleFreeFlowing() {
        free_flowing = !free_flowing;
    }

    @Override
    public void tick() {
        super.tick();
        if(free_flowing) {
            if(flow_timer < 0) {
                flow_timer = Config.ITEM_FLOW_INTERVAL.get();
                retrieveUpToCountMatching((byte) 64);
            }
            flow_timer--;
        }
    }

    /// Sense the filter for the first item in storage and emit its id.
    public void detectFilter() {

    }

    /// Get the inventory that the retriever is connected to (the inventory above it).
    private @Nullable IItemHandler getConnectedInventory() {
        assert level != null;
        return level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos().above(), Direction.DOWN);
    }

    private IItemHandler getOutputInventory() {
        assert level != null;
        return level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos().below(), Direction.UP);
    }

    /// Send items into the output inventory, returning a stack of the items that couldn't be transferred.
    private ItemStack sendToOutput(ItemStack stack) {
        var output = getOutputInventory();
        ItemStack to_move_stack = stack;
        for(int i = 0; i < output.getSlots(); i++) {
            if(output.isItemValid(i, stack)) {
                to_move_stack = output.insertItem(i, stack, false);
            }
            if(to_move_stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return to_move_stack;
    }

    private int getMatchingCount() {
        int matching = 0;
        var input = getConnectedInventory();
        if (input == null) {
            return 0;
        }
        for (int i = 0; i < input.getSlots(); i++) {
            if (matchesFilter(input.getStackInSlot(i))) {
                matching += input.getStackInSlot(i).getCount();
            }
        }
        return matching;
    }

    public void emitMatchingCount() {
        int count = getMatchingCount();
        if(count > Byte.MAX_VALUE) {
            count = 127;
        }
        emit(Sequence.fromByte((byte) count));
    }

    public void emitMatchingStackCount() {
        long count = (long) Math.ceil(getMatchingCount() / 64.0);
        if(count > Byte.MAX_VALUE) {
            count = 127;
        }
        emit(Sequence.fromByte((byte) count));
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        StringBuilder status_builder = new StringBuilder();
        status_builder.append(Component.translatable("debugger.pulsetech.selected_filter").getString()).append(" ").append(selected_filter_index).append("\n\n");
        if(filters.size() > selected_filter_index) {
            status_builder.append(Component.translatable("debugger.pulsetech.retriever_filter").getString()).append(filters.get(selected_filter_index).toString());
        }

        return super.getDebuggerInfoManifest().append(new DebuggerInfoManifest.Entry(
                Component.translatable("debugger.pulsetech.retriever_data").getString(),
                DebuggerInfoTypes.TEXT.value(),
                () -> new DebuggerTextInfo(status_builder.toString())
        ));
    }
}
