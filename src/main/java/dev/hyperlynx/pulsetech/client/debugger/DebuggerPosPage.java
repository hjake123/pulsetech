package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DebuggerPosPage extends DebuggerPage {
    private static final ResourceLocation ORIGIN = Pulsetech.location("pos_origin");
    private static final ResourceLocation CURSOR = Pulsetech.location("pos_cursor");
    private static final ResourceLocation COMPASS_ROSE = Pulsetech.location("compass_rose");
    private static final ResourceLocation Y_AXIS_METER = Pulsetech.location("textures/gui/y_axis_meter.png");
    private static final ResourceLocation Y_CURSOR = Pulsetech.location("y_cursor");
    private BlockPos pos = BlockPos.ZERO;

    public DebuggerPosPage(BlockPos pos, int id, String title, int x, int y) {
        super(pos, id, title, x, y);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        graphics.blit(Y_AXIS_METER, x, y - 4, 0, 0, 172, 108, 172, 108);
        graphics.drawString(Minecraft.getInstance().font, title, x, y, 0xFF0000, false);
        graphics.blitSprite(ORIGIN, x + 82, y + 50, 8, 8);
        graphics.blitSprite(COMPASS_ROSE, x, y + 78, 21, 21);

        int cursor_display_x = Math.min(Math.max(pos.getX(), -8), 8) * 8;
        int cursor_display_y = Math.min(Math.max(pos.getZ(), -5), 4) * 8;
        graphics.blitSprite(CURSOR, x + 82 + cursor_display_x, y + 50 + cursor_display_y, 8, 8);

        int y_cursor_y = Math.min(Math.max(pos.getY(), -10), 11) * -4;
        graphics.blitSprite(Y_CURSOR, x + 155, y + 48 + y_cursor_y, 10, 7);

        graphics.drawCenteredString(Minecraft.getInstance().font, pos.toShortString(), x + 86, y + 94, 0xFF0000);
    }

    @Override
    public void acceptInfo(Object info) {
        if(info instanceof BlockPos pos) {
            this.pos = pos;
        }
    }

}
