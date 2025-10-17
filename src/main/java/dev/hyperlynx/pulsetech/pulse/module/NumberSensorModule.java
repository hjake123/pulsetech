package dev.hyperlynx.pulsetech.pulse.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.Protocol;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.pulse.block.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

/// A SequenceModule that can scans in a number from its ProtcolBlockEntity's input
public class NumberSensorModule extends SequenceModule<ProtocolBlockEntity> {
    private short number = 0;

    public static final Codec<NumberSensorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Sequence.CODEC.fieldOf("buffer").forGetter(SequenceModule::getBuffer),
                    Codec.INT.fieldOf("delay_timer").forGetter(SequenceModule::getDelay),
                    Codec.BOOL.fieldOf("active").forGetter(SequenceModule::isActive),
                    Codec.SHORT.fieldOf("number").forGetter(NumberSensorModule::getNumber)
            ).apply(instance, NumberSensorModule::new)
    );

    public NumberSensorModule() {}

    private NumberSensorModule(Sequence buffer, int delay, boolean active, short number) {
        this.buffer = buffer;
        this.delay_timer = delay;
        this.setActive(active);
        this.number = number;
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
    public boolean run(ProtocolBlockEntity block) {
        if(block.getProtocol() == null) {
            return false;
        }
        if(buffer.length() < block.getProtocol().numberSequenceLength()) {
            number = 0;
            buffer.append(block.input());
            if(buffer.length() == block.getProtocol().sequenceLength()) {
                // We can now test for the NUM sequence. If it's absent, we need to fail out.
                if(!Objects.equals(block.getProtocol().sequenceFor(Protocol.NUM), buffer)) {
                    buffer.clear();
                    delay(6); // magic number!
                    return false;
                }
                Pulsetech.LOGGER.debug("Matched NUM, getting ready for input");
            }
            if(buffer.length() == block.getProtocol().numberSequenceLength()) {
                Pulsetech.LOGGER.debug("Parsing sequence {} for short", buffer);
                number = block.getProtocol().toShort(buffer);
                block.handleInput();
                return false;
            }
            return true;
        }
        return false;
    }
}
