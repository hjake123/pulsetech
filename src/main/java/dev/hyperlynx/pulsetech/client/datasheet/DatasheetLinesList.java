package dev.hyperlynx.pulsetech.client.datasheet;

import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class DatasheetLinesList extends ObjectSelectionList<DatasheetTextWidget> {
    public DatasheetLinesList(Minecraft client, int width, int height, int y, int item_height, Datasheet datasheet) {
        super(client, width, height, y, item_height);
        for(var entry : datasheet.entries().stream().sorted().toList()) {
            addEntry(new DatasheetTextWidget(entry.name().copy().withStyle(ChatFormatting.BOLD), entry.pattern() == null ? null : entry.pattern().toString()));
            if(!entry.params().getString().isEmpty()) {
                addEntry(new DatasheetTextWidget(Component.translatable("pulsetech.parameters").append(" ").append(entry.params()).withColor(0x68635F)));
            }
            for (var line : client.font.getSplitter().splitLines(entry.description(), getRowWidth(), Style.EMPTY)) {
                addEntry(new DatasheetTextWidget(Component.literal(line.getString())));
            }
            addEntry(new DatasheetTextWidget(Component.empty()));
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
