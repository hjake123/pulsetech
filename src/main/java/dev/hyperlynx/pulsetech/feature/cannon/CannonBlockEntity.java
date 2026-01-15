package dev.hyperlynx.pulsetech.feature.cannon;

import com.mojang.datafixers.util.Pair;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.util.Color;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

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

    public void setTargetOffset(int x, int y, int z) {
        this.target = origin.offset(x, y, z);
        ParticleScribe.drawParticleFrame(level, DustParticleOptions.REDSTONE, target, 2, 0.0F);
    }

    private static final int MAX_EXPLOSION_RESIST = 50;
    public void fire() {
        if(!level.isLoaded(target)) {
            return;
        }
        ParticleScribe.drawParticleLine(level, ParticleTypes.ELECTRIC_SPARK, getBlockPos(), target, 30, 0.5F);
        BlockState state_to_break = level.getBlockState(target);
        boolean can_break = state_to_break.getBlock().getExplosionResistance() < MAX_EXPLOSION_RESIST;
        boolean can_harvest = !state_to_break.requiresCorrectToolForDrops() || !state_to_break.is(BlockTags.NEEDS_DIAMOND_TOOL);
        if(can_break) {
            level.destroyBlock(target, can_harvest, null);
            level.removeBlock(target, false);
        }
        for(Direction n : nudge_directions) {
            target = target.relative(n, nudge_amount);
            ParticleScribe.drawParticleFrame(level, DustParticleOptions.REDSTONE, target, 2, 0.0F);
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
        if(tag.contains("NudgeDir")) {
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
}
