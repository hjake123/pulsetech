package dev.hyperlynx.pulsetech.client.number;

import dev.hyperlynx.pulsetech.feature.number.NumberSelectPayload;
import dev.hyperlynx.pulsetech.feature.number.block.NumberEmitterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.N;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class NumberChooseScreen extends Screen {
    private final NumberEmitterBlockEntity emitter;
    private final BlockPos pos;
    private EditBox number_box;
    private Button submit_button;

    public NumberChooseScreen(BlockPos pos, NumberEmitterBlockEntity emitter) {
        super(Component.empty());
        this.emitter = emitter;
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        number_box = new EditBox(Minecraft.getInstance().font, this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL),
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL),
                50, 20,
                Component.empty());
        number_box.setMaxLength(4);
        number_box.setValue("" + emitter.getNumber());
        number_box.setResponder(this::updateValidity);
        addRenderableWidget(number_box);

        submit_button = Button.builder(Component.translatable("gui.pulsetech.apply"), button -> applyChangesAndClose()).build();
        submit_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL),
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 20);
        addRenderableWidget(submit_button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ENTER) {
            applyChangesAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateValidity(String s) {
        boolean valid = tryParse(s) != null;
        submit_button.active = valid;
        if(valid) {
            submit_button.setTooltip(Tooltip.create(Component.literal("")));
            return;
        }

        // Test if it's a valid integer of the wrong size
        int parsed_value;
        try {
            parsed_value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            submit_button.setTooltip(Tooltip.create(Component.translatable("gui.pulsetech.invalid_number")));
            return;
        }

        if(parsed_value > Byte.MAX_VALUE) {
            submit_button.setTooltip(Tooltip.create(Component.translatable("gui.pulsetech.number_too_big")));
            return;
        }
        if(parsed_value < Byte.MIN_VALUE) {
            submit_button.setTooltip(Tooltip.create(Component.translatable("gui.pulsetech.number_too_small")));
            return;
        }

        submit_button.setTooltip(Tooltip.create(Component.literal("???")));
    }

    private @Nullable Byte tryParse(String str) {
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void applyChangesAndClose() {
        Byte number = tryParse(number_box.getValue());
        if(number != null) {
            PacketDistributor.sendToServer(new NumberSelectPayload(pos, number));
            emitter.setNumber(number);
            Minecraft.getInstance().setScreen(null);
        }
    }
}
