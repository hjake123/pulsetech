package dev.hyperlynx.pulsetech.client.debugger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class DebuggerTextPage extends DebuggerPage {
    private final MultiLineTextWidget text_box;

    public DebuggerTextPage(BlockPos pos, int id, String title, int x, int y) {
        super(pos, id, title, x, y);
        this.text_box = new MultiLineTextWidget(Component.empty(), Minecraft.getInstance().font);
        text_box.setPosition(x, y + 10);
        text_box.setMaxWidth(127);
        text_box.setMaxRows(5);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        graphics.drawString(Minecraft.getInstance().font, title, x, y, 0xFF0000, false);
        text_box.render(graphics, i, i1, v);
    }

    @Override
    public void acceptInfo(Object info) {
        if(info instanceof String text) {
            text_box.setMessage(Component.literal(text));
        }
    }

}
