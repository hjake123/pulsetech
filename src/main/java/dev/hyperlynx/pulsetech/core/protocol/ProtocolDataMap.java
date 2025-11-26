package dev.hyperlynx.pulsetech.core.protocol;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/// A datapack registry for the Protocols which are defined in this world
@EventBusSubscriber
public class ProtocolDataMap {
    public static final DataMapType<BlockEntityType<?>, Protocol> TYPE = DataMapType.builder(
                Pulsetech.location("protocols"),
                Registries.BLOCK_ENTITY_TYPE,
                Protocol.CODEC
            ).synced(
                Protocol.CODEC, false
    ).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(TYPE);
    }
}