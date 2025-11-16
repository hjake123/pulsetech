package dev.hyperlynx.pulsetech.pulse.protocol;

public interface Parameter<T> {
    boolean ready();
    boolean errored();
    T getValue();
}
