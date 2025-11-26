package dev.hyperlynx.pulsetech.core.protocol;

import java.util.List;

public record ExecutionContext(ProtocolBlockEntity block, List<Short> params) {}
