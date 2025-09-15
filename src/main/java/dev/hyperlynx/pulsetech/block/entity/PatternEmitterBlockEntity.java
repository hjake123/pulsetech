package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class PatternEmitterBlockEntity extends ProtocolBlockEntity {
    public PatternEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_EMITTER.get(), pos, blockState);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;
    private String emission = "";

    public void setEmission(String emission) {
        this.emission = emission;
    }

    public String getEmission() {
        return emission;
    }

    public void rotateEmission() {
        // TODO temporary logic
        emission = protocol.nextKey(emission);
    }

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        if(!output_initialized) {
            buffer = protocol.sequenceFor(emission);
            if(buffer == null) {
                Pulsetech.LOGGER.error("No sequence for pattern {}. If this keeps repeating, report it!", emission);
                return true;
            }
            output_cursor = 0;
            output_initialized = true;
            output(true);
            delay(1);
            return true;
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        output_initialized = false;
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        output(false);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        emission = tag.getString("Emission");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Emission", emission);
    }
}
