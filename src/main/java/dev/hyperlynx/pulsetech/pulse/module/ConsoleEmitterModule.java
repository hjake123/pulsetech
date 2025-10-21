package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.block.entity.ConsoleBlockEntity;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;
import dev.hyperlynx.pulsetech.pulse.data.ProtocolData;
import dev.hyperlynx.pulsetech.util.MapListPairConverter;
import net.minecraft.core.UUIDUtil;

import java.util.*;

public class ConsoleEmitterModule extends EmitterModule {
    public Map<Integer, Short> delay_points = new HashMap<>();
    public boolean looping = false;
    private static final MapListPairConverter<Integer, Short> converter = new MapListPairConverter<>();

    public static final Codec<ConsoleEmitterModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.INT.fieldOf("output_cursor").forGetter(EmitterModule::getCursor),
                    Codec.BOOL.fieldOf("output_initialized").forGetter(EmitterModule::outputInitialized),
                    Codec.pair(
                            Codec.INT.fieldOf("time").codec(),
                            Codec.SHORT.fieldOf("length").codec()
                    ).listOf().xmap(
                            converter::toMap,
                            converter::fromMap
                    ).fieldOf("delays").forGetter(ConsoleEmitterModule::getDelays),
                    Codec.BOOL.fieldOf("looping").forGetter(ConsoleEmitterModule::isLooping)
            ).apply(instance, ConsoleEmitterModule::new)
    );

    private Map<Integer, Short> getDelays() {
        return delay_points;
    }

    private boolean isLooping() {
        return looping;
    }

    public ConsoleEmitterModule() {}

    protected ConsoleEmitterModule(Sequence buffer, int delay, boolean active, int output_cursor, boolean output_initialized, Map<Integer, Short> delays, boolean looping) {
        super(buffer, delay, active, output_cursor, output_initialized);
        this.delay_points = new HashMap<>(delays);
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
