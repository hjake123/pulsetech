package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.protocol.Protocol;
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
}
