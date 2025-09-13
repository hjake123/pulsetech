package dev.hyperlynx.pulsetech;

import com.mojang.logging.LogUtils;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModCreativeTab;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(Pulsetech.MODID)
public class Pulsetech {
    public static final String MODID = "pulsetech";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Pulsetech(IEventBus bus, ModContainer container) {
        bus.addListener(this::commonSetup);
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModCreativeTab.TABS.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
