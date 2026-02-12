package dev.hyperlynx.pulsetech.datagen;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.registration.ModSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundsJsonGenerator extends SoundDefinitionsProvider {
    protected SoundsJsonGenerator(PackOutput output, ExistingFileHelper helper) {
        super(output, Pulsetech.MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(ModSounds.CANNON_ZAP, SoundDefinition.definition()
                .with(sound("pulsetech:cannon_zap"))
                .subtitle("caption.pulsetech.cannon_zap")
        );

        add(ModSounds.ORB_SPAWN, SoundDefinition.definition()
                .with(sound("pulsetech:orb_spawn"))
                .subtitle("caption.pulsetech.orb_command")
        );

        add(ModSounds.ORB_COMMAND, SoundDefinition.definition()
                .with(sound("pulsetech:orb_command"))
                .subtitle("caption.pulsetech.orb_command")
        );

        add(ModSounds.ORB_CONFIRM, SoundDefinition.definition()
                .with(sound("pulsetech:orb_command").pitch(1.5F))
                .subtitle("caption.pulsetech.orb_confirm")
        );

        add(ModSounds.SCANNER_FOUND, SoundDefinition.definition()
                .with(sound("pulsetech:beep_beep").volume(1.1F).pitch(0.9F))
                .subtitle("caption.pulsetech.scanner_found")
        );

        add(ModSounds.BEEP.get(), SoundDefinition.definition()
                .with(sound("pulsetech:beep"))
                .subtitle("caption.pulsetech.beep")
        );
    }
}
