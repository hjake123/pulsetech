package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.core.protocol.*;
import dev.hyperlynx.pulsetech.feature.controller.ControllerBlock;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerBlock;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlock;
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
                        ProtocolBuilder.builder(3)
                                .add(ControllerBlock.OFF)
                                .add(ControllerBlock.ON)
                                .add(ControllerBlock.PULSE)
                                .add(ControllerBlock.LOOP_PULSE)
                                .add(ControllerBlock.DELAY_PULSE)
                                .add(ControllerBlock.TIMED_PULSE)
                                .add(ControllerBlock.LOOP_DELAY_PULSE)
                                .add(ControllerBlock.RANDOMS)
                                .build(), false
                )
                .add(ModBlockEntityTypes.SCANNER,
                        ProtocolBuilder.builder(2)
                                .add(ScannerBlock.MODE_SELECT)
                                .add(ScannerBlock.CHECK)
                                .add(ScannerBlock.COUNT)
                                .add(ScannerBlock.FIND_NEAREST)
                                .build(), false
                )

                .add(ModBlockEntityTypes.SCREEN,
                        ProtocolBuilder.builder(3)
                                .add(ScreenBlock.BG)
                                .add(ScreenBlock.CLEAR_BG)
                                .add(ScreenBlock.PEN_COLOR)
                                .add(ScreenBlock.RESET_PEN_COLOR)
                                .add(ScreenBlock.MARK)
                                .build(), false
        );

    }
}
