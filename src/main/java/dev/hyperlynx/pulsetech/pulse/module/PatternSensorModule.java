package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;

import java.util.Objects;

/// A SequenceModule that can update a ProtocolBlockEntity about whether its pattern was matched
public class PatternSensorModule extends SequenceModule<ProtocolBlockEntity> {
    private String last_pattern = "";

    public static final Codec<PatternSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.STRING.fieldOf("last_pattern").forGetter(PatternSensorModule::getLastPattern)
            ).apply(instance, PatternSensorModule::new)
    );

    public String getLastPattern() {
        return last_pattern;
    }

    public PatternSensorModule() {}

    private PatternSensorModule(Sequence buffer, int delay, boolean active, String last_pattern) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
        this.last_pattern = last_pattern;
    }

    @Override
    public boolean run(ProtocolBlockEntity block) {
        if(block.getProtocol() == null) {
            return false;
        }
        buffer.append(block.input());
        if(buffer.length() > block.getProtocol().sequenceLength()) {
            buffer.clear();
            last_pattern = "";
            block.handleInput();
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
                last_pattern = key;
                block.handleInput();
                // We don't return false right away to
                // allow one extra pulse to be absorbed to help with timing.
            }
        }
        return true;
    }
}
