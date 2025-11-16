package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
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
