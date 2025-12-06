package dev.hyperlynx.pulsetech.client.datasheet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class DatasheetTextWidget extends ObjectSelectionList.Entry<DatasheetTextWidget> implements GuiEventListener {
    private final Component line;
    private final @Nullable String pattern;
    private static final int TEXT_COLOR = 0x3D3A37;

    public DatasheetTextWidget(Component line) {
        this(line, null);
    }

    public DatasheetTextWidget(Component line, @Nullable String pattern) {
        this.line = line;
        this.pattern = pattern;
    }

    @Override
    public Component getNarration() {
        return line;
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(Minecraft.getInstance().font, line, left, top, TEXT_COLOR, false);
        if(pattern != null) {
            int cursor = left + width - font.width(pattern) - 2;
            for(char p : pattern.toCharArray()) {
                graphics.drawString(font, String.valueOf(p), cursor, top, p == '1' ? 0xFA3100 : 0x7E0908, false);
                cursor += font.width(String.valueOf(p));
            }
        }
    }
}
