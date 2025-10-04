package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class PatternDetectorBlockEntity extends PatternBlockEntity {
    public PatternDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_DETECTOR.get(), pos, blockState);
    }

    @Override
    protected boolean run() {
        if(protocol == null) {
            return false;
        }
        buffer.append(input());
        if(buffer.length() > protocol.sequenceLength()) {
            buffer.clear();
            output(false);
            return false;
        } else if (buffer.length() == protocol.sequenceLength()) {
            if(Objects.equals(protocol.sequenceFor(Protocol.NUM), buffer)) {
                Pulsetech.LOGGER.debug("Matched NUM, sleeping...");
                delay(32);
                return false;
            }
            Pulsetech.LOGGER.debug("Checking for match with {}", buffer);
            String key = protocol.keyFor(buffer);
            if(key != null) {
                Pulsetech.LOGGER.debug("Matched pattern with key {}", key);
                output(key.equals(getPattern()));
                // We don't return false right away to
                // allow one extra pulse to be absorbed to help with timing.
            }
        }
        return true;
    }

    // Create an update tag here, like above.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
