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
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.N;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class NumberChooseScreen extends Screen {
    private final NumberEmitterBlockEntity emitter;
    private final BlockPos pos;
    private EditBox number_box;
    private Button submit_button;
    private Button plus_one_button;
    private Button plus_ten_button;
    private Button minus_one_button;
    private Button minus_ten_button;

    public NumberChooseScreen(BlockPos pos, NumberEmitterBlockEntity emitter) {
        super(Component.empty());
        this.emitter = emitter;
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        number_box = new EditBox(Minecraft.getInstance().font, this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 16,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 20,
                32, 20,
                Component.empty());
        number_box.setMaxLength(4);
        number_box.setValue("" + emitter.getNumber());
        number_box.setResponder(this::updateValidity);
        addRenderableWidget(number_box);

        submit_button = Button.builder(Component.translatable("gui.pulsetech.apply"), button -> applyChangesAndClose()).build();
        submit_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 25,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) + 2);
        submit_button.setSize(50, 20);
        addRenderableWidget(submit_button);

        plus_one_button = Button.builder(Component.translatable("gui.pulsetech.plus_one"), button -> pressModifyValue(1)).build();
        plus_one_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + 18,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 20);
        plus_one_button.setSize(16, 20);
        addRenderableWidget(plus_one_button);

        plus_ten_button = Button.builder(Component.translatable("gui.pulsetech.plus_ten"), button -> pressModifyValue(10)).build();
        plus_ten_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + 36,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 20);
        plus_ten_button.setSize(24, 20);
        addRenderableWidget(plus_ten_button);

        minus_one_button = Button.builder(Component.translatable("gui.pulsetech.minus_one"), button -> pressModifyValue(-1)).build();
        minus_one_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 34,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 20);
        minus_one_button.setSize(16, 20);
        addRenderableWidget(minus_one_button);

        minus_ten_button = Button.builder(Component.translatable("gui.pulsetech.minus_ten"), button -> pressModifyValue(-10)).build();
        minus_ten_button.setPosition(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 60,
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL) - 20);
        minus_ten_button.setSize(24, 20);
        addRenderableWidget(minus_ten_button);

        updateValidity(number_box.getValue());
    }

    private void pressModifyValue(int amount) {
        Byte prior_value = tryParse(number_box.getValue());
        if(prior_value == null) {
            return;
        }
        number_box.setValue("" + (prior_value + amount));
        updateValidity(number_box.getValue());
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
        Byte value = tryParse(s);
        boolean valid = value != null;
        submit_button.active = valid;
        if(valid) {
            submit_button.setTooltip(Tooltip.create(Component.literal("")));
            plus_one_button.active = value < Byte.MAX_VALUE;
            plus_ten_button.active = (value + 10) <= Byte.MAX_VALUE;
            minus_one_button.active = value > Byte.MIN_VALUE;
            minus_ten_button.active = (value - 10) >= Byte.MIN_VALUE;
            return;
        }
        plus_one_button.active = false;
        plus_ten_button.active = false;
        minus_one_button.active = false;
        minus_ten_button.active = false;

        // Test if it's a valid integer of the wrong size for tooltip
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
