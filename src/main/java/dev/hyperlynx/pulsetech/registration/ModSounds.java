package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.registries.BuiltInRegistries;
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
    public static final DeferredHolder<SoundEvent, SoundEvent> ORB_SPAWN = SOUND_EVENTS.register(
            "orb_spawn",
            () -> SoundEvent.createVariableRangeEvent(Pulsetech.location("orb_spawn"))
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> ORB_COMMAND = SOUND_EVENTS.register(
            "orb_command",
            () -> SoundEvent.createVariableRangeEvent(Pulsetech.location("orb_command"))
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> ORB_CONFIRM = SOUND_EVENTS.register(
            "orb_confirm",
            () -> SoundEvent.createVariableRangeEvent(Pulsetech.location("orb_confirm"))
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> SCANNER_FOUND = SOUND_EVENTS.register(
            "scanner_found",
            () -> SoundEvent.createVariableRangeEvent(Pulsetech.location("scanner_found"))
    );

}
