package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.core.PulseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StorageModemBlockEntity extends PulseBlockEntity {
    public StorageModemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isDelayed() {
        return false;
    }
}
