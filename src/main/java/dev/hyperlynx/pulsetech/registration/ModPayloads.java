package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.net.ConsoleSendLinePayload;
import dev.hyperlynx.pulsetech.net.OpenConsolePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class ModPayloads {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                OpenConsolePayload.TYPE,
                OpenConsolePayload.STREAM_CODEC,
                OpenConsolePayload::handler
        );

        registrar.playToServer(
                ConsoleSendLinePayload.TYPE,
                ConsoleSendLinePayload.STREAM_CODEC,
                ConsoleSendLinePayload::handler
        );
    }
}
