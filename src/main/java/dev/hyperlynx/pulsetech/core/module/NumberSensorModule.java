package dev.hyperlynx.pulsetech.core.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;

/// A SequenceModule that can scans in a number from its ProtocolBlockEntity's input
public class NumberSensorModule extends SequenceModule<PulseBlockEntity> {
    private byte number = 0;
    private boolean ready = false;

    public static final Codec<NumberSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.BYTE.fieldOf("number").forGetter(NumberSensorModule::getNumber),
                    Codec.BOOL.fieldOf("ready").forGetter(NumberSensorModule::peekNewNumberReady)
            ).apply(instance, NumberSensorModule::new)
    );

    public NumberSensorModule() {}

    private NumberSensorModule(Sequence buffer, int delay, boolean active, byte number, boolean ready) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
        this.number = number;
        this.ready = ready;
    }

    public byte getNumber() {
        return number;
    }

    @Override
    public void reset() {
        super.reset();
        // number = 0;
    }

    @Override
    public boolean run(PulseBlockEntity block) {
        if(buffer.length() <= 8) {
            number = 0;
            buffer.append(block.input());
            if(buffer.length() == 8) {
                number = buffer.toByte();
                ready = true;
                block.handleInput();
                delay(2);
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
