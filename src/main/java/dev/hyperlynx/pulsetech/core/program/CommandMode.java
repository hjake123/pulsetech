package dev.hyperlynx.pulsetech.core.program;

/// The mode of the current command being parsed. Resets with each new command.
public enum CommandMode {
    PARSE,
    DEFINE,
    SET_DELAY,
    FORGET,
    NUM,
    COLOR,
    EMIT
}

