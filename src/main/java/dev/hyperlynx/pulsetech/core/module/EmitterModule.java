package dev.hyperlynx.pulsetech.core.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.Sequence;

/// A SequenceModule that contains an output cursor.
/// When ticked, if it is active, it will call the given PulseBlockEntity's output methods
/// with appropriate cadence to output its entire buffer.
public class EmitterModule extends SequenceModule<PulseBlockEntity> {
    protected int output_cursor = 0;
    protected boolean output_initialized = false;

    public static final Codec<EmitterModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.INT.fieldOf("output_cursor").forGetter(EmitterModule::getCursor),
                    Codec.BOOL.fieldOf("output_initialized").forGetter(EmitterModule::outputInitialized)
            ).apply(instance, EmitterModule::new)
    );

    public int getCursor() {
        return output_cursor;
    }

    public boolean outputInitialized() {
        return output_initialized;
    }

    public EmitterModule() {}

    protected EmitterModule(Sequence buffer, int delay, boolean active, int output_cursor, boolean output_initialized) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
        this.output_cursor = output_cursor;
        this.output_initialized = output_initialized;
    }

    public void enqueueTransmission(Sequence sequence) {
        buffer.append(true);
        buffer.appendAll(sequence);
        buffer.append(false);
    }

    public void enqueueWithoutHeader(Sequence sequence) {
        this.buffer.appendAll(sequence);
        buffer.append(false);
    }

    @Override
    public boolean run(PulseBlockEntity pulser) {
        if(!output_initialized) {
            output_cursor = 0;
            output_initialized = true;
        }
        pulser.output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        output_initialized = false;
        return false;
    }
}
