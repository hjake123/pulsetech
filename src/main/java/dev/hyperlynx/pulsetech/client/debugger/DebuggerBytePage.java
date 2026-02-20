package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.client.SequenceDisplayWidget;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;

public class DebuggerBytePage extends DebuggerPage {
    private final SequenceDisplayWidget byte_sequence_display;
    private byte number = 0;

    public DebuggerBytePage(BlockPos pos, int id, String title, int x, int y) {
        super(pos, id, title, x, y);
        this.byte_sequence_display = new SequenceDisplayWidget(x + 16, y + 40, 20, 20);
        byte_sequence_display.setSequence(Sequence.fromByte((byte) 0));
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        graphics.drawString(Minecraft.getInstance().font, title, x, y, 0xFF0000, false);
        byte_sequence_display.render(graphics, i, i1, v);
        graphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(number), x + 82, y + 35, 0xFF0000);
    }

    @Override
    public void acceptInfo(Object info) {
        if(info instanceof Byte b) {
            number = b;
            byte_sequence_display.setSequence(Sequence.fromByte(b));
        }
    }

}
