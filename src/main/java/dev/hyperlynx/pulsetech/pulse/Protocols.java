package dev.hyperlynx.pulsetech.pulse;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/// A datapack registry for the Protocols which are defined in this world
@EventBusSubscriber
public class Protocols {
    public static final ResourceKey<Registry<Protocol>> KEY = ResourceKey.createRegistryKey(Pulsetech.location("protocols"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                KEY,
                Protocol.CODEC,
                Protocol.CODEC
        );
    }
}