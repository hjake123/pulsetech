package dev.hyperlynx.pulsetech.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ConsoleScreen extends Screen {
    MultiLineEditBox console;
    boolean writing_mode = true;

    protected ConsoleScreen() {
        super(Component.translatable("pulsetech.console"));
        Font font = Minecraft.getInstance().font;
        console = new MultiLineEditBox(font, 10, 10, font.width(" ") * 80, font.lineHeight * 25, Component.empty(), Component.empty());
        addRenderableWidget(console);
    }

    @Override
    public void tick() {
        if(writing_mode) {
            setFocused(console);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Send " + console.getValue()));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
