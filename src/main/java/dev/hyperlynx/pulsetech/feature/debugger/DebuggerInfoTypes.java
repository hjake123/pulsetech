package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class DebuggerInfoTypes {
    public static final ResourceKey<Registry<DebuggerInfoType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(Pulsetech.location("debugger_info_types"));
    public static final Registry<DebuggerInfoType<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .defaultKey(Pulsetech.location("error"))
            .create();
    public static final DeferredRegister<DebuggerInfoType<?>> TYPES = DeferredRegister.create(REGISTRY, Pulsetech.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static final DeferredHolder<DebuggerInfoType<?>, DebuggerInfoType<String>> TEXT = TYPES.register("text", () ->
            new DebuggerInfoType<>() {
                @Override
                public StreamCodec<ByteBuf, String> streamCodec() {
                    return ByteBufCodecs.STRING_UTF8;
                }
            });

    public static final DeferredHolder<DebuggerInfoType<?>, DebuggerInfoType<Sequence>> SEQUENCE = TYPES.register("sequence", () ->
            new DebuggerInfoType<>() {
                @Override
                public StreamCodec<ByteBuf, Sequence> streamCodec() {
                    return Sequence.STREAM_CODEC;
                }
            });
}
