package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PagerWidget extends AbstractWidget implements Renderable {
    private static final ResourceLocation FULL_PIP = Pulsetech.location("full_pip");
    private static final ResourceLocation EMPTY_PIP = Pulsetech.location("empty_pip");

    public int page = 0;
    private final int total_page_count;

    public PagerWidget(int x, int y, int width, int height, int total_page_count) {
        super(x, y, width, height, Component.empty());
        this.total_page_count = total_page_count;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cursor = total_page_count % 2 == 0 ? getX() + 29 - ((total_page_count-1) / 2) * 10 : getX() + 34 - ((total_page_count) / 2) * 10;
        for(int i = 0; i < total_page_count; i++) {
            graphics.blitSprite(i == page ? FULL_PIP : EMPTY_PIP, cursor, getY() + 4, 5, 5);
            cursor += 10;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
