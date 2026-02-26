package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import dev.hyperlynx.pulsetech.feature.storage.StorageModemSyncRequest;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class StorageModemScreen extends Screen {
    private final BlockPos pos;
    private final List<ItemFilter> filters = new ArrayList<>();

    public StorageModemScreen(BlockPos pos, List<ItemFilter> initial_filters) {
        super(Component.empty());
        this.pos = pos;
        filters.addAll(initial_filters);

    }

    @Override
    protected void init() {
        Button sync_button = Button.builder(Component.literal("sync"), button -> {
            PacketDistributor.sendToServer(new StorageModemSyncRequest(pos));
        }).build();
        addRenderableWidget(sync_button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
