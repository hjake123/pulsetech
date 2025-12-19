package dev.hyperlynx.pulsetech.feature.cannon;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class CannonBlockEntity extends ProtocolBlockEntity implements ScannerLinkable {
    private BlockPos target = getBlockPos();
    private BlockPos origin = getBlockPos();

    public CannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CANNON.get(), pos, blockState);
    }

    public void setTargetOffset(int x, int y, int z) {
        this.target = origin.offset(x, y, z);
    }

    private static final int MAX_EXPLOSION_RESIST = 50;
    public void fire() {
        if(!level.isLoaded(target)) {
            return;
        }
        BlockState state_to_break = level.getBlockState(target);
        boolean can_break = state_to_break.getBlock().getExplosionResistance() < MAX_EXPLOSION_RESIST;
        boolean can_harvest = !state_to_break.requiresCorrectToolForDrops() || !state_to_break.is(BlockTags.NEEDS_DIAMOND_TOOL);
        if(can_break) {
            level.destroyBlock(target, can_harvest, null);
            level.removeBlock(target, false);
        }
    }

    public void nudge(Direction direction, int amount) {
        target = target.relative(direction, amount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        target = NbtUtils.readBlockPos(tag, "Target").orElseGet(this::getBlockPos);
        origin = NbtUtils.readBlockPos(tag, "Origin").orElseGet(this::getBlockPos);
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Target", NbtUtils.writeBlockPos(target));
        tag.put("Origin", NbtUtils.writeBlockPos(origin));
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
