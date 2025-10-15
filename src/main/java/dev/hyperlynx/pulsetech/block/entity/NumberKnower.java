package dev.hyperlynx.pulsetech.block.entity;

public interface NumberKnower {
    short getNumber();
    default String getOverrideMessage() {
        return null;
    }
}
