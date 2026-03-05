package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.protocol.Protocol;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolDataMap;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StorageModemBlockEntity extends PulseBlockEntity implements FilterBearer {
    private final EmitterModule emitter = new EmitterModule();
    private List<ItemFilter> filters = new ArrayList<>();
    private int sync_cooldown = 0;

    // The current status of the filter sync system.
    private final DataSlot sync_mode = DataSlot.standalone();
    public static final int SYNC_MODE_STANDBY = 0;
    public static final int SYNC_MODE_REQUESTED = 1;
    public static final int SYNC_MODE_COOLDOWN = 2;

    public StorageModemBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.STORAGE_MODEM.value(), pos, blockState);
        // TODO debug
        filters.add(new ItemFilter(Items.PAPER.getDefaultInstance(), false));
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
        if(sync_mode.get() == SYNC_MODE_REQUESTED) {
            performFilterSync();
            sync_mode.set(SYNC_MODE_COOLDOWN);
            sync_cooldown = FilterSyncMan.SYNC_COOLDOWN;
        }
        if(sync_cooldown == 0) {
            sync_mode.set(SYNC_MODE_STANDBY);
            setChanged();
        }
        if(sync_cooldown >= 0) {
            sync_cooldown--;
        }
    }

    @Override
    public boolean isDelayed() {
        return emitter.getDelay() > 0;
    }

    /// Register a sync code with [FilterSyncMan] and send it from the output, to be heard by retrievers.
    public void performFilterSync() {
        try {
            short code = FilterSyncMan.reserveSyncKey(getBlockPos(), filters);
            if(code == 0) {
                // This was a duplicate sync request. Don't proceed.
                return;
            }

            // Fetch the sequence for the Retriever's SYNC command. First, get the registry holder for the Retriever.
            var retriever_registry_holder = ModBlockEntityTypes.RETRIEVER.get().builtInRegistryHolder();
            if(retriever_registry_holder == null) {
                Pulsetech.LOGGER.error("Can't find the registry holder for {}", ModBlockEntityTypes.RETRIEVER.get());
                return;
            }

            // Next, get its Protocol.
            Protocol retriever_protocol = retriever_registry_holder.getData(ProtocolDataMap.TYPE);
            if(retriever_protocol == null || !retriever_protocol.getCommands().containsKey(RetrieverBlock.SYNC.get())) {
                Pulsetech.LOGGER.error("Retriever has no valid protocol, so the sync operation will fail. Check your datapacks, reinstall Pulsetech, or report this as a bug!");
                return;
            }

            // Finally, send the sequence for SYNC.
            Sequence sync_sequence = retriever_protocol.getCommands().get(RetrieverBlock.SYNC.get());
            emitter.enqueueTransmission(sync_sequence);

            // Split the code into a pair of bytes for transmission
            byte code_high = (byte) ((code & 0xFF00) >> 8);
            byte code_low = (byte) (code & 0x00FF);

            emitter.enqueueTransmission(Sequence.fromByte(code_low));
            emitter.enqueueTransmission(Sequence.fromByte(code_high));
            emitter.setActive(true);
        } catch (FilterSyncMan.NoFreeFilterSyncException e) {
            Pulsetech.LOGGER.warn("No free sync slots for storage modem filters; over 65,536 syncs are taking place at once.");
            Pulsetech.LOGGER.warn("That's way more then is reasonable, so I recommend checking into what's causing all those syncs.");
            Pulsetech.LOGGER.warn("If you can't find anything, please report this as a Pulsetech bug.");
        }
    }

    @Override
    public List<ItemFilter> getFilters() {
        return filters;
    }

    @Override
    public void setFilters(List<ItemFilter> filters) {
        this.filters = filters;
    }

    public DataSlot getSyncStatus() {
        return sync_mode;
    }
}
