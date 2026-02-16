package dev.hyperlynx.pulsetech.ponder;

import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import dev.hyperlynx.pulsetech.feature.orb.OrbBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import dev.hyperlynx.pulsetech.util.Color;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Storyboards {
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
        scene.addKeyframe();
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
        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.addKeyframe();
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
        scene.addKeyframe();
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
        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(60)
                .independent(20)
                .pointAt(util.vector().blockSurface(pos, Direction.DOWN))
                .text("");
        scene.idle(80);
        scene.addKeyframe();
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
        scene.addKeyframe();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(2, 1, 1), 40)
                .text("The Number Emitter")
                .pointAt(util.grid().at(2, 1, 1).getBottomCenter());
        scene.idle(60);
        scene.overlay().showOutlineWithText(util.select().position(2, 1, 3), 40)
                .text("and the Number Monitor")
                .pointAt(util.grid().at(2, 1, 3).getBottomCenter());
        scene.idle(60);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("Right click on the Number Emitter to set the number in its GUI")
                .pointAt(util.grid().at(2, 1, 1).getBottomCenter());
        scene.idle(100);
        scene.overlay().showControls(util.grid().at(2, 1, 1).getCenter(), Pointing.DOWN, 20)
                .rightClick();
        scene.idle(10);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 1));
        scene.idle(20);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("The Number Monitor will show any number it hears in its tag")
                .pointAt(util.grid().at(2, 1, 3).getCenter());
        scene.idle(100);
    }

    static void protocolBlocks(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("protocol_blocks_showcase", "Protocol Blocks");
        scene.showBasePlate();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.overlay().showText(60)
                .text("Many blocks operate according to a Protocol");
        scene.idle(80);
        scene.overlay().showText(80)
                .text("A Protocol is a set of pulse patterns which each cause a block to do something different");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("To view a block's Protocol, right click it with a Datasheet");
        scene.idle(20);
        scene.overlay().showControls(util.grid().at(1, 1, 2).getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void controller(SceneBuilder scene, SceneBuildingUtil util) {
        BlockPos position = util.grid().at(1, 1, 1);
        scene.title("controller", "Using a Controller");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(position), 60)
                .text("");
        scene.idle(80);
        scene.overlay().showText(60)
                .text("")
                .pointAt(position.getBottomCenter());
        scene.idle(80);
        scene.overlay().showText(60)
                .text("")
                .pointAt(position.getBottomCenter());
        scene.overlay().showControls(position.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void cannon(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cannon", "");
        scene.showBasePlate();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        BlockPos pos = util.grid().at(2, 1, 1);
        scene.overlay().showOutlineWithText(util.select().position(pos), 60)
                .text("");
        scene.idle(80);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(40);
        scene.world().setBlock(util.grid().at(2, 1, 3), Blocks.AIR.defaultBlockState(), true);
        scene.idle(40);
        scene.world().showSection(util.select().layersFrom(2), Direction.DOWN);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(40);
        scene.world().setBlock(util.grid().at(2, 1, 4), Blocks.AIR.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 2, 4), Blocks.AIR.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 3, 4), Blocks.AIR.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 4, 4), Blocks.AIR.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 5, 4), Blocks.AIR.defaultBlockState(), true);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(80);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void screen(SceneBuilder scene, SceneBuildingUtil util) {
        var position = util.grid().at(2, 2, 3);
        scene.title("screen", "Using a Screen");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showOutlineWithText(util.select().position(position), 60)
                .text("");
        scene.idle(80);
        scene.overlay().showText(60)
                .text("")
                .pointAt(position.getBottomCenter());
        scene.idle(80);
        scene.effects().indicateRedstone(position);
        scene.world().modifyBlockEntity(position, ScreenBlockEntity.class, screen -> {
            screen.setBackgroundColor(new Color(0, 200, 40));
            screen.sendUpdate();
        });
        scene.idle(40);
        scene.overlay().showText(60)
                .text("")
                .pointAt(position.getBottomCenter());
        scene.overlay().showControls(position.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void orb(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("orb", "Using an Orb Projector");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        BlockPos orb_projector_pos = util.grid().at(2, 1, 2);
        scene.idle(20);
        scene.world().modifyBlockEntity(orb_projector_pos, OrbBlockEntity.class, entity -> entity.forceSetOrigin(orb_projector_pos));
        scene.world().modifyBlockEntity(orb_projector_pos, OrbBlockEntity.class, OrbBlockEntity::spawnOrb);
        scene.overlay().showOutlineWithText(util.select().position(orb_projector_pos), 80)
                .text("The Orb Projector summons and manipulates a floating orb");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("It has commands to move the Orb...")
                .pointAt(orb_projector_pos.getCenter());
        scene.effects().indicateRedstone(orb_projector_pos);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(2, 0, 0, true));
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, 2, 0, true));
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(-2, 0, 0, true));
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, -3, 0, true));
        scene.idle(80);
        scene.addKeyframe();
        var items = scene.world().createItemEntity(orb_projector_pos.above(2).getCenter(), Vec3.ZERO, Items.COBBLESTONE.getDefaultInstance().copyWithCount(32));
        scene.idle(20);
        scene.effects().indicateRedstone(orb_projector_pos);
        scene.world().modifyEntity(items, Entity::kill);
        scene.overlay().showText(40)
                .text("...to grab and move entities...")
                .pointAt(orb_projector_pos.getCenter());
        scene.idle(20);
        scene.overlay().showControls(orb_projector_pos.getCenter(), Pointing.DOWN, 20)
                        .withItem(Items.COBBLESTONE.getDefaultInstance());
        scene.idle(40);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(-2, 0, 2, true));
        scene.idle(30);
        scene.addKeyframe();
        scene.effects().indicateRedstone(orb_projector_pos);
        scene.world().modifyEntities(Orb.class, Orb::togglePen);
        scene.world().modifyEntities(ItemEntity.class, Entity::stopRiding);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(4, 0, 0, true));
        scene.overlay().showText(40)
                .text("...to place blocks...")
                .pointAt(orb_projector_pos.getCenter());
        scene.world().setBlock(util.grid().at(0, 1, 4), Blocks.COBBLESTONE.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(1, 1, 4), Blocks.COBBLESTONE.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 1, 4), Blocks.COBBLESTONE.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(3, 1, 4), Blocks.COBBLESTONE.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(4, 1, 4), Blocks.COBBLESTONE.defaultBlockState(), true);
        scene.idle(20);
        scene.addKeyframe();
        scene.effects().indicateRedstone(orb_projector_pos);
        scene.world().modifyEntities(Orb.class, Orb::togglePen);
        scene.world().modifyEntities(Orb.class, Orb::toggleGrab);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, 0, -2, true));
        scene.idle(20);
        scene.overlay().showText(40)
                .text("...and to change into a damaging projectile.")
                .pointAt(orb_projector_pos.getCenter());
        scene.world().modifyEntities(Orb.class, Orb::toggleProjectile);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, -4, 0, true));
        scene.idle(60);
        scene.overlay().showText(60)
                .text("")
                .pointAt(orb_projector_pos.getBottomCenter());
        scene.overlay().showControls(orb_projector_pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void scanner(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("scanner", "Using a Scanner");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.special().createBirb(util.grid().at(2, 1, 3).getBottomCenter(), ParrotPose.FaceCursorPose::new);
        BlockPos pos = util.grid().at(2, 1, 0);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .text("");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.overlay().showText(40)
                .text("")
                .pointAt(pos.getCenter())
                .placeNearTarget();
        scene.idle(60);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        var input_cable = util.select().fromTo(3, 1, 0, 4, 1, 0);
        var output_cable = util.select().fromTo(0, 1, 0, 1, 1, 0);
        var output_pos = util.grid().at(0, 1, 0);
        scene.world().toggleRedstonePower(input_cable);
        scene.idle(4);
        scene.world().toggleRedstonePower(input_cable);
        scene.world().toggleRedstonePower(output_cable);
        scene.idle(10);
        scene.world().toggleRedstonePower(output_cable);
        scene.overlay().showText(40)
                        .pointAt(output_pos.getBottomCenter())
                        .placeNearTarget()
                        .text("");
        scene.idle(60);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.world().toggleRedstonePower(input_cable);
        scene.idle(4);
        scene.world().toggleRedstonePower(input_cable);
        scene.world().toggleRedstonePower(output_cable);
        scene.idle(10);
        scene.world().toggleRedstonePower(output_cable);
        scene.overlay().showText(40)
                .pointAt(output_pos.getBottomCenter())
                .placeNearTarget()
                .text("");
        scene.idle(60);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.world().toggleRedstonePower(input_cable);
        scene.idle(4);
        scene.world().toggleRedstonePower(input_cable);
        scene.world().toggleRedstonePower(output_cable);
        scene.idle(10);
        scene.world().toggleRedstonePower(output_cable);
        scene.overlay().showText(40)
                .pointAt(output_pos.getBottomCenter())
                .placeNearTarget()
                .text("");
        scene.idle(60);
        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void console(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("console", "Using a Console");
        scene.showBasePlate();
        BlockPos pos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .text("");
        scene.idle(100);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(4, 1, 0, 4, 1, 4), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 1, 4), Direction.DOWN);
        scene.world().showSection(util.select().position(3, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text("")
                .pointAt(pos.getCenter())
                .placeNearTarget();
        scene.idle(60);
        scene.world().toggleRedstonePower(util.select().position(4, 1, 2));
        scene.idle(20);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 4, 1, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.world().modifyBlockEntity(util.grid().at(0, 1, 4), OrbBlockEntity.class, entity -> entity.forceSetOrigin(util.grid().at(0, 1, 4)));
        scene.world().modifyBlockEntity(util.grid().at(0, 1, 4), OrbBlockEntity.class, OrbBlockEntity::spawnOrb);
        scene.idle(10);
        scene.overlay().showText(40)
                .text("")
                .pointAt(pos.getCenter())
                .placeNearTarget();
        scene.idle(60);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, 2, 0, true));

        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(100);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.addKeyframe();
        scene.overlay().showText(50)
                .text("")
                .pointAt(pos.getCenter())
                .placeNearTarget();
        scene.idle(70);
        scene.overlay().showText(40)
                .text("")
                .pointAt(pos.getCenter())
                .placeNearTarget();
        scene.idle(60);
        scene.world().modifyEntities(Orb.class, orb -> orb.addDestination(0, -2, 0, true));
        scene.idle(40);
    }

    private static ItemStack loadedDataCell() {
        var cell = ModItems.DATA_CELL.toStack();
        cell.set(ModComponentTypes.MACROS, new Macros(Map.of(), new HashSet<>()));
        return cell;
    }

    public static void dataCellConsole(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("console_macros", "Copying Macros with the Data Cell");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showText(80)
                .text("The Data Cell can copy saved macros between Consoles");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showControls(util.grid().at(3, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(ModItems.DATA_CELL.toStack());
        scene.idle(40);
        scene.overlay().showControls(util.grid().at(1, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.idle(40);
        scene.effects().indicateSuccess(util.grid().at(1, 1, 2));
    }

    public static void dataCellScreen(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("screen_data", "Copying Screen Data with the Data Cell");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showText(80)
                .text("The Data Cell can copy the graphics between Screens");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showControls(util.grid().at(3, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(ModItems.DATA_CELL.toStack());
        scene.idle(50);
        scene.overlay().showControls(util.grid().at(1, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.idle(20);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 2), ScreenBlockEntity.class, screen -> {
            screen.setBackgroundColor(new Color(85, 255, 254));
            screen.sendUpdate();
        });
        scene.effects().indicateSuccess(util.grid().at(1, 1, 2));
    }

    public static void dataCellScanner(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("scanner_pos", "Scanner Origin Binding with the Data Cell");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showControls(util.grid().at(1, 1, 3).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(ModItems.DATA_CELL.toStack());
        scene.idle(40);
        scene.overlay().showControls(util.grid().at(3, 1, 3).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.overlay().showControls(util.grid().at(3, 1, 1).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.idle(40);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
    }

    public static void processor(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("processor", "Using a Processor");
        scene.showBasePlate();
        var pos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(loadedDataCell());
        scene.idle(20);
        scene.world().setBlock(pos, ModBlocks.PROCESSOR.get().defaultBlockState(), false);
        scene.idle(40);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(100);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(60);
        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 0, 5, 1, 1).add(util.select().fromTo(0, 1, 3, 5, 1, 5)).add(util.select().position(4, 1, 2)).add(util.select().position(0, 1, 2)), Direction.DOWN);
        scene.overlay().showText(80)
                .text("")
                .pointAt(pos.getBottomCenter());
        scene.idle(100);
        scene.addKeyframe();
        scene.world().toggleRedstonePower(util.select().position(0, 1, 0));
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(0, 1, 0));
        scene.effects().indicateRedstone(pos);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(0, 1, 3), ScreenBlockEntity.class, screen -> {
            screen.setBackgroundColor(new Color(50, 200, 10));
            screen.sendUpdate();
        });
        scene.idle(20);
        scene.addKeyframe();
        scene.world().toggleRedstonePower(util.select().position(4, 1, 0));
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(4, 1, 0));
        scene.effects().indicateRedstone(pos);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(0, 1, 3), ScreenBlockEntity.class, screen -> {
            screen.setBackgroundColor(new Color(0, 80, 200));
            screen.sendUpdate();
        });
    }
}
