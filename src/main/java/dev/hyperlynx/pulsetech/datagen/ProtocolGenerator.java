package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.core.protocol.*;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

/// Creates all the Protocols for the mod's features.
public class ProtocolGenerator extends DataMapProvider {
    public ProtocolGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ProtocolDataMap.TYPE)
                .add(ModBlockEntityTypes.CONTROLLER,
                        ProtocolBuilder.builder(4)
                                .add(ProtocolCommands.OFF)
                                .add(ProtocolCommands.ON)
                                .add(ProtocolCommands.PULSE)
                                .add(ProtocolCommands.LOOP_PULSE)
                                .add(ProtocolCommands.DELAY_PULSE)
                                .add(ProtocolCommands.TIMED_PULSE)
                                .build(), false
                );
    }
}
