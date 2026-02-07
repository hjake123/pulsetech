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
        BlockPos emitter_pos = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().column(2, 2), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(emitter_pos), 80)
                .independent(0)
                .pointAt(util.vector().blockSurface(emitter_pos, Direction.DOWN))
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(emitter_pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.overlay().showControls(emitter_pos.getCenter(), Pointing.DOWN, 20)
                .rightClick();
        scene.idle(20);
        scene.effects().indicateSuccess(emitter_pos);
        scene.idle(10);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().centerOf(emitter_pos))
                .text("");
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(emitter_pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.world().toggleRedstonePower(util.select().position(2, 1, 0));
        scene.effects().indicateRedstone(util.grid().at(2, 1, 1));
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(2, 1, 0));
        scene.effects().indicateRedstone(util.grid().at(2, 1, 3));
        scene.effects().indicateRedstone(util.grid().at(2, 1, 4));
        scene.effects().indicateRedstone(util.grid().at(1, 1, 4));
        scene.effects().indicateRedstone(util.grid().at(3, 1, 4));
        scene.idle(10);
        scene.effects().indicateRedstone(util.grid().at(0, 1, 3));
        scene.world().toggleRedstonePower(util.select().position(0, 1, 2));
        scene.idle(30);
    }
}
