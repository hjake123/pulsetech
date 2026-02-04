package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.client.ClientWrapper;
import dev.hyperlynx.pulsetech.feature.console.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.feature.console.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.feature.console.OpenConsolePayload;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoRequest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerByteInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
import dev.hyperlynx.pulsetech.feature.number.NumberSelectPayload;
import dev.hyperlynx.pulsetech.feature.pattern.OpenSequenceChooserPayload;
import dev.hyperlynx.pulsetech.feature.pattern.SequenceSelectPayload;
import dev.hyperlynx.pulsetech.feature.screen.ScreenUpdatePayload;
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

        registrar.playToServer(
                SequenceSelectPayload.TYPE,
                SequenceSelectPayload.STREAM_CODEC,
                SequenceSelectPayload::handler
        );

        registrar.playToClient(
                ScreenUpdatePayload.TYPE,
                ScreenUpdatePayload.STREAM_CODEC,
                ClientWrapper::acceptScreenBlockPayload
        );

        registrar.playToServer(
                NumberSelectPayload.TYPE,
                NumberSelectPayload.STREAM_CODEC,
                NumberSelectPayload::handler
        );

        registrar.playToClient(
                DebuggerInfoManifest.TYPE,
                DebuggerInfoManifest.STREAM_CODEC,
                ClientWrapper::openDebuggerScreen
        );

        registrar.playToServer(
                DebuggerInfoRequest.TYPE,
                DebuggerInfoRequest.STREAM_CODEC,
                DebuggerInfoRequest::processRequest
        );

        registrar.playToClient(
                DebuggerSequenceInfo.TYPE,
                DebuggerSequenceInfo.STREAM_CODEC,
                ClientWrapper::acceptDebuggerSequenceInfo
        );

        registrar.playToClient(
                DebuggerByteInfo.TYPE,
                DebuggerByteInfo.STREAM_CODEC,
                ClientWrapper::acceptDebuggerByteInfo
        );
    }
}
