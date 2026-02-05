package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Pulsetech.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Orb>> ORB = TYPES.register("orb", () ->
            EntityType.Builder.of(Orb::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .fireImmune()
                    .setUpdateInterval(1)
                    .build("orb")
    );
}
