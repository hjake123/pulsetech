package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.block.entity.ConsoleBlockEntity;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleEmitterModule extends EmitterModule {
    public Map<Integer, Short> delay_points = new HashMap<>();
    public boolean looping = false;

    public static final Codec<ConsoleEmitterModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.INT.fieldOf("output_cursor").forGetter(EmitterModule::getCursor),
                    Codec.BOOL.fieldOf("output_initialized").forGetter(EmitterModule::outputInitialized),
                    Codec.INT.listOf().fieldOf("delay_times").forGetter(ConsoleEmitterModule::getDelayTimes),
                    Codec.SHORT.listOf().fieldOf("delay_lengths").forGetter(ConsoleEmitterModule::getDelayLengths),
                    Codec.BOOL.fieldOf("looping").forGetter(ConsoleEmitterModule::isLooping)
            ).apply(instance, ConsoleEmitterModule::new)
    );

    private List<Integer> getDelayTimes() {
        return delay_points.keySet().stream().toList();
    }

    private List<Short> getDelayLengths() {
        return delay_points.values().stream().toList();
    }

    private boolean isLooping() {
        return looping;
    }

    public ConsoleEmitterModule() {}

    protected ConsoleEmitterModule(Sequence buffer, int delay, boolean active, int output_cursor, boolean output_initialized, List<Integer> delay_times, List<Short> delay_lengths, boolean looping) {
        super(buffer, delay, active, output_cursor, output_initialized);
        for(int i = 0; i < delay_times.size(); i++) {
            delay_points.put(delay_times.get(i), delay_lengths.get(i));
        }
        this.looping = looping;
    }

    @Override
    public boolean run(PulseBlockEntity pulser) {
        if(!output_initialized) {
            output_cursor = 0;
            output_initialized = true;
        }
        if(delay_points.containsKey(output_cursor)) {
            delay(delay_points.get(output_cursor));
            output_cursor++;
            return true;
        }
        pulser.output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        if(looping) {
            delay(4);
            output_cursor = 0;
            return true;
        }
        output_initialized = false;
        delay_points.clear();
        return false;
    }
}
