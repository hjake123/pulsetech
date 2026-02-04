package dev.hyperlynx.pulsetech.feature.debugger.infotype;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class DebuggerInfoTypes {
    public static final ResourceKey<Registry<DebuggerInfoType>> REGISTRY_KEY = ResourceKey.createRegistryKey(Pulsetech.location("debugger_info_types"));
    public static final Registry<DebuggerInfoType> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .defaultKey(Pulsetech.location("error"))
            .create();
    public static final DeferredRegister<DebuggerInfoType> TYPES = DeferredRegister.create(REGISTRY, Pulsetech.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static final DeferredHolder<DebuggerInfoType, DebuggerInfoType> SEQUENCE = TYPES.register("sequence", DebuggerInfoType::new);
    public static final DeferredHolder<DebuggerInfoType, DebuggerInfoType> NUMBER = TYPES.register("number", DebuggerInfoType::new);
    public static final DeferredHolder<DebuggerInfoType, DebuggerInfoType> TEXT = TYPES.register("text", DebuggerInfoType::new);
    public static final DeferredHolder<DebuggerInfoType, DebuggerInfoType> BLOCK_POS = TYPES.register("pos", DebuggerInfoType::new);
}
