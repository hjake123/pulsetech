package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.net.ConsoleSendLinePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ConsoleScreen extends Screen {
    private final MultiLineEditBox console;
    private final boolean writing_mode = true;
    private final BlockPos pos;

    protected ConsoleScreen(BlockPos pos) {
        super(Component.translatable("pulsetech.console"));
        this.pos = pos;
        Font font = Minecraft.getInstance().font;
        console = new MultiLineEditBox(font, 10, 10, font.width(" ") * 80, font.lineHeight * 25, Component.empty(), Component.empty());
        addRenderableWidget(console);
    }

    @Override
    public void tick() {
        if(writing_mode) {
            setFocused(console);
        } else {
            console.setFocused(false);
        }
    }

    private String getLastLine() {
        return console.getValue().lines().toList().getLast();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            assert Minecraft.getInstance().player != null;
            PacketDistributor.sendToServer(new ConsoleSendLinePayload(pos, getLastLine()));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
