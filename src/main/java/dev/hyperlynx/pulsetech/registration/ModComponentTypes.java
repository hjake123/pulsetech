package dev.hyperlynx.pulsetech.registration;

import com.jcraft.jorbis.Block;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.Protocol;
import dev.hyperlynx.pulsetech.feature.console.macros.Macros;
import dev.hyperlynx.pulsetech.feature.screen.ScreenData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponentTypes {
    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Pulsetech.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Protocol>> PROTOCOL = TYPES.register("protocol", () ->
            DataComponentType.<Protocol>builder()
                    .persistent(Protocol.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Macros>> MACROS = TYPES.register("macros", () ->
            DataComponentType.<Macros>builder()
                    .persistent(Macros.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ScreenData>> SCREEN_DATA = TYPES.register("screen_data", () ->
            DataComponentType.<ScreenData>builder()
                    .persistent(ScreenData.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> SCANNER_LINK_POSITION = TYPES.register("scanner_link_position", () ->
            DataComponentType.<BlockPos>builder()
                    .persistent(BlockPos.CODEC)
                    .build());
}
