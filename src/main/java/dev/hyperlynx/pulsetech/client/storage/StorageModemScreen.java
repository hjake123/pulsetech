package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.storage.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StorageModemScreen extends AbstractContainerScreen<StorageModemMenu> {
    private static final ResourceLocation BACKGROUND_LOCATION = Pulsetech.location("textures/gui/storage_modem.png");

    private ItemFilterListWidget filter_list;
    private Button sync_button;

    public StorageModemScreen(StorageModemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 175;
        imageHeight = 193;
        titleLabelX = getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + 7;
        titleLabelY = getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 7;
        inventoryLabelX = titleLabelX;
        inventoryLabelY = imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();
        sync_button = Button.builder(Component.literal("sync"), button -> {
            PacketDistributor.sendToServer(new StorageModemSyncRequest(getMenu().getPos()));
            sync_button.active = false;
        }).build();
        sync_button.active = syncAvailable();
        sync_button.setHeight(14);
        sync_button.setWidth(Minecraft.getInstance().font.width(sync_button.getMessage().getVisualOrderText()) + 16);
        sync_button.setPosition(
                leftPos + imageWidth - sync_button.getWidth() - 6,
                topPos + 4
                );
        addRenderableWidget(sync_button);

        Button add_button = Button.builder(Component.literal("+"), button -> {
            filter_list.addFilter(new ItemFilter(ItemStack.EMPTY, false));
        }).build();
        add_button.setWidth(14);
        add_button.setHeight(14);
        add_button.setPosition(
                leftPos + imageWidth - 20,
                topPos + 97
        );
        addRenderableWidget(add_button);

        PacketDistributor.sendToServer(new StorageModemFiltersRequest(getMenu().getPos()));

        filter_list = new ItemFilterListWidget(Minecraft.getInstance(), 161, 75, 0, 20, getMenu()::getCarried);
        filter_list.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 80, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 78);
        addRenderableWidget(filter_list);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND_LOCATION, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private boolean syncAvailable() {
        assert Minecraft.getInstance().level != null;
        return !StorageModemBlock.isSyncing(Minecraft.getInstance().level.getBlockState(getMenu().getPos()));
    }

    @Override
    public void containerTick() {
        if(!sync_button.active && Minecraft.getInstance().level != null) {
            if (syncAvailable() ) {
                sync_button.active = true;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setFilters(List<ItemFilter> filters) {
        filter_list.updateFilters(filters);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
