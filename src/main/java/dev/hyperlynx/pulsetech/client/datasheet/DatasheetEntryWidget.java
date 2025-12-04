package dev.hyperlynx.pulsetech.client.datasheet;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class DatasheetEntryWidget extends ObjectSelectionList.Entry<DatasheetEntryWidget> implements GuiEventListener {
    private static final int TEXT_COLOR = 0x3D3A37;
    private final DatasheetEntry entry;

    public DatasheetEntryWidget(DatasheetEntry entry) {
        super();
        this.entry = entry;
    }

    @Override
    public Component getNarration() {
        return Component.empty();
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, entry.name().getString(), left, top, TEXT_COLOR, false);
        assert entry.pattern() != null;
        String pattern = entry.pattern().toString();
        int cursor = left;
        for(char p : pattern.toCharArray()) {
            graphics.drawString(font, String.valueOf(p), cursor, top + 10, p == '1' ? 0xFA3100 : 0x7E0908, false);
            cursor += font.width(String.valueOf(p));
        }
        graphics.drawString(font, entry.description().getString(), left, top + 20, TEXT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
        return false;
    }
}
