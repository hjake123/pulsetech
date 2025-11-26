package dev.hyperlynx.pulsetech.core.protocol;

public interface Parameter<T> {
    boolean ready();
    boolean errored();
    T getValue();
}
