package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemFilterEntry extends ObjectSelectionList.Entry<ItemFilterEntry> implements GuiEventListener {
    private static final ResourceLocation BACKGROUND = Pulsetech.location("filter_entry");
    private static final ResourceLocation BACKGROUND_SELECTED = Pulsetech.location("filter_entry_selected");

    private ItemFilter filter;
    private boolean focused = false;
    private final PlayerCarryingAccessor accessor;

    public ItemFilterEntry(ItemFilter filter, PlayerCarryingAccessor accessor) {
        this.filter = filter;
        this.accessor = accessor;
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
        graphics.blitSprite(focused ? BACKGROUND_SELECTED : BACKGROUND, left + 29, top - 2, 0, 160, 20);
        graphics.renderItem(filter.item(), left + 49, top);
        graphics.drawScrollingString(Minecraft.getInstance().font, filter.getFilterLabel(), left + 68, left + 150, top + 4, 0xFFFFFF);
    }

    @Override
    public @NotNull Component getNarration() {
        return filter.getFilterLabel();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // TODO Check for sub buttons being clicked
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ItemStack carried_on_mouse = accessor.getCarrying();
        if(carried_on_mouse.isEmpty()) {
            return true;
        }
        updateFilter(new ItemFilter(carried_on_mouse.copyWithCount(1), getFilter().match_data()));
        return true;
    }
}
