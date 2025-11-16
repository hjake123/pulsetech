package dev.hyperlynx.pulsetech.pulse.protocol;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.pulse.block.PulseBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Map;

/// A static registry of all commands.
@EventBusSubscriber
public class ProtocolCommands {
    public static final ResourceKey<Registry<ProtocolCommand<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(Pulsetech.location("protocol_commands"));
    public static final Registry<ProtocolCommand<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .defaultKey(Pulsetech.location("error"))
            .create();
    public static final DeferredRegister<ProtocolCommand<?>> COMMANDS = DeferredRegister.create(REGISTRY, Pulsetech.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static final DeferredHolder<ProtocolCommand<?>, ProtocolCommand<?>> ON = COMMANDS.register("on", () ->
            new ProtocolCommand<>(Map.of()) {
                @Override
                void run(PulseBlockEntity block) {
                    block.output(true);
                }
            });

    public static final DeferredHolder<ProtocolCommand<?>, ProtocolCommand<?>> OFF = COMMANDS.register("off", () ->
            new ProtocolCommand<>(Map.of()) {
                @Override
                void run(PulseBlockEntity block) {
                    block.output(false);
                }
            });
}

