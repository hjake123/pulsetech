package dev.hyperlynx.pulsetech.feature.cannon;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class CannonBlockEntity extends ProtocolBlockEntity {
    private BlockPos target = getBlockPos();

    public CannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CANNON.get(), pos, blockState);
    }

    public void setTarget(BlockPos target) {
        this.target = target;
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
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Target", NbtUtils.writeBlockPos(target));
    }
}
