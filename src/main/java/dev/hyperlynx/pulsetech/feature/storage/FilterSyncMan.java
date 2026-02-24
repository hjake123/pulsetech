package dev.hyperlynx.pulsetech.feature.storage;

import java.util.List;

/// A helper class that coordinates the synchronization of item filters between Storage Modems and Retrievers.
/// When Storage Modems are syncing, they should reserve a Sync Key from this class and emit it.
/// When Retrievers are retrieving, they hear a Sync Key and use it to ask this class for the item filters.
public class FilterSyncMan {
    public static List<ItemFilter> fetch(Byte key) {
        return null;
    }

    public static Byte reserveSyncKey() {
        return 0;
    }

    public static void freeSyncKey(Byte key) {

    }
}
