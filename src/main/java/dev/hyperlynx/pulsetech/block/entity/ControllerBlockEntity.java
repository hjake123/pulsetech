package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.pulse.block.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ControllerBlockEntity extends ProtocolBlockEntity {
    public ControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONTROLLER.get(), pos, blockState);
    }
}
