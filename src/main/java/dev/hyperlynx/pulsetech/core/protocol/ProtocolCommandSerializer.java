package dev.hyperlynx.pulsetech.core.protocol;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.feature.processor.MacroProtocolCommand;

/// Somewhat of a hack to allow the Processor to work.
/// Tries to serialize as either a registered or a dynamic macro command.
public class ProtocolCommandSerializer {
    public static final Codec<ProtocolCommand> CODEC = Codec.either(
            ProtocolCommands.REGISTRY.byNameCodec(),
            MacroProtocolCommand.CODEC
    ).xmap(either -> {
        if(either.left().isPresent()) {
            return either.left().orElseThrow();
        }
        return either.right().orElseThrow();
    }, Either::left);

}
