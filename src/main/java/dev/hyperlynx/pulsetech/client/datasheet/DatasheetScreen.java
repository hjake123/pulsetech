package dev.hyperlynx.pulsetech.client.datasheet;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatasheetScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Pulsetech.location("textures/gui/datasheet_background.png");
    private final Datasheet datasheet;
    private int paper_top_x;
    private int paper_top_y;
    private Component title;
    private DatasheetLinesList list;

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

        list = new DatasheetLinesList(Minecraft.getInstance(), 170, 207, paper_top_y + 30, Minecraft.getInstance().font.lineHeight + 2, datasheet);
        list.setPosition(paper_top_x + 4, paper_top_y + 31);
        addRenderableWidget(list);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(Minecraft.getInstance().font, title, paper_top_x + 8, paper_top_y + 8, 0, false);
        Block block = datasheet.block();
        if(block.defaultBlockState().is(ModBlocks.PROCESSOR)) {
            // Special case for processor rendering
            ItemStack dummy_data_cell = ModItems.DATA_CELL.toStack();
            dummy_data_cell.set(ModComponentTypes.MACROS, new Macros(Map.of("dummy", List.of("dummy")), new HashSet<>()));
            graphics.renderItem(dummy_data_cell,paper_top_x + 157,paper_top_y + 8);
        } else {
            graphics.renderItem(datasheet.block().asItem().getDefaultInstance(),paper_top_x + 157,paper_top_y + 8);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(BACKGROUND, paper_top_x, paper_top_y, 0, 0,180, 240, 180, 240);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
