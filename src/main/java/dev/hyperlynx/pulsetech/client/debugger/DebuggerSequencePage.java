package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.client.SequenceDisplayWidget;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DebuggerSequencePage extends DebuggerPage {
    List<SequenceDisplayWidget> sequence_displays = new ArrayList<>();

    public DebuggerSequencePage(BlockPos pos, int id, String title, int x, int y) {
        super(pos, id, title, x, y);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        graphics.drawString(Minecraft.getInstance().font, title, x, y, 0xFF0000, false);
        for(SequenceDisplayWidget widget : sequence_displays) {
            widget.render(graphics, i, i1, v);
        }
    }

    private final int LINE_LENGTH = 10;

    @Override
    public void acceptInfo(Object info) {
        if(info instanceof Sequence sequence) {
            sequence_displays.clear();
            Sequence current = new Sequence();
            int y_cursor = y;
            for(int i = 0; i < sequence.length(); i++) {
                if(i % LINE_LENGTH == 0 && !current.isEmpty()) {
                    var widget = new SequenceDisplayWidget(x, y_cursor, 20, 20);
                    widget.setSequence(new Sequence(current));
                    sequence_displays.add(widget);
                    current.clear();
                    y_cursor += 16;
                }
                current.append(sequence.get(i));
            }
            var widget = new SequenceDisplayWidget(x, y_cursor, 20, 20);
            widget.setSequence(new Sequence(current));
            sequence_displays.add(widget);
        }
    }

}
