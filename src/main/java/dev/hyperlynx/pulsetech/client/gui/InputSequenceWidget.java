package dev.hyperlynx.pulsetech.client.gui;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputSequenceWidget extends AbstractWidget implements Renderable {
    private final Sequence sequence;
    private Sequence last_sequence = null;
    private final List<SpriteIconButton> bit_buttons = new ArrayList<>();
    private SpriteIconButton add_button;
    private SpriteIconButton remove_button;

    public InputSequenceWidget(int x, int y, int width, int height, Sequence sequence) {
        super(x, y, width, height, Component.empty());
        this.sequence = sequence;
        tick();
    }

    public void tick() {
        if(Objects.equals(last_sequence, sequence) && !sequence.isEmpty()) {
            return;
        }
        add_button = new SpriteIconButton.Builder(Component.empty(), b -> bitAppendButtonPress(), true)
                .sprite(Pulsetech.location("bit_button_add"), 20, 16)
                .size(20, 16)
                .build();
        remove_button = new SpriteIconButton.Builder(Component.empty(), b -> bitRemoveButtonPress(), true)
                .sprite(Pulsetech.location("bit_button_remove"), 20, 16)
                .size(20, 16)
                .build();
        last_sequence = new Sequence(sequence);
        int x = this.getX() - (sequence.length() * 12) - 6;
        bit_buttons.clear();
        for(int i = 0; i < sequence.length(); i++) {
            SpriteIconButton button;
            if(sequence.get(i)) {
                int index = i;
                button = new SpriteIconButton.Builder(Component.empty(), b -> bitButtonPress(index), true)
                        .sprite(Pulsetech.location("bit_button_on"), 20, 32)
                        .size(20, 32)
                        .build();
            } else {
                int index = i;
                button = new SpriteIconButton.Builder(Component.empty(), b -> bitButtonPress(index), true)
                        .sprite(Pulsetech.location("bit_button_off"), 20, 32)
                        .size(20, 32)
                        .build();
            }
            button.setPosition(x, this.getY());
            x += 24;
            bit_buttons.add(button);
        }
        add_button.setPosition(x, this.getY());
        remove_button.setPosition(x, this.getY() + 16);
    }

    private void bitButtonPress(int index) {
        sequence.set(index, !sequence.get(index));
        clickSound(sequence.get(index) ? 1.0F : 0.8F);
    }

    private void bitAppendButtonPress() {
        sequence.append(false);
        last_sequence = null;
        clickSound(1.0F + (sequence.length() / 36.0F));
    }

    private void bitRemoveButtonPress() {
        sequence.removeLast();
        last_sequence = null;
        clickSound(0.7F + (sequence.length() / 36.0F));
    }

    private void clickSound(float pitch) {
        assert Minecraft.getInstance().player != null;
        assert Minecraft.getInstance().level != null;
        Minecraft.getInstance().player.playSound(SoundEvents.LEVER_CLICK, 0.5F, (float) (pitch + Minecraft.getInstance().level.random.nextFloat() * 0.1));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for(Button button : bit_buttons) {
            button.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
        add_button.render(guiGraphics, mouseX, mouseY, partialTicks);
        remove_button.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        for(Button bit_button : bit_buttons) {
            if(bit_button.isMouseOver(mouseX, mouseY)) {
                bit_button.onClick(mouseX, mouseY);
                return true;
            }
        }
        if(add_button.isMouseOver(mouseX, mouseY)) {
            add_button.onClick(mouseX, mouseY);
            return true;
        }
        if(remove_button.isMouseOver(mouseX, mouseY)) {
            remove_button.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.sequence_widget"));
    }

    public Sequence getSequence() {
        return sequence;
    }
}
