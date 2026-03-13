package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.protocol.Protocol;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolDataMap;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageModemBlockEntity extends PulseBlockEntity implements FilterBearer {
    private final EmitterModule emitter = new EmitterModule();
    private List<ItemFilter> filters = new ArrayList<>(Collections.singleton(new ItemFilter(ItemStack.EMPTY, false)));
    private int sync_cooldown = 0;
    private boolean gui_sync_needed = false; // Stores whether the Request button in the GUI should work, or if the Sync button should be required first

    public StorageModemBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.STORAGE_MODEM.value(), pos, blockState);
    }

    @Override
    public boolean isActive() {
        return emitter.isActive();
    }

    @Override
    public void setActive(boolean active) {
        emitter.setActive(active);
    }

    @Override
    public void tick() {
        assert level != null;
        if(level.isClientSide) {
            return;
        }
        emitter.tick((ServerLevel) level, this);
        if(sync_cooldown == 0) {
            StorageModemBlock.setSyncing(level, getBlockPos(), getBlockState(), false);
        }
        if(sync_cooldown >= 0) {
            sync_cooldown--;
        }
    }

    @Override
    public boolean isDelayed() {
        return emitter.getDelay() > 0;
    }

    private static @Nullable Protocol getRetrieverProtocol() {
        // Fetch the sequence for the Retriever's commands. First, get the registry holder for the Retriever.
        var retriever_registry_holder = ModBlockEntityTypes.RETRIEVER.get().builtInRegistryHolder();
        if(retriever_registry_holder == null) {
            Pulsetech.LOGGER.error("Can't find the registry holder for {}", ModBlockEntityTypes.RETRIEVER.get());
            return null;
        }

        // Next, get its Protocol.
        Protocol retriever_protocol = retriever_registry_holder.getData(ProtocolDataMap.TYPE);
        if(retriever_protocol == null || !retriever_protocol.getCommands().containsKey(RetrieverBlock.SYNC.get())) {
            Pulsetech.LOGGER.error("Retriever has no valid protocol, so this Storage Modem operation will fail. Check your datapacks, reinstall Pulsetech, or report this as a bug!");
            return null;
        }
        return retriever_protocol;
    }

    /// Register a sync code with [FilterSyncMan] and send it from the output, to be heard by retrievers.
    public void performFilterSync() {
        short code = 0;
        try {
            code = FilterSyncMan.reserveSyncKey(getBlockPos(), filters);
        } catch (FilterSyncMan.NoFreeFilterSyncException e) {
            Pulsetech.LOGGER.warn("No free sync slots for storage modem filters; over 65,536 syncs are taking place at once.");
            Pulsetech.LOGGER.warn("That's way more then is reasonable, so I recommend checking into what's causing all those syncs.");
            Pulsetech.LOGGER.warn("If you can't find anything, please report this as a Pulsetech bug.");
        }
        if(code == 0) {
            // This was a duplicate sync request. Don't proceed.
            return;
        }

        assert level != null;
        StorageModemBlock.setSyncing(level, getBlockPos(), getBlockState(), true);
        sync_cooldown = FilterSyncMan.SYNC_COOLDOWN;

        Protocol retriever_protocol = getRetrieverProtocol();
        if (retriever_protocol == null) return;

        // Send the sequence for SYNC.
        Sequence sync_sequence = retriever_protocol.getCommands().get(RetrieverBlock.SYNC.get());
        emitter.enqueueTransmission(sync_sequence);

        // Split the code into a pair of bytes for transmission
        byte code_high = (byte) ((code & 0xFF00) >> 8);
        byte code_low = (byte) (code & 0x00FF);

        emitter.enqueueTransmission(Sequence.fromByte(code_low));
        emitter.enqueueTransmission(Sequence.fromByte(code_high));
        emitter.setActive(true);
    }

    public void sendRetrieveCommand(byte filterIndex, byte count) {
        Protocol retriever_protocol = getRetrieverProtocol();
        if (retriever_protocol == null) return;

        // Send the sequence for SELECT FILTER.
        emitter.enqueueTransmission(retriever_protocol.getCommands().get(RetrieverBlock.SELECT_FILTER.get()));

        // Send the filter to select.
        emitter.enqueueTransmission(Sequence.fromByte(filterIndex));

        if(count == 0) {
            // 0 count requests just set the filter, so we stop here.
            return;
        }

        // Send the sequence for RETRIEVE.
        emitter.enqueueTransmission(retriever_protocol.getCommands().get(RetrieverBlock.RETRIEVE.get()));

        // Send the count to retrieve.
        emitter.enqueueTransmission(Sequence.fromByte(count));

        // Start transmitting.
        emitter.setActive(true);
    }

    @Override
    public List<ItemFilter> getFilters() {
        return filters;
    }

    @Override
    public void setFilters(List<ItemFilter> filters) {
        this.filters = filters;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Filters", ItemFilter.CODEC.listOf().encodeStart(NbtOps.INSTANCE, filters).getPartialOrThrow());
        tag.putBoolean("NeedsSync", gui_sync_needed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        filters = ItemFilter.CODEC.listOf().decode(NbtOps.INSTANCE, tag.get("Filters")).getPartialOrThrow().getFirst();
        gui_sync_needed = tag.getBoolean("NeedsSync");
    }

    public void rememberGUISyncRequired(boolean sync_required) {
        gui_sync_needed = sync_required;
    }

    public boolean isGUISyncNeeded() {
        return gui_sync_needed;
    }
}
