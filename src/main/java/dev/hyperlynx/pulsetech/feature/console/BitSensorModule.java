package dev.hyperlynx.pulsetech.feature.console;

import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.module.SequenceModule;

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
