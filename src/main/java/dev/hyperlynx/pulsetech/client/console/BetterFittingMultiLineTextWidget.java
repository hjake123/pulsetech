package dev.hyperlynx.pulsetech.client.console;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.network.chat.Component;

public class BetterFittingMultiLineTextWidget extends FittingMultiLineTextWidget {
    protected BetterFittingMultiLineTextWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message, font);
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            super.renderBackground(guiGraphics);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(!scrollbarVisible()) {
            super.renderBorder(guiGraphics,
                    this.getX() - this.innerPadding() + 4,
                    this.getY() - this.innerPadding() + 4,
                    this.getWidth() + this.totalInnerPadding() - 8,
                    this.getHeight() + this.totalInnerPadding() - 8
            );
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(4.0, 4.0, 0);
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if(!scrollbarVisible()) {
            guiGraphics.pose().popPose();
        }
    }

    public void scrollToBottom() {
        setScrollAmount(getMaxScrollAmount());
    }

    public BetterFittingMultiLineTextWidget withMessage(Component message) {
        return new BetterFittingMultiLineTextWidget(getX(), getY(), width, height, message, Minecraft.getInstance().font);
    }

}
