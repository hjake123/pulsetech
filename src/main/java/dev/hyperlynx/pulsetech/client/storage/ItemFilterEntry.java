package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemFilterEntry extends ObjectSelectionList.Entry<ItemFilterEntry> implements GuiEventListener {
    private ItemFilter filter;
    private boolean focused = false;
    private boolean edit_mode = false;
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
        graphics.renderItem(filter.item(), top, left);
    }

    @Override
    public Component getNarration() {
        return Component.literal(filter.toString());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }
}
