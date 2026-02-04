package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PagerWidget extends AbstractWidget implements Renderable {
    private static final ResourceLocation on_bit_icon = Pulsetech.location("bit_light_on");
    private static final ResourceLocation off_bit_icon = Pulsetech.location("bit_light_off");

    public int page = 0;
    private final int total_page_count;

    public PagerWidget(int x, int y, int width, int height, int total_page_count) {
        super(x, y, width, height, Component.empty());
        this.total_page_count = total_page_count;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cursor = getX();
        for(int i = 0; i < total_page_count; i++) {
            graphics.blitSprite(i == page ? on_bit_icon : off_bit_icon, cursor, getY() + 12, 20, 20);
            cursor += 16;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
