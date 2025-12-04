package dev.hyperlynx.pulsetech.client.datasheet;

import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class DatasheetEntryList extends ObjectSelectionList<DatasheetEntryWidget> {
    public DatasheetEntryList(Minecraft client, int width, int height, int y, int item_height, Datasheet datasheet) {
        super(client, width, height, y, item_height);
        for(var entry : datasheet.entries().stream().sorted().toList()) {
            addEntry(new DatasheetEntryWidget(entry));
        }
        setRenderHeader(false, 0);
    }

    public int getRowWidth() {
        return getWidth();
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
        // NO-OP
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
        // NO-OP
    }
}
