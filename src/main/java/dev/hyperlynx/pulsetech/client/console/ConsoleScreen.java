package dev.hyperlynx.pulsetech.client.console;

import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.feature.console.ConsolePriorLinesPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleScreen extends Screen {
    private BetterFittingMultiLineTextWidget prior_lines;
    private EditBox command_box;
    private final BlockPos pos;
    private final String prior_lines_str;
    private int past_commands_cursor = -1;

    private final List<String> past_commands = new ArrayList<>();

    public ConsoleScreen(BlockPos pos, String lines) {
        super(Component.translatable("block.pulsetech.console"));
        this.pos = pos;
        this.prior_lines_str = lines;
    }

    @Override
    protected void init() {
        super.init();
        Font font = Minecraft.getInstance().font;
        int console_width = font.width(" ") * 80;
        int command_box_height = font.lineHeight * 2;
        int console_height = font.lineHeight * 22 + 6;
        command_box = new EditBox(font, this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - (console_width / 2), this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + (console_height / 2) - 5, console_width, command_box_height, Component.empty());
        command_box.setMaxLength(104);
        addRenderableWidget(command_box);
        prior_lines = new BetterFittingMultiLineTextWidget(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - (console_width / 2),this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - (console_height / 2) - 10, console_width, console_height, Component.literal(prior_lines_str), font);
        prior_lines.scrollToBottom();
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
            past_commands.add(getLastLine());
            // Workaround to avoid flickering when clearing the screen
            // Consider changing this, since it adds lots of overhead to the client for just this reason
            if(Arrays.stream(getLastLine().split(" ")).noneMatch(token -> token.equals("clear"))) {
                addReadoutLine("> " + getLastLine());
            }
            command_box.setValue("");
            past_commands_cursor = -1;
        }
        if(keyCode == GLFW.GLFW_KEY_UP) {
            if(past_commands.isEmpty()) {
                return true;
            }
            if(past_commands_cursor == -1) {
                past_commands_cursor = past_commands.size() - 1;
            } else if(past_commands_cursor > 0) {
                past_commands_cursor -= 1;
            }
            command_box.setValue(past_commands.get(past_commands_cursor));
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            if(past_commands.isEmpty()) {
                return true;
            }
            if(past_commands_cursor != -1) {
                if(past_commands_cursor < past_commands.size() - 1) {
                    past_commands_cursor += 1;
                }
                command_box.setValue(past_commands.get(past_commands_cursor));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void addReadoutLine(String added) {
        removeWidget(prior_lines);
        prior_lines = prior_lines.withMessage((prior_lines.getMessage().copy().append(prior_lines.getMessage().getString().isEmpty() ? added : "\n" + added)));
        if(prior_lines.getMessage().getString().length() > 8192) {
            prior_lines = prior_lines.withMessage(Component.literal(prior_lines.getMessage().getString()
                    .substring(prior_lines.getMessage().getString().length() - 8192)));
        }
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
        String lines = prior_lines.getMessage().getString();
        if(lines.length() > 8192) {
            lines = lines.substring(lines.length() - 8192);
        }
        PacketDistributor.sendToServer(new ConsolePriorLinesPayload(pos, lines));
        super.onClose();
    }
}
