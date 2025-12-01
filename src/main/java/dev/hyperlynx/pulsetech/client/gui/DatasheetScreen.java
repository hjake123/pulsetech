package dev.hyperlynx.pulsetech.client.gui;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.HitResult;

public class DatasheetScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Pulsetech.location("textures/gui/datasheet_background.png");
    private final Datasheet datasheet;
    private int paper_top_x;
    private int paper_top_y;
    private Component title;

    public DatasheetScreen(Datasheet sheet) {
        super(Component.empty());
        datasheet = sheet;
    }

    @Override
    protected void init() {
        super.init();
        paper_top_x = getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 90;
        paper_top_y = getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 120;
        title = Component.translatable(datasheet.block().getDescriptionId()).withStyle(ChatFormatting.BLACK);

        var text = new MultiLineTextWidget(Component.literal(datasheet.entries().size() + " entries"), Minecraft.getInstance().font);
        text.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - (text.getWidth() / 2), getRectangle().getCenterInAxis(ScreenAxis.VERTICAL));
        addRenderableWidget(text);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(Minecraft.getInstance().font, title, paper_top_x + 8, paper_top_y + 8, 0, false);
        graphics.renderItem(datasheet.block().asItem().getDefaultInstance(),paper_top_x + 157,paper_top_y + 8);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(BACKGROUND, paper_top_x, paper_top_y, 0, 0,180, 240, 180, 240);
    }
}
