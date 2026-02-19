package dev.hyperlynx.pulsetech.client.console;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.feature.console.*;
import dev.hyperlynx.pulsetech.feature.console.block.ConsoleBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleScreen extends Screen {
    private BetterFittingMultiLineTextWidget prior_lines;
    private EditBox command_box;
    private Button copy_macro_button;
    private Button paste_macro_button;

    private final BlockPos pos;
    private final String prior_lines_str;
    private int past_commands_cursor = -1;
    private final ConsoleColor color;

    private final List<String> past_commands = new ArrayList<>();
    private final String initial_command_box_text;

    public ConsoleScreen(BlockPos pos, String lines, String command_box_text) {
        super(Component.translatable("block.pulsetech.console"));
        this.pos = pos;
        this.prior_lines_str = lines;
        past_commands.addAll(Arrays.stream(prior_lines_str.split("\n")).filter(line -> line.startsWith(">")).map(line -> line.substring(2)).toList());
        initial_command_box_text = command_box_text;

        if (Minecraft.getInstance().level.getBlockState(pos).getBlock() instanceof ConsoleBlock console_block) {
            this.color = console_block.getColor();
        } else {
            this.color = ConsoleColor.REDSTONE;
        }
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
        command_box.setValue(initial_command_box_text);
        addRenderableWidget(command_box);
        prior_lines = new BetterFittingMultiLineTextWidget(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - (console_width / 2),this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - (console_height / 2) - 10, console_width, console_height, Component.literal(prior_lines_str), font);
        prior_lines.scrollToBottom();
        addRenderableWidget(prior_lines);
        setInitialFocus(command_box);
        setupTextColors();

        copy_macro_button = Button.builder(Component.literal("copy"), button -> {
            PacketDistributor.sendToServer(new ConsoleClipboardCopyPayload(pos, ""));
        }).pos(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + (console_width / 2), getRectangle().getCenterInAxis(ScreenAxis.VERTICAL)).build();
        addRenderableWidget(copy_macro_button);

        paste_macro_button = Button.builder(Component.literal("paste"), button -> {
            PacketDistributor.sendToServer(new ConsoleClipboardPastePayload(pos, Minecraft.getInstance().keyboardHandler.getClipboard()));
        }).pos(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + (console_width / 2), getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 20).build();
        addRenderableWidget(paste_macro_button);

    }

    private static final int AMBER_COLOR = 0xFFE09E;
    private static final int RED_COLOR = 0xEC1400;
    private static final int GREEN_COLOR = 0x00A54A;
    private static final int INDIGO_COLOR = 0x703DFF;

    private void setupTextColors() {
        int color_value = switch(color) {
            case AMBER -> AMBER_COLOR;
            case REDSTONE -> RED_COLOR;
            case GREEN -> GREEN_COLOR;
            case INDIGO -> INDIGO_COLOR;
            default -> 0xFFFFFF;
        };
        prior_lines.setColor(color_value);
        command_box.setTextColor(color_value);
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
        setupTextColors();
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setPriorLines(String lines, String command_box_text) {
        removeWidget(prior_lines);
        prior_lines = prior_lines.withMessage(Component.literal(lines));
        prior_lines.scrollToBottom();
        addRenderableWidget(prior_lines);
        setupTextColors();
        command_box.setValue(command_box_text);
    }

    @Override
    public void onClose() {
        String lines = prior_lines.getMessage().getString();
        if(lines.length() > 8192) {
            lines = lines.substring(lines.length() - 8192);
        }
        PacketDistributor.sendToServer(new ConsolePriorLinesPayload(pos, lines, command_box.getValue()));
        super.onClose();
    }
}
