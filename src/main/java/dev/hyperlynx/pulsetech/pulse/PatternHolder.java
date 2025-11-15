package dev.hyperlynx.pulsetech.pulse;

/// An interface to be implemented by BlockEntities that want to have a single specific sequence they know,
/// for example a Sequence Detector would use this to hold onto its trigger sequence
public interface PatternHolder {
    Sequence getPattern();
    void setPattern(Sequence sequence);
}
