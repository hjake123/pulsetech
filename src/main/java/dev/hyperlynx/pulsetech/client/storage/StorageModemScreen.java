package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.storage.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StorageModemScreen extends AbstractContainerScreen<StorageModemMenu> {
    private static final ResourceLocation BACKGROUND_LOCATION = Pulsetech.location("textures/gui/storage_modem.png");

    private ItemFilterListWidget filter_list;
    private Button sync_button;
    private Button add_button;
    private Button remove_button;
    private Button request_submenu_tab;
    private RequestWidget request_submenu;
    private boolean anything_was_selected = false;

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
        getMenu().setQuickStackRoutine(this::onQuickStack);

        PacketDistributor.sendToServer(new StorageModemFiltersRequest(getMenu().getPos()));
        filter_list = new ItemFilterListWidget(Minecraft.getInstance(), 161, 77, 0, 20, getMenu()::getCarried);
        filter_list.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 80, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 77);
        addRenderableWidget(filter_list);

        sync_button = Button.builder(Component.translatable("gui.pulsetech.sync"), button -> {
            PacketDistributor.sendToServer(new StorageModemGUIPayload(getMenu().getPos(), filter_list.getFilters(), false), new StorageModemSyncRequest(getMenu().getPos()));
            sync_button.active = false;
            if(Minecraft.getInstance().level != null) {
                Level level = Minecraft.getInstance().level;
                StorageModemBlock.setSyncing(level, getMenu().getPos(), level.getBlockState(getMenu().getPos()), true);
            }
            request_submenu.filters_changed_since_sync = false;
            request_submenu.updateRequestStatus();
        }).build();
        sync_button.active = syncAvailable();
        sync_button.setHeight(14);
        sync_button.setWidth(Minecraft.getInstance().font.width(sync_button.getMessage().getVisualOrderText()) + 16);
        sync_button.setPosition(
                leftPos + imageWidth - sync_button.getWidth() - 6,
                topPos + 4
                );
        addRenderableWidget(sync_button);

        add_button = Button.builder(Component.literal("+"), button -> {
            filter_list.addFilter(new ItemFilter(ItemStack.EMPTY, false));
            if(filter_list.size() >= 128) {
                add_button.active = false;
            }
            request_submenu.filters_changed_since_sync = true;
            request_submenu.updateRequestStatus();
        }).build();
        add_button.setWidth(14);
        add_button.setHeight(14);
        add_button.setPosition(
                leftPos + imageWidth - 36,
                topPos + 97
        );
        addRenderableWidget(add_button);

        remove_button = Button.builder(Component.literal("-"), button -> {
            if(!filter_list.removeEntry(filter_list.getSelected())) {
                // Failed to remove the selected entry, so remove the last one instead.
                filter_list.removeLast();
            }
            remove_button.active = false;
            request_submenu.filters_changed_since_sync = true;
            request_submenu.updateRequestStatus();
        }).build();
        remove_button.setWidth(14);
        remove_button.setHeight(14);
        remove_button.setPosition(
                leftPos + imageWidth - 20,
                topPos + 97
        );
        addRenderableWidget(remove_button);

        Button up_button = new ImageButton(
                leftPos + imageWidth - 68,
                topPos + 97,
                14, 14,
                new WidgetSprites(Pulsetech.location("up_button"), Pulsetech.location("up_button_focused")),
                button -> {
                    if (filter_list.getSelected() != null) {
                        filter_list.moveEntry(filter_list.getSelected(), -1);
                        request_submenu.filters_changed_since_sync = true;
                        request_submenu.updateRequestStatus();
                    }
                }
        );
        addRenderableWidget(up_button);

        Button down_button = new ImageButton(
                leftPos + imageWidth - 52,
                topPos + 97,
                14, 14,
                new WidgetSprites(Pulsetech.location("down_button"), Pulsetech.location("down_button_focused")),
                button -> {
                    if (filter_list.getSelected() != null) {
                        filter_list.moveEntry(filter_list.getSelected(), 1);
                        request_submenu.filters_changed_since_sync = true;
                        request_submenu.updateRequestStatus();
                    }
                }
        );
        addRenderableWidget(down_button);

        request_submenu_tab = new ImageButton(
                filter_list.getX() - 14, filter_list.getY(),
                8, 60,
                new WidgetSprites(Pulsetech.location("storage_modem_request_tab"), Pulsetech.location("storage_modem_request_tab")),
                button -> {
                    if(!request_submenu.visible) {
                        // We are opening the submenu. Move the tab to the left to make room.
                        request_submenu_tab.setX(filter_list.getX() - 86);
                    } else {
                        // We are closing the submenu. Move the tab to the right to fill the space.
                        request_submenu_tab.setX(filter_list.getX() - 14);
                    }
                    // Toggle the visibility of the submenu.
                    request_submenu.visible = !request_submenu.visible;
                }
        );
        addRenderableWidget(request_submenu_tab);

        request_submenu = new RequestWidget(filter_list.getX() - 86, filter_list.getY(), (count) -> {
            PacketDistributor.sendToServer(new StorageModemRetrieveRequest(menu.getPos(), filter_list.getSelectedIndex(), count));
        });
        request_submenu.visible = false;
        request_submenu.updateRequestStatus();
        addRenderableWidget(request_submenu);
    }

    public void onQuickStack(ItemStack itemStack) {
        if(filter_list.isDevoidOfData()) {
            filter_list.forceRemoveLast();
        }
        filter_list.addFilter(new ItemFilter(itemStack, false));
        request_submenu.filters_changed_since_sync = true;
        request_submenu.updateRequestStatus();
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
            if (syncAvailable()) {
                sync_button.active = true;
            }
        }
        add_button.active = filter_list.size() < 127;
        remove_button.active = filter_list.size() > 1 || !filter_list.isDevoidOfData();
        if(request_submenu.visible) {
            boolean anything_is_selected = filter_list.getSelected() != null;
            if(anything_is_selected != anything_was_selected) {
                request_submenu.request_can_activate = anything_is_selected;
                request_submenu.updateRequestStatus();
                anything_was_selected = anything_is_selected;
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

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        filter_list.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new StorageModemGUIPayload(menu.getPos(), filter_list.getFilters(), request_submenu.filters_changed_since_sync));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(filter_list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public List<Rect2i> getFilterSlotRectangles() {
        return List.of(new Rect2i(filter_list.getX(), filter_list.getY(), filter_list.getWidth(), filter_list.getHeight()));
    }

    public void setGUISyncRequired(boolean syncRequired) {
        request_submenu.filters_changed_since_sync = syncRequired;
    }
}
