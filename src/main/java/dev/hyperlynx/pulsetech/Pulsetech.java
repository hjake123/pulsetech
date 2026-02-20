package dev.hyperlynx.pulsetech;

import com.mojang.logging.LogUtils;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommands;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Pulsetech.MODID)
public class Pulsetech {
    public static final String MODID = "pulsetech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Pulsetech(IEventBus bus, ModContainer container) {
        ModBlocks.BLOCKS.register(bus);
        ModBlockEntityTypes.TYPES.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntityTypes.TYPES.register(bus);
        ModCreativeTab.TABS.register(bus);
        ModComponentTypes.TYPES.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);
        ProtocolCommands.COMMANDS.register(bus);
        DebuggerInfoTypes.TYPES.register(bus);
        container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
