package dev.hyperlynx.pulsetech.feature.number.block;

import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import dev.hyperlynx.pulsetech.core.module.EmitterModule;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerByteInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
import dev.hyperlynx.pulsetech.feature.number.NumberKnower;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class NumberEmitterBlockEntity extends PulseBlockEntity implements NumberKnower, DebuggerInfoSource {
    private EmitterModule emitter = new EmitterModule();

    public NumberEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_EMITTER.get(), pos, blockState);
    }

    private byte number = 0;

    @Override
    public void tick() {
        if(level instanceof ServerLevel slevel) {
            if (emitter.isActive()) {
                if (!getBlockState().getValue(NumberEmitterBlock.ACTIVE)) {
                    assert getLevel() != null;
                    getLevel().setBlock(getBlockPos(), getBlockState().setValue(NumberEmitterBlock.ACTIVE, true), Block.UPDATE_ALL);
                }
                if (!emitter.outputInitialized()) {
                    emitter.enqueueTransmission(Sequence.fromByte(number));
                }
            } else if (getBlockState().getValue(NumberEmitterBlock.ACTIVE)) {
                assert getLevel() != null;
                getLevel().setBlock(getBlockPos(), getBlockState().setValue(NumberEmitterBlock.ACTIVE, false), Block.UPDATE_ALL);
            }
            emitter.tick(slevel, this);
        }
    }

    @Override
    public byte getNumber() {
        return number;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        number = (byte) tag.getInt("Number");
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

    @Override
    public boolean isDelayed() {
        return emitter.getDelay() > 0;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        return new DebuggerInfoManifest(List.of(
                new DebuggerInfoManifest.Entry(
                        Component.translatable("debugger.pulsetech.number").getString(),
                        DebuggerInfoTypes.NUMBER.value(),
                        () -> new DebuggerByteInfo(getNumber())),
                new DebuggerInfoManifest.Entry(
                        Component.translatable("debugger.pulsetech.output_buffer").getString(),
                        DebuggerInfoTypes.SEQUENCE.value(),
                        () -> new DebuggerSequenceInfo(emitter.getBuffer()))

        ), getBlockPos());
    }
}
