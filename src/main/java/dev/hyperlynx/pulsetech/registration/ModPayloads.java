package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.net.OpenConsolePayload;
import dev.hyperlynx.pulsetech.net.OpenSequenceChooserPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
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

        registrar.playBidirectional(
                ConsoleLinePayload.TYPE,
                ConsoleLinePayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ConsoleLinePayload::clientHandler,
                        ConsoleLinePayload::serverHandler
                )
        );

        registrar.playBidirectional(
                ConsolePriorLinesPayload.TYPE,
                ConsolePriorLinesPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ConsolePriorLinesPayload::clientHandler,
                        ConsolePriorLinesPayload::serverHandler
                )
        );

        registrar.playToClient(
                OpenSequenceChooserPayload.TYPE,
                OpenSequenceChooserPayload.STREAM_CODEC,
                OpenSequenceChooserPayload::handler
        );
    }
}
