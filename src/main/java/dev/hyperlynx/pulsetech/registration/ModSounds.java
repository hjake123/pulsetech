package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Pulsetech.MODID);

    // Remember to also put an entry in datagen/SoundsJsonGenerator for each new sound!

    public static final DeferredHolder<SoundEvent, SoundEvent> CANNON_ZAP = SOUND_EVENTS.register(
            "cannon_zap",
            () -> SoundEvent.createVariableRangeEvent(Pulsetech.location("cannon_zap"))
    );
}
