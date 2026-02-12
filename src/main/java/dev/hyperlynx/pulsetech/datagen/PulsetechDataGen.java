package dev.hyperlynx.pulsetech.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@EventBusSubscriber
public class PulsetechDataGen {
    @SubscribeEvent
    public static void gatherData (GatherDataEvent event) throws ExecutionException, InterruptedException {
        var lookup = event.getLookupProvider();
        var output = event.getGenerator().getPackOutput();

        event.getGenerator().addProvider(
                event.includeServer(),
                new ProtocolGenerator(output, lookup)
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                new RecipeGenerator(output, lookup)
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                new LootTableProvider(
                        output,
                        Set.of(),
                        List.of(
                                new LootTableProvider.SubProviderEntry(
                                        BlockLootTableGenerator::new,
                                        LootContextParamSets.BLOCK
                                )
                        ),
                        lookup
                )
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                new BlockTagGenerator(output, lookup, event.getExistingFileHelper())
        );

        event.getGenerator().addProvider(
                event.includeClient(),
                new SoundsJsonGenerator(output, event.getExistingFileHelper())
        );
    }
}
