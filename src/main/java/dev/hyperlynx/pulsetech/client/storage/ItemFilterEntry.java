package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemFilterEntry extends ObjectSelectionList.Entry<ItemFilterEntry> implements GuiEventListener {
    private static final ResourceLocation BACKGROUND = Pulsetech.location("filter_entry");
    private static final ResourceLocation BACKGROUND_SELECTED = Pulsetech.location("filter_entry_selected");
    private static final ResourceLocation BUTTON_MATCH_DATA = Pulsetech.location("filter_button_match_data");
    private static final ResourceLocation BUTTON_NO_MATCH_DATA = Pulsetech.location("filter_button_no_match_data");

    private ItemFilter filter;
    private boolean focused = false;
    private final PlayerCarryingAccessor accessor;

    private final SpriteIconButton match_data_button;
    private final SpriteIconButton no_match_data_button;

    private int item_left;
    private int item_right;
    private int toggle_left;
    private int toggle_right;

    public ItemFilterEntry(ItemFilter filter, PlayerCarryingAccessor accessor) {
        this.filter = filter;
        this.accessor = accessor;

        match_data_button = SpriteIconButton.builder(Component.empty(), button -> {
            updateFilter(new ItemFilter(getFilter().item(), !getFilter().match_data()));
        }, true)
                .sprite(BUTTON_MATCH_DATA, 18, 18)
                .size(18, 18)
                .build();

        no_match_data_button = SpriteIconButton.builder(Component.empty(), button -> {
                    updateFilter(new ItemFilter(getFilter().item(), !getFilter().match_data()));
                }, true)
                .sprite(BUTTON_NO_MATCH_DATA, 18, 18)
                .size(18, 18)
                .build();
    }

    public void updateFilter(ItemFilter new_filter) {
        this.filter = new_filter;
    }

    public ItemFilter getFilter() {
        return filter;
    }

    @Override
    public void setFocused(boolean b) {
        focused = b;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        item_left = left + 19;
        item_right = item_left + 16;
        toggle_left = left + width - 20;
        toggle_right = toggle_left + 18;

        graphics.blitSprite(focused ? BACKGROUND_SELECTED : BACKGROUND, left - 1, top - 2, 0, 160, 20);
        graphics.renderItem(filter.item(), item_left, top);
        graphics.drawScrollingString(Minecraft.getInstance().font, filter.getFilterLabel(), left + 40, left + 139, top + 4, 0xFFFFFF);
        if(filter.match_data()) {
            match_data_button.setPosition(toggle_left, top - 1);
            match_data_button.render(graphics, mouseX, mouseY, partialTick);
        } else {
            no_match_data_button.setPosition(toggle_left, top - 1);
            no_match_data_button.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public @NotNull Component getNarration() {
        return filter.getFilterLabel();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ItemStack carried_on_mouse = accessor.getCarrying();
        if(carried_on_mouse.isEmpty()) {
            // Check for button clicks.
            if(filter.match_data()) {
                match_data_button.mouseClicked(mouseX, mouseY, button);
            } else {
                no_match_data_button.mouseClicked(mouseX, mouseY, button);
            }
            return true;
        }
        updateFilter(new ItemFilter(carried_on_mouse.copyWithCount(1), getFilter().match_data()));
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return true;
    }

    public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (!isMouseOver(x, y)) {
            return;
        }
        if (!filter.item().isEmpty() && x >= item_left && x <= item_right) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, filter.item(), x, y);
        } else if (x >= toggle_left && x <= toggle_right) {
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font,
                    List.of(filter.match_data() ? Component.translatable("gui.pulsetech.match_data") : Component.translatable("gui.pulsetech.no_match_data")),
                    x, y);
        }
    }
}
