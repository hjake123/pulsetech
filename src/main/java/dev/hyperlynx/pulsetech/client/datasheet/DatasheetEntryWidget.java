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
        int cursor = left + width - font.width(pattern) - 2;
        for(char p : pattern.toCharArray()) {
            graphics.drawString(font, String.valueOf(p), cursor, top, p == '1' ? 0xFA3100 : 0x7E0908, false);
            cursor += font.width(String.valueOf(p));
        }
        boolean no_params = entry.params().getString().isEmpty();
        if(!no_params) {
            graphics.drawString(font, Component.translatable("pulsetech.parameters").append(" ").append(entry.params()), left, top + 10, 0x68635F, false);
        }
        graphics.drawWordWrap(font,entry.description(), left, top + (no_params ? 10 : 20), 165, TEXT_COLOR);
    }

    @Override
    public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
        return false;
    }
}
