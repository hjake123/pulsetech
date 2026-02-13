package dev.hyperlynx.pulsetech.core.module;

import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

/// A module to be owned by a BlockEntity, representing a buffer, a delay timer,
/// and an active state for reading from or writing to the buffer.
public abstract class SequenceModule<T extends PulseBlockEntity> {
    protected Sequence buffer = new Sequence();
    protected int delay_timer = 0;
    protected boolean active = false;

    /// Performs some action whenever this module is in an active state.
    /// The boolean value is whether it should stop being active.
    /// For example, this might check for matches with the buffer, output the buffer, or anything else.
    protected abstract boolean run(T pulser);

    public Sequence getBuffer() {
        return buffer;
    }

    public int getDelay() {
        return delay_timer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void delay(int amount) {
        delay_timer += amount;
    }

    public void reset() {
        buffer.clear();
        delay_timer = 0;
        active = false;
    }

    public void tick(ServerLevel level, T pulser) {
        assert level != null;
        if(delay_timer > 0) {
            delay_timer--;
            return;
        }
        if(isActive()){
            setActive(run(pulser));
            delay(2);
            if(!isActive()) {
                pulser.setChanged();
            }
        } else {
            if(!buffer.isEmpty()) {
                reset();
            }
        }
    }
}
