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
        helper.addStoryBoard(ModBlocks.PATTERN_EMITTER.getId(), Pulsetech.location("pattern/pattern_emitter"), Storyboards::patternEmitter);
        helper.addStoryBoard(ModBlocks.PATTERN_DETECTOR.getId(), Pulsetech.location("pattern/pattern_detector"), Storyboards::patternDetector);
        helper.addStoryBoard(ModBlocks.NUMBER_EMITTER.getId(), Pulsetech.location("numbers"), Storyboards::numbers);
        helper.addStoryBoard(ModBlocks.NUMBER_MONITOR.getId(), Pulsetech.location("numbers"), Storyboards::numbers);
        helper.addStoryBoard(ModBlocks.CONTROLLER.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);
        helper.addStoryBoard(ModBlocks.CONTROLLER.getId(), Pulsetech.location("protocol/controller"), Storyboards::controller);
        helper.addStoryBoard(ModBlocks.CANNON.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);
        helper.addStoryBoard(ModBlocks.CANNON.getId(), Pulsetech.location("protocol/cannon"), Storyboards::cannon);
        helper.addStoryBoard(ModBlocks.SCREEN.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);
        helper.addStoryBoard(ModBlocks.SCREEN.getId(), Pulsetech.location("protocol/screen"), Storyboards::screen);
        helper.addStoryBoard(ModBlocks.ORB.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);
        helper.addStoryBoard(ModBlocks.ORB.getId(), Pulsetech.location("protocol/orb"), Storyboards::orb);
        helper.addStoryBoard(ModBlocks.SCANNER.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);
    }

}
