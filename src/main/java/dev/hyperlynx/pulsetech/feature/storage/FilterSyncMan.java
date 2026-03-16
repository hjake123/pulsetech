package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/// A helper class that coordinates the synchronization of item filters between Storage Modems and Retrievers.
/// When Storage Modems are syncing, they should reserve a Sync Key from this class and emit it.
/// When Retrievers are retrieving, they hear a Sync Key and use it to ask this class for the item filters.
/// This class has a tick handler it uses to let requests timeout after a configurable time (default 10 seconds).
@EventBusSubscriber
public class FilterSyncMan {
    private static final Map<Short, SyncEntry> active_sync_requests = new HashMap<>();
    public static final int SYNC_COOLDOWN = 400;

    private static final Semaphore sync_busy = new Semaphore(1);

    public static Short reserveSyncKey(BlockPos pos, List<ItemFilter> filters) throws NoFreeFilterSyncException {
        if(active_sync_requests.size() >= 65536) {
            // We can only have 65536 unique codes at once, so we have to bail out.
            throw new NoFreeFilterSyncException();
        }
        short code_from_hash = (short) filters.hashCode();
        if(code_from_hash == 0) {
            // 0 is a special result that halts the process for the sake of not sending repeat syncs.
            code_from_hash++;
        }
        while(active_sync_requests.containsKey(code_from_hash)) {
            if(active_sync_requests.get(code_from_hash).modem_pos.equals(pos)) {
                // This is a duplicate sync request, ignore it.
                return 0;
            }
            code_from_hash++;
        }

        // We now have a unique code to register and return.
        try {
            sync_busy.acquire();
            active_sync_requests.put(code_from_hash, new SyncEntry(pos));
            sync_busy.release();
        } catch (InterruptedException e) {
            Pulsetech.LOGGER.error("Error! Storage filter sync was interrupted internally, so sync will fail.");
            return 0;
        }

        return code_from_hash;
    }

    public static @Nullable List<ItemFilter> fetch(ServerLevel level, Short key) {
        if(!active_sync_requests.containsKey(key)) {
            return null;
        }
        BlockPos modem_pos = active_sync_requests.get(key).getModemPos();
        if(!level.isLoaded(modem_pos) || !(level.getBlockEntity(modem_pos) instanceof FilterBearer source)) {
            return null;
        }
        return source.getFilters();
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if(event.getLevel().isClientSide()) {
            return;
        }
        try {
            sync_busy.acquire();
        } catch (InterruptedException e) {
            Pulsetech.LOGGER.debug("Caught interruption locking the sync semaphore: ", e);
        }
        for(Short key : active_sync_requests.keySet()) {
            active_sync_requests.get(key).tick();
            if(active_sync_requests.get(key).isExpired()) {
                active_sync_requests.remove(key);
            }
        }
        sync_busy.release();
    }

    private static class SyncEntry {
        private final BlockPos modem_pos;
        private int timer = SYNC_COOLDOWN;

        public SyncEntry(BlockPos modem_pos) {
            this.modem_pos = modem_pos;
        }

        public void tick() {
            this.timer--;
        }

        public boolean isExpired() {
            return timer <= 0;
        }

        public BlockPos getModemPos() {
            return modem_pos;
        }
    }

    public static class NoFreeFilterSyncException extends Exception {}
}
