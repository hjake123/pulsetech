package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/// A static registry of all commands.
/// Specific commands are registered within the PulseBlock subclass for each feature.
@EventBusSubscriber
public class ProtocolCommands {
    public static final ResourceKey<Registry<ProtocolCommand>> REGISTRY_KEY = ResourceKey.createRegistryKey(Pulsetech.location("protocol_commands"));
    public static final Registry<ProtocolCommand> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .defaultKey(Pulsetech.location("error"))
            .create();
    public static final DeferredRegister<ProtocolCommand> COMMANDS = DeferredRegister.create(REGISTRY, Pulsetech.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}

