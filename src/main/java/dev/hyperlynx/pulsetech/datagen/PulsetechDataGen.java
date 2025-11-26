package dev.hyperlynx.pulsetech.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class PulsetechDataGen {
    @SubscribeEvent
    public static void gatherData (GatherDataEvent event){
        var lookup = event.getLookupProvider();
        event.getGenerator().addProvider(
                event.includeServer(),
                new ProtocolGenerator(event.getGenerator().getPackOutput(), lookup)
        );
    }
}
