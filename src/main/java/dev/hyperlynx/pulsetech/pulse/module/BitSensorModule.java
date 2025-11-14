package dev.hyperlynx.pulsetech.pulse.module;

import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;

public class BitSensorModule extends SequenceModule<PulseBlockEntity> {
    private boolean input = false;
    private boolean has_input = false;

    public boolean ready() {
        return has_input;
    }

    public boolean read() {
        return input;
    }

    @Override
    protected boolean run(PulseBlockEntity pulser) {
        input = pulser.input();
        has_input = true;
        pulser.handleInput();
        return true;
    }
}
