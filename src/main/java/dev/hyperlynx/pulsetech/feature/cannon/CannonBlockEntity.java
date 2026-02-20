package dev.hyperlynx.pulsetech.feature.cannon;

import com.mojang.datafixers.util.Pair;
import dev.hyperlynx.pulsetech.Config;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerPosInfo;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModSounds;
import dev.hyperlynx.pulsetech.util.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CannonBlockEntity extends ProtocolBlockEntity implements ScannerLinkable {
    private BlockPos target = getBlockPos();
    private BlockPos origin = getBlockPos();
    private final List<Direction> nudge_directions = new ArrayList<>();
    private int nudge_amount = 0;

    public CannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CANNON.get(), pos, blockState);
    }

    private void updateTargetIfInRange(BlockPos new_target) {
        if(new_target.distSqr(origin) <= Config.CANNON_MAX_RANGE.get() * Config.CANNON_MAX_RANGE.get()) {
            target = new_target;
            ParticleScribe.drawParticleFrame(level, DustParticleOptions.REDSTONE, target, 2, 0.0F);
        } else {
            ParticleScribe.drawParticleBox(level, ParticleTypes.SMOKE, AABB.ofSize(getBlockPos().getCenter(), 1, 1, 1), 3);
        }
    }

    public void setTargetOffset(int x, int y, int z) {
        updateTargetIfInRange(origin.offset(x, y, z));
    }

    public void fire() {
        if(level == null || !level.isLoaded(target)) {
            return;
        }
        ParticleScribe.drawParticleLine(level, ParticleTypes.ELECTRIC_SPARK, getBlockPos(), target, 30, 0.5F);
        level.playSound(null, getBlockPos(), ModSounds.CANNON_ZAP.value(), SoundSource.BLOCKS, 0.3F, level.getRandom().nextFloat() * 0.1F + 0.95F);
        BlockState state_to_break = level.getBlockState(target);
        boolean can_break = state_to_break.getBlock().getExplosionResistance() < Config.CANNON_MAX_BLAST_RESIST.get() && !state_to_break.isAir();
        boolean can_harvest = !state_to_break.requiresCorrectToolForDrops() || !state_to_break.is(BlockTags.NEEDS_DIAMOND_TOOL);
        if(can_break) {
            level.playSound(null, target, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.1F, 0.1F);
            level.destroyBlock(target, can_harvest, null);
            level.removeBlock(target, false);
        }
        for(Direction n : nudge_directions) {
            updateTargetIfInRange(target.relative(n, nudge_amount));
        }
    }

    public void addNudge(Direction direction, int amount) {
        nudge_directions.add(direction);
        nudge_amount = amount;
    }

    public void resetNudge() {
        nudge_directions.clear();
        nudge_amount = 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        target = NbtUtils.readBlockPos(tag, "Target").orElseGet(this::getBlockPos);
        origin = NbtUtils.readBlockPos(tag, "Origin").orElseGet(this::getBlockPos);
        if(tag.contains("NudgeDirs")) {
            nudge_directions.clear();
            nudge_directions.addAll(Direction.CODEC.listOf().decode(NbtOps.INSTANCE, Objects.requireNonNull(tag.get("NudgeDirs"))).resultOrPartial().orElse(Pair.of(List.of(Direction.NORTH), new CompoundTag())).getFirst());

        }
        nudge_amount = tag.getInt("NudgeAmount");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Target", NbtUtils.writeBlockPos(target));
        tag.put("Origin", NbtUtils.writeBlockPos(origin));
        tag.put("NudgeDirs", Direction.CODEC.listOf().encodeStart(NbtOps.INSTANCE, nudge_directions).getPartialOrThrow());
        tag.put("NudgeAmount", IntTag.valueOf(nudge_amount));
    }

    @Override
    public boolean setLinkedOrigin(BlockPos scanner_position) {
        assert level != null;
        if(level.isLoaded(scanner_position) && level.getBlockState(scanner_position).is(ModBlocks.SCANNER)) {
            this.origin = scanner_position;
            this.target = scanner_position;
            return true;
        }
        return false;
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {

        return super.getDebuggerInfoManifest().append(new DebuggerInfoManifest.Entry(
                Component.translatable("debugger.pulsetech.nudge").getString(),
                DebuggerInfoTypes.BLOCK_POS.value(),
                () -> {
                    BlockPos nudge = BlockPos.ZERO;
                    for(Direction n : nudge_directions) {
                        nudge = nudge.relative(n, nudge_amount);
                    }
                    return new DebuggerPosInfo(nudge);
                }
        )).append(new DebuggerInfoManifest.Entry(
                Component.translatable("debugger.pulsetech.target").getString(),
                DebuggerInfoTypes.BLOCK_POS.value(),
                () -> new DebuggerPosInfo(target.subtract(origin))
        ));
    }

    public BlockPos getTargetOffset() {
        return target.subtract(origin);
    }
}
