package dev.hyperlynx.pulsetech.feature.scope;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.core.module.SequenceModule;

public class RawSensorModule extends SequenceModule<PulseBlockEntity> {
    private static final int MAX_BUFFER = 14;

    public static final Codec<RawSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive)
            ).apply(instance, RawSensorModule::new)
    );

    public RawSensorModule() {}

    protected RawSensorModule(Sequence buffer, int delay, boolean active) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
    }

    @Override
    protected boolean run(PulseBlockEntity pulser) {
        buffer.append(pulser.input());
        if (buffer.length() > MAX_BUFFER) {
            buffer.removeFirst();
        }
        pulser.handleInput();
        return true;
    }
}
