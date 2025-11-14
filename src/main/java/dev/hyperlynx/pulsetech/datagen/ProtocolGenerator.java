package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Protocols;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class ProtocolGenerator {
    public static RegistrySetBuilder get() {
        return new RegistrySetBuilder().add(
                Protocols.KEY,
                bootstrap -> {
                    addProtocol(bootstrap, "debug", 4, Map.of(
                            "A", new Sequence(true, false, true, false),
                            "B", new Sequence(false, true, true, false),
                            "C", new Sequence(false, false, true, true),
                            Protocol.NUM, new Sequence(true, true, false, true)
                    ));
                }
        );
    }

    private static void addProtocol(BootstrapContext<Protocol> bootstrap, String id, int sequence_length, Map<String, Sequence> terms) {
        bootstrap.register(ResourceKey.create(Protocols.KEY, Pulsetech.location(id)),
                new Protocol(sequence_length, terms)
        );
    }
}
