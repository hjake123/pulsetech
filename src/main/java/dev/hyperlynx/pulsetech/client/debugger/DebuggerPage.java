package dev.hyperlynx.pulsetech.client.debugger;

import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoRequest;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class DebuggerPage implements Renderable {
    protected final int id;
    protected final BlockPos pos;
    protected final String title;
    protected final int x;
    protected final int y;

    public DebuggerPage(BlockPos pos, int id, String title, int x, int y) {
        this.id = id;
        this.pos = pos;
        this.x = x;
        this.y = y;
        this.title = title;
    }

    public void update() {
        PacketDistributor.sendToServer(new DebuggerInfoRequest(pos, id));
    }

    public abstract void acceptInfo(Object info);
}
