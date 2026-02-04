package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DebuggerScreen extends Screen {
    private final BlockPos pos;
    private int current_page = 0;
    private final List<DebuggerPage> pages = new ArrayList<>();
    private final DebuggerInfoManifest manifest;

    private final Button previous_page_button = Button.builder(Component.literal("<"), (button) -> changePage(-1)).build();
    private final Button next_page_button = Button.builder(Component.literal(">"), (button) -> changePage(1)).build();
    private PagerWidget pager;

    public DebuggerScreen(DebuggerInfoManifest manifest) {
        super(Component.empty());
        this.pos = manifest.pos();
        this.manifest = manifest;
    }

    @Override
    protected void init() {
        super.init();
        int i = 0;
        int widget_x = getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL);
        int widget_y = getRectangle().getCenterInAxis(ScreenAxis.VERTICAL);
        for(DebuggerInfoManifest.Entry entry : manifest.entries()) {
            if (entry.type().equals(DebuggerInfoTypes.TEXT.value())) {
                pages.add(new DebuggerTextPage(pos, i, entry.title(), widget_x, widget_y));
            } else if (entry.type().equals(DebuggerInfoTypes.BLOCK_POS.value())) {
                pages.add(new DebuggerPosPage(pos, i, entry.title(), widget_x, widget_y));
            } else if (entry.type().equals(DebuggerInfoTypes.SEQUENCE.value())) {
                pages.add(new DebuggerSequencePage(pos, i, entry.title(), widget_x, widget_y));
            } else if (entry.type().equals(DebuggerInfoTypes.NUMBER.value())) {
                pages.add(new DebuggerBytePage(pos, i, entry.title(), widget_x, widget_y));
            }
            i++;
        }
        next_page_button.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + 50, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 20);
        next_page_button.setSize(10, 10);
        previous_page_button.setPosition(getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 50, getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 20);
        previous_page_button.setSize(10, 10);
        pager = new PagerWidget(
                getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 40,
                getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 40,
                100, 20,
                pages.size()
                );
        addRenderableWidget(previous_page_button);
        addRenderableWidget(next_page_button);
        addRenderableWidget(pager);
    }

    private void changePage(int amount) {
        current_page = current_page + amount;
        if(current_page < 0) {
            current_page = pages.size() - 1;
        }
        if(current_page > pages.size() -1) {
            current_page = 0;
        }
        pager.page = current_page;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        pages.get(current_page).update();
    }

    public void acceptInfo(Object info) {
        pages.get(current_page).acceptInfo(info);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        pages.get(current_page).render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(!super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            if(scrollY > 0) {
                changePage(1);
                return true;
            }
            if(scrollY < 0) {
                changePage(-1);
                return true;
            }
            return false;
        }
        return true;
    }
}
