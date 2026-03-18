package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.screen.ScreenData;
import dev.hyperlynx.pulsetech.feature.storage.ItemFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModComponentTypes {
    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Pulsetech.MODID);

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> REMOTE_CONSOLE_LINK_POSITION = TYPES.register("remote_console_link_position", () ->
            DataComponentType.<GlobalPos>builder()
                    .persistent(GlobalPos.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemFilter>>> ITEM_FILTERS = TYPES.register("item_filters", () ->
            DataComponentType.<List<ItemFilter>>builder()
                    .persistent(ItemFilter.CODEC.listOf())
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Datasheet>> DATASHEET = TYPES.register("datasheet", () ->
            DataComponentType.<Datasheet>builder()
                    .persistent(Datasheet.CODEC)
                    .networkSynchronized(Datasheet.STREAM_CODEC)
                    .build());
}
