package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFilterListWidget extends ObjectSelectionList<ItemFilterEntry> {
    private final PlayerCarryingAccessor accessor;

    public ItemFilterListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, PlayerCarryingAccessor accessor) {
        super(minecraft, width, height, y, itemHeight);
        this.accessor = accessor;
    }

    public void updateFilters(List<ItemFilter> filters) {
        clearEntries();
        for(var filter : filters) {
            addEntry(new ItemFilterEntry(filter, accessor));
        }
    }

    public void addFilter(ItemFilter filter) {
        addEntry(new ItemFilterEntry(filter, accessor));
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
        // NO-OP
    }

    @Override
    protected void renderListSeparators(GuiGraphics graphics) {
        // NO-OP
    }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
        // NO-OP
    }

    @Override
    protected void renderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height) {
        super.renderItem(graphics, mouseX, mouseY, partialTick, index, left, top, width, height);
        int number_left = left + 30 + (index < 100 ? (index < 10 ? 6 : 2) : 0);
        graphics.drawScrollingString(Minecraft.getInstance().font, Component.literal(String.valueOf(index)), number_left, number_left + 20, top + 4, 0xFFFFFFFF);
    }

    @Override
    public boolean removeEntry(@Nullable ItemFilterEntry entry) {
        if(entry == null) {
            return false;
        }
        return super.removeEntry(entry);
    }

    public int size() {
        return children().size();
    }

    public void removeLast() {
        children().removeLast();
    }
}
