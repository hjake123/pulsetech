package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.block.PatternBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.EmitterModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class PatternEmitterBlockEntity extends PatternBlockEntity {
    private EmitterModule emitter = new EmitterModule();

    public PatternEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.PATTERN_EMITTER.get(), pos, blockState);
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

    @Override
    public boolean isActive() {
        return emitter.isActive();
    }

    @Override
    public void setActive(boolean active) {
        emitter.setActive(active);
    }

    @Override
    public void tick() {
        if(!(getLevel() instanceof ServerLevel slevel)) {
            return;
        }
        if(emitter.isActive() && !emitter.outputInitialized()) {
            if(protocol.sequenceFor(getPattern()) == null) {
                return;
            }
            emitter.getBuffer().append(true);
            emitter.getBuffer().appendAll(Objects.requireNonNull(protocol.sequenceFor(getPattern())));
            emitter.getBuffer().append(false);
        }
        emitter.tick(slevel, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Emitter", EmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        EmitterModule.CODEC.decode(NbtOps.INSTANCE, tag.get("Emitter")).ifSuccess(success -> emitter = success.getFirst());
    }
}
