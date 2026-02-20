package dev.hyperlynx.pulsetech.feature.datasheet;

@FunctionalInterface
public interface DatasheetProvider {
    Datasheet getDatasheet();
}
