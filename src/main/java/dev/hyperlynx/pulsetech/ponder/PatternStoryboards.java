package dev.hyperlynx.pulsetech.ponder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PatternStoryboards {
    static void patternEmitter(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pattern_emitter", "Using a Pattern Emitter");
        scene.showBasePlate();
        scene.idle(10);
        BlockPos pos = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().column(2, 2), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .independent(0)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 20)
                .rightClick();
        scene.idle(20);
        scene.effects().indicateSuccess(pos);
        scene.idle(10);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().centerOf(pos))
                .text("");
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.world().toggleRedstonePower(util.select().position(2, 1, 0));
        scene.world().toggleRedstonePower(util.select().position(2, 1, 1));
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(2, 1, 0));
        scene.world().toggleRedstonePower(util.select().position(2, 1, 1));

        var signal_line = util.select().fromTo(1, 1, 4, 3, 1, 4).add(util.select().position(2, 1, 3));
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(4);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(4);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(2);
        scene.world().toggleRedstonePower(signal_line);
        scene.world().toggleRedstonePower(util.select().position(0, 1, 3));
        scene.world().toggleRedstonePower(util.select().position(0, 1, 2));
        scene.idle(2);
        scene.world().toggleRedstonePower(util.select().position(0, 1, 3));
        scene.idle(30);
    }

    static void patternDetector(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pattern_detector", "Using a Pattern Detector");
        scene.showBasePlate();
        scene.idle(10);
        BlockPos pos = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().column(2, 2), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .independent(0)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 20)
                .rightClick();
        scene.idle(20);
        scene.effects().indicateSuccess(pos);
        scene.idle(10);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().centerOf(pos))
                .text("");
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);

        var signal_line = util.select().fromTo(1, 1, 1, 3, 1, 1);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(2);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(2);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(4);
        scene.world().toggleRedstonePower(signal_line);
        scene.idle(2);
        scene.world().toggleRedstonePower(util.select().fromTo(2, 1, 3, 2, 1, 4));
        scene.idle(2);
        scene.world().toggleRedstonePower(util.select().position(2, 1, 3));
    }

    static void numbers(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("numbers", "Numbers");
        scene.showBasePlate();
        scene.overlay().showText(80)
                .text("In Pulsetech, numbers are represented as 8-bit signed integers");
        scene.idle(100);
        scene.overlay().showText(60)
                .text("Two blocks that work with numbers are...");
        scene.idle(80);
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(2, 1, 1), 40)
                .text("The Number Emitter")
                .pointAt(util.grid().at(2, 1, 1).getBottomCenter());
        scene.idle(60);
        scene.overlay().showOutlineWithText(util.select().position(2, 1, 3), 40)
                .text("and the Number Monitor")
                .pointAt(util.grid().at(2, 1, 3).getBottomCenter());
        scene.idle(60);
        scene.overlay().showText(80)
                .text("Right click on the Number Emitter to set the number in its GUI")
                .pointAt(util.grid().at(2, 1, 1).getBottomCenter());
        scene.idle(100);
        scene.overlay().showControls(util.grid().at(2, 1, 1).getCenter(), Pointing.DOWN, 20)
                .rightClick();
        scene.idle(10);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 1));
        scene.idle(20);
        scene.overlay().showText(80)
                .text("The Number Monitor will show any number it hears in its tag")
                .pointAt(util.grid().at(2, 1, 3).getCenter());
        scene.idle(100);
    }

}
