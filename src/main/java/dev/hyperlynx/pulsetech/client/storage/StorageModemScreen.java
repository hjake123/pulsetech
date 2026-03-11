package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.storage.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
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
    private static final ResourceLocation UP_BUTTON = Pulsetech.location("up_button");
    private static final ResourceLocation DOWN_BUTTON = Pulsetech.location("down_button");

    private ItemFilterListWidget filter_list;
    private Button sync_button;
    private Button add_button;
    private Button remove_button;
    private Button up_button;
    private Button down_button;

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

        sync_button = Button.builder(Component.translatable("gui.pulsetech.sync"), button -> {
            PacketDistributor.sendToServer(new StorageModemGUIPayload(getMenu().getPos(), filter_list.getFilters()), new StorageModemSyncRequest(getMenu().getPos()));
            sync_button.active = false;
            if(Minecraft.getInstance().level != null) {
                Level level = Minecraft.getInstance().level;
                StorageModemBlock.setSyncing(level, getMenu().getPos(), level.getBlockState(getMenu().getPos()), true);
            }
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
            if(filter_list.size() >= 256) {
                add_button.active = false;
            }
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
        }).build();
        remove_button.setWidth(14);
        remove_button.setHeight(14);
        remove_button.setPosition(
                leftPos + imageWidth - 20,
                topPos + 97
        );
        addRenderableWidget(remove_button);

        up_button = SpriteIconButton.builder(Component.literal("^"), button -> {
            if(filter_list.getSelected() != null) {
                filter_list.moveEntry(filter_list.getSelected(), -1);
            }
        }, true).sprite(UP_BUTTON, 14, 14).build();
        up_button.setWidth(14);
        up_button.setHeight(14);
        up_button.setPosition(
                leftPos + imageWidth - 68,
                topPos + 97
        );
        addRenderableWidget(up_button);

        down_button = SpriteIconButton.builder(Component.literal("v"), button -> {
            if(filter_list.getSelected() != null) {
                filter_list.moveEntry(filter_list.getSelected(), 1);
            }
        }, true).sprite(DOWN_BUTTON, 14, 14).build();
        down_button.setWidth(14);
        down_button.setHeight(14);
        down_button.setPosition(
                leftPos + imageWidth - 52,
                topPos + 97
        );
        addRenderableWidget(down_button);

        PacketDistributor.sendToServer(new StorageModemFiltersRequest(getMenu().getPos()));

        filter_list = new ItemFilterListWidget(Minecraft.getInstance(), 161, 77, 0, 20, getMenu()::getCarried);
        filter_list.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 80, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 77);
        addRenderableWidget(filter_list);
    }

    public void onQuickStack(ItemStack itemStack) {
        if(filter_list.isDevoidOfData()) {
            filter_list.forceRemoveLast();
        }
        filter_list.addFilter(new ItemFilter(itemStack, false));
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
        add_button.active = filter_list.size() < 256;
        remove_button.active = filter_list.size() > 1 || !filter_list.isDevoidOfData();
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
        PacketDistributor.sendToServer(new StorageModemGUIPayload(menu.getPos(), filter_list.getFilters()));
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

}
