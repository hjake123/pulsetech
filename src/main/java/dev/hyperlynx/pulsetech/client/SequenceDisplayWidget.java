package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SequenceDisplayWidget extends AbstractWidget implements Renderable {
    private static final ResourceLocation on_bit_icon = Pulsetech.location("bit_light_on");
    private static final ResourceLocation off_bit_icon = Pulsetech.location("bit_light_off");
    private Sequence sequence = null;

    public SequenceDisplayWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int cursor = getX();
        for(int i = 0; i < sequence.length(); i++) {
            boolean bit = sequence.get(i);
            graphics.blitSprite(bit ? on_bit_icon : off_bit_icon, cursor, getY() + 12, 20, 20);
            cursor += 16;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
