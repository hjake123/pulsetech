package dev.hyperlynx.pulsetech.client.storage;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class RequestWidget extends AbstractContainerWidget {
    private static final ResourceLocation BACKGROUND = Pulsetech.location("storage_modem_request_panel");

    private EditBox count_box;
    private final Button request_button;
    private boolean focused = false;
    public boolean request_can_activate = false;

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(count_box, request_button);
    }

    public RequestWidget(int x, int y, Consumer<Byte> on_request) {
        super(x, y, 80, 60, Component.empty());
        Component message = Component.translatable("gui.pulsetech.request");
        int width = Minecraft.getInstance().font.width(message) + 10;
        request_button = Button.builder(message, button -> {
            try {
                byte count = Byte.parseByte(count_box.getValue());
                on_request.accept(count);
            } catch (NumberFormatException e) {
                // Don't do anything if there's an invalid number (though the button should be disabled in this case).
            }
        }).build();
        request_button.setPosition(x + 20, y + 36);
        request_button.setWidth(width);
        request_button.setHeight(14);
        request_button.active = false;

        count_box = new EditBox(Minecraft.getInstance().font, x + 20, y + 10, width, 16, Component.empty());
        count_box.setResponder(this::updateRequestStatus);
    }

    public void updateRequestStatus() {
        updateRequestStatus(count_box.getValue());
    }

    private void updateRequestStatus(String count_box_message) {
        try {
            var number = Byte.parseByte(count_box_message);
            if(number < 0) {
                throw new NumberFormatException();
            }

            // We succeeded, so this is a valid byte! Enable the request button.
            request_button.active = request_can_activate;
            request_button.setTooltip(Tooltip.create(request_can_activate ? Component.empty() : Component.translatable("gui.pulsetech.please_select_filter")));
        } catch (NumberFormatException e) {
            request_button.active = false;
            request_button.setTooltip(Tooltip.create(Component.translatable("gui.pulsetech.invalid_number")));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.blitSprite(BACKGROUND, this.getX(), this.getY(), this.width, this.height);
        request_button.render(gui, mouseX, mouseY, partialTick);
        count_box.renderWidget(gui, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return count_box.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setFocused(boolean b) {
        focused = b;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
