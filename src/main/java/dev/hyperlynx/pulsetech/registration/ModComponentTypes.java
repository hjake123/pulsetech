package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.Protocol;
import dev.hyperlynx.pulsetech.feature.console.macros.Macros;
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
}
