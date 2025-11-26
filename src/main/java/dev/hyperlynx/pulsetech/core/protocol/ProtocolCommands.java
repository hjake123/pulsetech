package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.Sequence;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/// A static registry of all commands.
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

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> ON = COMMANDS.register("on", () ->
            new ProtocolCommand() {
                @Override
                public void run(ProtocolBlockEntity block) {
                    block.output(true);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> OFF = COMMANDS.register("off", () ->
            new ProtocolCommand() {
                @Override
                public void run(ProtocolBlockEntity block) {
                    block.output(false);
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> PULSE = COMMANDS.register("pulse", () ->
            new ProtocolCommand() {
                @Override
                public void run(ProtocolBlockEntity block) {
                    block.emit(new Sequence(true));
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> LOOP_PULSE = COMMANDS.register("loop_pulse", () ->
            new ProtocolCommand() {
                @Override
                public int parameterCount() {
                    return 1;
                }

                @Override
                public void run(ProtocolBlockEntity block) {
                    ShortParameter loops = block.getParameter(0);
                    if(loops == null) {
                        return;
                    }
                    if(loops.ready()) {
                        Sequence output = new Sequence();
                        for(int i = 0; i < loops.getValue(); i++) {
                            output.append(true);
                            output.append(false);
                        }
                        block.emit(output);
                    }
                }
            });


    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> DELAY_PULSE = COMMANDS.register("delay_pulse", () ->
            new ProtocolCommand() {
                @Override
                public int parameterCount() {
                    return 1;
                }

                @Override
                public void run(ProtocolBlockEntity block) {
                    ShortParameter ticks = block.getParameter(0);
                    if(ticks == null) {
                        return;
                    }
                    if(ticks.ready()) {
                        Sequence output = new Sequence();
                        for(int i = 0; i < ticks.getValue(); i++) {
                            output.append(false);
                        }
                        output.append(true);
                        block.emit(output);
                    }
                }
            });

    public static final DeferredHolder<ProtocolCommand, ProtocolCommand> TIMED_PULSE = COMMANDS.register("timed_pulse", () ->
            new ProtocolCommand() {
                @Override
                public int parameterCount() {
                    return 1;
                }

                @Override
                public void run(ProtocolBlockEntity block) {
                    ShortParameter ticks = block.getParameter(0);
                    if(ticks == null) {
                        return;
                    }
                    if(ticks.ready()) {
                        Sequence output = new Sequence();
                        for(int i = 0; i < ticks.getValue(); i++) {
                            output.append(true);
                        }
                        block.emit(output);
                    }
                }
            });
}

