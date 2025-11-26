package dev.hyperlynx.pulsetech.feature.controller;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ControllerBlockEntity extends ProtocolBlockEntity {
    public ControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONTROLLER.get(), pos, blockState);
    }
}
