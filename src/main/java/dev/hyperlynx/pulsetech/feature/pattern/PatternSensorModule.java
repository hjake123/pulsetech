package dev.hyperlynx.pulsetech.feature.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PatternHolder;
import dev.hyperlynx.pulsetech.core.module.SequenceModule;
import dev.hyperlynx.pulsetech.feature.pattern.block.PatternBlockEntity;

public class PatternSensorModule extends SequenceModule<PatternBlockEntity> implements PatternHolder {
    Sequence pattern = new Sequence();

    public static final Codec<PatternSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Sequence.CODEC.fieldOf("pattern").forGetter(PatternSensorModule::getPattern)
            ).apply(instance, PatternSensorModule::new)
    );

    public PatternSensorModule() {}
    public PatternSensorModule(Sequence buffer, int delay, boolean active, Sequence pattern) {
        this.buffer = buffer;
        delay_timer = delay;
        this.active = active;
        this.pattern = pattern;
    }

    @Override
    protected boolean run(PatternBlockEntity pulser) {
        buffer.append(pulser.input());
        if(buffer.length() > pattern.length()) {
            buffer.clear();
            pulser.handleInput();
            return false;
        } else if (buffer.length() == pattern.length()) {
            Pulsetech.LOGGER.debug("Checking for match with {}", buffer);
            if(buffer.equals(pattern)) {
                Pulsetech.LOGGER.debug("Matched pattern");
                pulser.handleInput();
                // We don't return false right away to
                // allow one extra pulse to be absorbed to help with timing.
            }
        }
        return true;
    }



    @Override
    public Sequence getPattern() {
        return pattern;
    }

    @Override
    public void setPattern(Sequence sequence) {
        pattern = sequence;
    }

    public boolean bufferMatchesPattern() {
        return buffer.equals(pattern);
    }
}
