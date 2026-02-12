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
    }
}
