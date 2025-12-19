package dev.hyperlynx.pulsetech.feature.cannon;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenData;
import dev.hyperlynx.pulsetech.feature.screen.ScreenUpdatePayload;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class CannonBlockEntity extends ProtocolBlockEntity {
    private BlockPos target = getBlockPos();

    public CannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CANNON.get(), pos, blockState);
    }

    public void setTarget(BlockPos target) {
        this.target = target;
    }

    public void fire() {
        level.removeBlock(target, false);
    }

    public void nudge(Direction direction, int amount) {
        target = target.relative(direction, amount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        target = NbtUtils.readBlockPos(tag, "Target").orElseGet(this::getBlockPos);
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Target", NbtUtils.writeBlockPos(target));
    }
}
