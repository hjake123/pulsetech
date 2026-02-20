package dev.hyperlynx.pulsetech.ponder;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.TagBuilder;
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

        helper.addStoryBoard(ModBlocks.CONTROLLER.getId(), Pulsetech.location("protocol/controller"), Storyboards::controller);
        helper.addStoryBoard(ModBlocks.CONTROLLER.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);

        helper.addStoryBoard(ModBlocks.CANNON.getId(), Pulsetech.location("protocol/cannon"), Storyboards::cannon);
        helper.addStoryBoard(ModBlocks.CANNON.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);

        helper.addStoryBoard(ModBlocks.SCREEN.getId(), Pulsetech.location("protocol/screen"), Storyboards::screen);
        helper.addStoryBoard(ModBlocks.SCREEN.getId(), Pulsetech.location("datacell/screen_data"), Storyboards::dataCellScreen);
        helper.addStoryBoard(ModBlocks.SCREEN.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);

        helper.addStoryBoard(ModBlocks.ORB.getId(), Pulsetech.location("protocol/orb"), Storyboards::orb);
        helper.addStoryBoard(ModBlocks.ORB.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);

        helper.addStoryBoard(ModBlocks.SCANNER.getId(), Pulsetech.location("protocol/scanner"), Storyboards::scanner);
        helper.addStoryBoard(ModBlocks.SCANNER.getId(), Pulsetech.location("datacell/scanner_pos"), Storyboards::dataCellScanner);
        helper.addStoryBoard(ModBlocks.SCANNER.getId(), Pulsetech.location("protocol_blocks_showcase"), Storyboards::protocolBlocks);

        helper.addStoryBoard(ModBlocks.CONSOLE.getId(), Pulsetech.location("console"), Storyboards::console);
        helper.addStoryBoard(ModBlocks.CONSOLE.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);
        helper.addStoryBoard(ModBlocks.GREEN_CONSOLE.getId(), Pulsetech.location("console"), Storyboards::console);
        helper.addStoryBoard(ModBlocks.GREEN_CONSOLE.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);
        helper.addStoryBoard(ModBlocks.INDIGO_CONSOLE.getId(), Pulsetech.location("console"), Storyboards::console);
        helper.addStoryBoard(ModBlocks.INDIGO_CONSOLE.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);
        helper.addStoryBoard(ModBlocks.RED_CONSOLE.getId(), Pulsetech.location("console"), Storyboards::console);
        helper.addStoryBoard(ModBlocks.RED_CONSOLE.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);
        helper.addStoryBoard(ModBlocks.WHITE_CONSOLE.getId(), Pulsetech.location("console"), Storyboards::console);
        helper.addStoryBoard(ModBlocks.WHITE_CONSOLE.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);

        helper.addStoryBoard(ModItems.DATA_CELL.getId(), Pulsetech.location("datacell/screen_data"), Storyboards::dataCellScreen);
        helper.addStoryBoard(ModItems.DATA_CELL.getId(), Pulsetech.location("datacell/console_macros"), Storyboards::dataCellConsole);
        helper.addStoryBoard(ModItems.DATA_CELL.getId(), Pulsetech.location("datacell/processor"), Storyboards::processor);
        helper.addStoryBoard(ModItems.DATA_CELL.getId(), Pulsetech.location("datacell/scanner_pos"), Storyboards::dataCellScanner);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation feature_tag = Pulsetech.location("features");
        TagBuilder builder = helper.registerTag(feature_tag);
        builder.item(ModItems.CONSOLE, true, false);
        helper.addTagToComponent(ModItems.CONSOLE.getId(), feature_tag);
        helper.addTagToComponent(ModItems.PATTERN_DETECTOR.getId(), feature_tag);
        helper.addTagToComponent(ModItems.PATTERN_EMITTER.getId(), feature_tag);
        helper.addTagToComponent(ModItems.NUMBER_MONITOR.getId(), feature_tag);
        helper.addTagToComponent(ModItems.NUMBER_EMITTER.getId(), feature_tag);
        helper.addTagToComponent(ModItems.CONTROLLER.getId(), feature_tag);
        helper.addTagToComponent(ModItems.CANNON.getId(), feature_tag);
        helper.addTagToComponent(ModItems.ORB.getId(), feature_tag);
        helper.addTagToComponent(ModItems.SCANNER.getId(), feature_tag);
        helper.addTagToComponent(ModItems.SCREEN.getId(), feature_tag);
        helper.addTagToComponent(ModItems.DATA_CELL.getId(), feature_tag);
        builder.addToIndex();
        builder.register();
    }
}
