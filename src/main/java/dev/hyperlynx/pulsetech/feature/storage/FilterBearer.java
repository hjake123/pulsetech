package dev.hyperlynx.pulsetech.feature.storage;

import java.util.List;

/// An interface for getting/setting filter information from a block entity.
public interface FilterBearer {
    List<ItemFilter> getFilters();
    void setFilters(List<ItemFilter> filters);
}
