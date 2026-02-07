package dev.hyperlynx.pulsetech.ponder;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PulsetechPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return Pulsetech.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(ModBlocks.PATTERN_EMITTER.getId(), Pulsetech.location("pattern/pattern_emitter"), PatternStoryboards::patternEmitter);
        helper.addStoryBoard(ModBlocks.PATTERN_DETECTOR.getId(), Pulsetech.location("pattern/pattern_detector"), PatternStoryboards::patternDetector);
    }

}
