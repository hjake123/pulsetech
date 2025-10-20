package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.module.EmitterModule;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class NumberEmitterBlockEntity extends ProtocolBlockEntity implements NumberKnower {
    private EmitterModule emitter = new EmitterModule();

    public NumberEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_EMITTER.get(), pos, blockState);
    }

    private short number = 0;

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            if(emitter.isActive() && !emitter.outputInitialized()) {
                emitter.enqueueTransmission(getProtocol().fromShort(number));
            }
            emitter.tick(slevel, this);
        }
    }

    public void adjustNumber(int amount) {
        number = (short) Math.max(0, number + amount);
    }

    @Override
    public short getNumber() {
        return number;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        number = (short) tag.getInt("Number");
        EmitterModule.CODEC.decode(NbtOps.INSTANCE, tag).ifSuccess(success -> emitter = success.getFirst());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Number", number);
        tag.put("Emitter", EmitterModule.CODEC.encodeStart(NbtOps.INSTANCE, emitter).getOrThrow());
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
}
