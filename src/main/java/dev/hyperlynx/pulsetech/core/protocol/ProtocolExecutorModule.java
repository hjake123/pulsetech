package dev.hyperlynx.pulsetech.core.protocol;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.core.module.SequenceModule;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProtocolExecutorModule extends SequenceModule<ProtocolBlockEntity> {
    private State state = State.AWAIT_COMMAND;
    @Nullable private ProtocolCommand active_command = null;
    private List<Short> active_parameters = new ArrayList<>();

    private enum State {
        AWAIT_COMMAND,
        AWAIT_PARAMETER,
        RUN
    }

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

    public ProtocolExecutorModule() {}

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

        switch(state) {
            case AWAIT_COMMAND -> {
                buffer.append(pulser.input());
                if(buffer.length() > protocol.sequenceLength()) {
                    buffer.clear();
                    pulser.handleInput();
                    return false;
                } else if (buffer.length() == protocol.sequenceLength()) {
                    ProtocolCommand command = protocol.getCommand(buffer);
                    if(command != null) {
                        active_command = command;
                        active_parameters.clear();
                        state = State.AWAIT_PARAMETER;
                    }
                }
            }
            case AWAIT_PARAMETER -> {
                assert active_command != null;
                buffer.append(pulser.input());
                if(buffer.length() > 16) {
                    buffer.clear();
                    pulser.handleInput();
                    state = State.AWAIT_COMMAND;
                    active_command = null;
                    return false;
                } else if (buffer.length() == 16) {
                    active_parameters.add(buffer.toShort());
                }

                if(active_parameters.size() == active_command.parameterCount()) {
                    state = State.RUN;
                }
            }

            case RUN -> {
                active_command.run(new ExecutionContext(pulser, active_parameters));
                active_command = null;
                active_parameters.clear();
                state = State.AWAIT_COMMAND;
                return false;
            }
        }
        return true;
    }
}
