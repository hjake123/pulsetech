package dev.hyperlynx.pulsetech.client.gui;

import dev.hyperlynx.pulsetech.net.SequenceSelectPayload;
import dev.hyperlynx.pulsetech.pulse.PatternHolder;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SequenceChooseScreen extends Screen {
    private InputSequenceWidget sequence_widget;
    private final PatternHolder holder;
    private final BlockPos pos;

    public SequenceChooseScreen(BlockPos pos, PatternHolder bearer) {
        super(Component.empty());
        this.holder = bearer;
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        sequence_widget = new InputSequenceWidget(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL),
                this.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL),
                200, 100, holder.getPattern());
        addRenderableWidget(sequence_widget);
    }

    @Override
    public void tick() {
        super.tick();
        sequence_widget.tick();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return sequence_widget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new SequenceSelectPayload(pos, sequence_widget.getSequence()));
    }
}
