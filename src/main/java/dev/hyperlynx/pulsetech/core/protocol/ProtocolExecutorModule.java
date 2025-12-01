package dev.hyperlynx.pulsetech.core.protocol;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.NumberSensorModule;
import dev.hyperlynx.pulsetech.core.module.SequenceModule;
import dev.hyperlynx.pulsetech.feature.datasheet.DatasheetEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProtocolExecutorModule extends SequenceModule<ProtocolBlockEntity> {
    private State state = State.AWAIT_COMMAND;
    @Nullable private ProtocolCommand active_command = null;
    private final List<Byte> active_parameters = new ArrayList<>();
    private final NumberSensorModule parameter_sensor = new NumberSensorModule();

    private enum State {
        AWAIT_COMMAND,
        AWAIT_PARAMETER,
        RUN
    }

    public @Nullable Protocol fetchProtocol(ProtocolBlockEntity block) {
        assert block.getType().builtInRegistryHolder() != null;
        return block.getType().builtInRegistryHolder().getData(ProtocolDataMap.TYPE);
    }

    public static final Codec<ProtocolExecutorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive)
            ).apply(instance, ProtocolExecutorModule::new)
    ); // TODO Add the new fields !

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
                        buffer.clear();
                        state = command.parameterCount() > 0 ? State.AWAIT_PARAMETER : State.RUN;
                        return false;
                    }
                }
            }
            case AWAIT_PARAMETER -> {
                assert active_command != null;
                parameter_sensor.run(pulser);
                if(parameter_sensor.checkNewNumberReady()) {
                    active_parameters.add(parameter_sensor.getNumber());
                    parameter_sensor.reset();
                    if(active_parameters.size() == active_command.parameterCount()) {
                        state = State.RUN;
                    } else {
                        return false;
                    }
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
