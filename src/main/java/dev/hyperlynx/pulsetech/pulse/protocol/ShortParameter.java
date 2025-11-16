package dev.hyperlynx.pulsetech.pulse.protocol;

import dev.hyperlynx.pulsetech.pulse.module.NumberSensorModule;

import javax.annotation.Nullable;

/// A number Parameter for a Verb. Currently, the only kind of Parameter.
public class ShortParameter implements Parameter<Short> {
    private final NumberSensorModule input;
    private boolean queried = false;
    public ShortParameter(NumberSensorModule input) {
        this.input = input;
    }

    public boolean ready() {
        queried = true;
        return input.peekNewNumberReady();
    }

    public boolean errored() {
        return queried && !input.isActive();
    }

    public @Nullable Short getValue() {
        if(!ready()) {
            queried = true;
            return null;
        }
        queried = false;
        return input.getNumber();
    }
}
