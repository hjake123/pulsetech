package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;

import java.util.Objects;

/// A SequenceModule that can scans in a number from its ProtocolBlockEntity's input
public class NumberSensorModule extends SequenceModule<PulseBlockEntity> {
    private short number = 0;
    private boolean ready = false;

    public static final Codec<NumberSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.SHORT.fieldOf("number").forGetter(NumberSensorModule::getNumber),
                    Codec.BOOL.fieldOf("ready").forGetter(NumberSensorModule::peekNewNumberReady)
            ).apply(instance, NumberSensorModule::new)
    );

    public NumberSensorModule() {}

    private NumberSensorModule(Sequence buffer, int delay, boolean active, short number, boolean ready) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
        this.number = number;
        this.ready = ready;
    }

    public short getNumber() {
        return number;
    }

    @Override
    public void reset() {
        super.reset();
        number = 0;
    }

    @Override
    public boolean run(PulseBlockEntity block) {
        if(buffer.length() <= 16) {
            number = 0;
            buffer.append(block.input());
            if(buffer.length() == 16) {
                Pulsetech.LOGGER.debug("Parsing sequence {} for short", buffer);
                number = buffer.toShort();
                ready = true;
                block.handleInput();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean peekNewNumberReady() {
        return ready;
    }

    public boolean checkNewNumberReady() {
        if(ready) {
            ready = false;
            return true;
        }
        return false;
    }
}
