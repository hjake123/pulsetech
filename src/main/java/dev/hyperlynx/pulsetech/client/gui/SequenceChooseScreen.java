package dev.hyperlynx.pulsetech.client.gui;

import dev.hyperlynx.pulsetech.pulse.PatternHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class SequenceChooseScreen extends Screen {
    private final InputSequenceWidget sequence;

    public SequenceChooseScreen(BlockPos pos, PatternHolder bearer) {
        super(Component.empty());
        sequence = new InputSequenceWidget(10, 10, 100, 100, Component.empty());
        addWidget(sequence);
    }
}
