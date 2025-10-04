package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.net.ConsoleSendLinePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ConsoleScreen extends Screen {
    private BetterFittingMultiLineTextWidget prior_lines;
    private final EditBox command_box;
    private final boolean writing_mode = true;
    private final BlockPos pos;

    protected ConsoleScreen(BlockPos pos) {
        super(Component.translatable("pulsetech.console"));
        this.pos = pos;
        Font font = Minecraft.getInstance().font;
        command_box = new EditBox(font, 10, 226, font.width(" ") * 80, font.lineHeight * 2, Component.empty());
        addRenderableWidget(command_box);
        prior_lines = new BetterFittingMultiLineTextWidget(10, 10, font.width(" ") * 80, font.lineHeight * 22 + 6, Component.empty(), font);
        addRenderableWidget(prior_lines);
        setInitialFocus(command_box);
    }

    private String getLastLine() {
        return command_box.getValue();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            assert Minecraft.getInstance().player != null;
            PacketDistributor.sendToServer(new ConsoleSendLinePayload(pos, getLastLine()));
            removeWidget(prior_lines);
            prior_lines = prior_lines.withMessage(prior_lines.getMessage().copy().append(prior_lines.getMessage().getString().isEmpty() ? getLastLine() : "\n" + getLastLine()));
            prior_lines.scrollToBottom();
            addRenderableWidget(prior_lines);
            command_box.setValue("");
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
