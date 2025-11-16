package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.protocol.Protocol;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolCommand;
import dev.hyperlynx.pulsetech.pulse.protocol.ProtocolDataMap;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProtocolExecutorModule extends SequenceModule<ProtocolBlockEntity> {
    private @Nullable Protocol fetchProtocol(ProtocolBlockEntity block) {
        assert block.getType().builtInRegistryHolder() != null;
        return block.getType().builtInRegistryHolder().getData(ProtocolDataMap.TYPE);
    }

    public static final Codec<ProtocolExecutorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive)
            ).apply(instance, ProtocolExecutorModule::new)
    );

    public ProtocolExecutorModule(ProtocolBlockEntity block) {
        Protocol protocol = fetchProtocol(block);
        if(protocol != null) {
            protocol.getCommands().forEach((command, sequence) -> command.ownerInit(block));
        }
    }

    public ProtocolExecutorModule(Sequence buffer, int delay, boolean active) {
        this.buffer = buffer;
        delay_timer = delay;
        this.active = active;
    }

    @Override
    public boolean run(ProtocolBlockEntity pulser) {
        Protocol protocol = fetchProtocol(pulser);
        if(protocol == null) {
            Pulsetech.LOGGER.error("No protocol loaded for {}", pulser.getType().builtInRegistryHolder().getKey().location());
            return false;
        }
        buffer.append(pulser.input());
        if(buffer.length() > protocol.sequenceLength()) {
            buffer.clear();
            pulser.handleInput();
            return false;
        } else if (buffer.length() == protocol.sequenceLength()) {
            ProtocolCommand command = protocol.getCommand(buffer);
            if(command != null) {
                command.run(pulser);
            }
        }
        return true;
    }
}
