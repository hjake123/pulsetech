package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class ConsoleScreen extends Screen {
    private BetterFittingMultiLineTextWidget prior_lines;
    private final EditBox command_box;
    private final BlockPos pos;

    protected ConsoleScreen(BlockPos pos, String lines) {
        super(Component.translatable("pulsetech.console"));
        this.pos = pos;
        Font font = Minecraft.getInstance().font;
        command_box = new EditBox(font, 10, 226, font.width(" ") * 80, font.lineHeight * 2, Component.empty());
        addRenderableWidget(command_box);
        prior_lines = new BetterFittingMultiLineTextWidget(10, 10, font.width(" ") * 80, font.lineHeight * 22 + 6, Component.literal(lines), font);
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
            PacketDistributor.sendToServer(new ConsoleLinePayload(pos, getLastLine()));
            // Workaround to avoid flickering when clearing the screen
            // Consider changing this, since it adds lots of overhead to the client for just this reason
            if(Arrays.stream(getLastLine().split(" ")).noneMatch(token -> token.equals("clear"))) {
                addReadoutLine(getLastLine());
            }
            command_box.setValue("");
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void addReadoutLine(String added) {
        removeWidget(prior_lines);
        prior_lines = prior_lines.withMessage((prior_lines.getMessage().copy().append(prior_lines.getMessage().getString().isEmpty() ? added : "\n" + added)));
        prior_lines.scrollToBottom();
        addRenderableWidget(prior_lines);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setPriorLines(String lines) {
        removeWidget(prior_lines);
        prior_lines = prior_lines.withMessage(Component.literal(lines));
        prior_lines.scrollToBottom();
        addRenderableWidget(prior_lines);
    }


    @Override
    public void onClose() {
        PacketDistributor.sendToServer(new ConsolePriorLinesPayload(pos, prior_lines.getMessage().getString()));
        super.onClose();
    }
}
