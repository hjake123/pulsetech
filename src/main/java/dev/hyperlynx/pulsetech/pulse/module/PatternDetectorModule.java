package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;

import java.util.Objects;

/// A SequenceModule that can update a PatternBlockEntity about whether its pattern was matched
public class PatternDetectorModule extends SequenceModule<PatternBlockEntity> {
    public static final Codec<PatternDetectorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive)
            ).apply(instance, PatternDetectorModule::new)
    );

    public PatternDetectorModule() {}

    private PatternDetectorModule(Sequence buffer, int delay, boolean active) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
    }

    @Override
    public boolean run(PatternBlockEntity block) {
        if(block.getProtocol() == null) {
            return false;
        }
        buffer.append(block.input());
        if(buffer.length() > block.getProtocol().sequenceLength()) {
            buffer.clear();
            block.output(false);
            return false;
        } else if (buffer.length() == block.getProtocol().sequenceLength()) {
            if(Objects.equals(block.getProtocol().sequenceFor(Protocol.NUM), buffer)) {
                Pulsetech.LOGGER.debug("Matched NUM, sleeping...");
                delay(46);
                return false;
            }
            Pulsetech.LOGGER.debug("Checking for match with {}", buffer);
            String key = block.getProtocol().keyFor(buffer);
            if(key != null) {
                Pulsetech.LOGGER.debug("Matched pattern with key {}", key);
                block.output(key.equals(block.getPattern()));
                // We don't return false right away to
                // allow one extra pulse to be absorbed to help with timing.
            }
        }
        return true;
    }
}
