package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.feature.storage.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class StorageModemScreen extends AbstractContainerScreen<StorageModemMenu> {
    private ItemFilterListWidget filter_list;
    private Button sync_button;

    public StorageModemScreen(StorageModemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        sync_button = Button.builder(Component.literal("sync"), button -> {
            getMenu().getSyncStatusSlot().set(StorageModemBlockEntity.SYNC_MODE_REQUESTED);
            PacketDistributor.sendToServer(new StorageModemSyncRequest(getMenu().getPos()));
            sync_button.active = false;
        }).build();
        addRenderableWidget(sync_button);

        PacketDistributor.sendToServer(new StorageModemFiltersRequest(getMenu().getPos()));

        filter_list = new ItemFilterListWidget(Minecraft.getInstance(), 300, 300, 0, 20, getMenu()::getCarried);
        filter_list.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 150, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 150);
        addRenderableWidget(filter_list);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    public void containerTick() {
        if(!sync_button.active && getMenu().getSyncStatusSlot().get() == StorageModemBlockEntity.SYNC_MODE_STANDBY) {
            sync_button.active = true;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setFilters(List<ItemFilter> filters) {
        filter_list.updateFilters(filters);
    }
}
