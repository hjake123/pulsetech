package dev.hyperlynx.pulsetech.integration.ponder;

import dev.hyperlynx.pulsetech.core.program.Macros;
import dev.hyperlynx.pulsetech.feature.number.bulb.NumberBulbBlock;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import dev.hyperlynx.pulsetech.feature.orb.OrbBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlockEntity;
import dev.hyperlynx.pulsetech.feature.storage.RetrieverBlock;
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

    public static void remoteConsole(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("remote_console", "Using a Remote Console");
        scene.showBasePlate();
        BlockPos pos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick()
                .withItem(ModItems.REMOTE_CONSOLE.toStack());
        scene.idle(20);
        scene.effects().indicateSuccess(pos);
        scene.idle(30);

        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
    }

    public static void remoteConsoleStorageModem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("remote_console_storage_modem", "Using a Remote Console on a Storage Modem");
        scene.showBasePlate();
        BlockPos pos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick()
                .withItem(ModItems.REMOTE_CONSOLE.toStack());
        scene.idle(20);
        scene.effects().indicateSuccess(pos);
        scene.idle(30);

        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
    }

    public static void analogEmitter(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("analog_emitter", "Using an Analog Number Emitter");
        scene.showBasePlate();
        BlockPos pos = util.grid().at(3, 1, 3);
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .text("");
        scene.idle(100);

        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 4, 5, 1, 5), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(4, 1, 3, 6, 1, 4), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 2, 1, 4), Direction.DOWN);
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.overlay().showText(40)
                .text("")
                .pointAt(util.grid().at(3, 1, 4).getBottomCenter())
                .placeNearTarget();
        scene.idle(60);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.idle(80);
        scene.effects().indicateRedstone(util.grid().at(5, 1, 3));
        scene.overlay().showText(40)
                .text("")
                .pointAt(util.grid().at(0, 1, 3).getCenter())
                .placeNearTarget();
        scene.idle(60);

        scene.addKeyframe();
        scene.overlay().showText(60)
                .text("")
                .pointAt(pos.getCenter());
        scene.world().showSection(util.select().fromTo(0, 1, 0, 6, 1, 2), Direction.DOWN);
        scene.idle(80);
    }

    public static void numberMonitorAnalog(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("analog_monitor", "Getting an analog signal from a Number Monitor");
        scene.showBasePlate();
        scene.world().showSection(util.select().fromTo(0, 1, 0, 6, 1, 2), Direction.DOWN);
        scene.overlay().showText(80)
                .text("")
                .pointAt(util.grid().at(4, 1, 1).getBottomCenter());
        scene.idle(100);
        scene.overlay().showText(40)
                .text("")
                .pointAt(util.grid().at(3, 1, 1).getBottomCenter())
                .placeNearTarget();
        scene.idle(60);
        scene.overlay().showText(60)
                .text("")
                .pointAt(util.grid().at(4, 1, 1).getBottomCenter());
        scene.idle(80);

        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 3, 6, 1, 3), Direction.DOWN);
        scene.overlay().showText(80)
                .text("")
                .pointAt(util.grid().at(3, 1, 3).getBottomCenter());
        scene.idle(100);
    }

    public static void numberBulb(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("number_bulb", "Using a Number Bulb");
        scene.showBasePlate();
        BlockPos pos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(pos), 80)
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .pointAt(util.select().position(pos).getCenter())
                .text("");
        scene.idle(80);

        scene.addKeyframe();
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(10);
        scene.effects().indicateSuccess(pos);
        scene.world().modifyBlock(pos, state -> state.setValue(NumberBulbBlock.STORED_VALUES, 1), false);
        scene.idle(40);

        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(2, 2, 2, 2, 4, 2), Direction.DOWN);
        scene.overlay().showText(80)
                .pointAt(util.select().position(pos).getCenter())
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .pointAt(util.select().position(pos).getCenter())
                .text("");
        scene.idle(10);
        scene.world().modifyBlock(pos.above(), state -> state.setValue(NumberBulbBlock.STORED_VALUES, 1), false);
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(10);
        scene.world().modifyBlock(pos.above(), state -> state.setValue(NumberBulbBlock.STORED_VALUES, 2), false);
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(10);
        scene.world().modifyBlock(pos.above(2), state -> state.setValue(NumberBulbBlock.STORED_VALUES, 1), false);
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(10);
        scene.world().modifyBlock(pos.above(2), state -> state.setValue(NumberBulbBlock.STORED_VALUES, 2), false);
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(10);
        scene.world().modifyBlock(pos.above(2), state -> state.setValue(NumberBulbBlock.STORED_VALUES, 1), false);
        scene.effects().indicateRedstone(util.grid().at(4, 1, 1));
        scene.idle(30);

        scene.addKeyframe();
        scene.overlay().showText(80)
                .pointAt(util.select().position(pos).getCenter())
                .text("");
        scene.idle(100);
        scene.overlay().showText(60)
                .pointAt(util.select().position(pos).getCenter())
                .text("");
        scene.idle(20);
        scene.overlay().showControls(pos.getCenter(), Pointing.DOWN, 40)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(60);
    }

    public static void storageSystem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("storage", "Using the storage system");
        scene.showBasePlate();
        scene.overlay().showText( 160)
                .text("Two blocks work together to form an item filtering system you can use to help automate your storage systems:");
        scene.idle(40);

        BlockPos retriever_pos = util.grid().at(2, 2, 2);
        BlockPos modem_pos = util.grid().at(2, 2, 0);

        scene.world().showSection(util.select().position(retriever_pos), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showOutlineWithText(util.select().position(retriever_pos), 40)
                        .text("The Retriever...");
        scene.idle(60);
        scene.world().showSection(util.select().position(modem_pos), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showOutlineWithText(util.select().position(modem_pos), 40)
                        .text("...and the Storage Modem");
        scene.idle(80);

        scene.addKeyframe();
        scene.world().hideSection(util.select().position(modem_pos), Direction.DOWN);
        scene.world().showSection(util.select().position(retriever_pos.above()), Direction.DOWN);
        scene.overlay().showText(80)
                .pointAt(retriever_pos.getCenter())
                .text("The Retriever can take items from the inventory above it");
        scene.idle(100);
        scene.overlay().showText(80)
                .pointAt(retriever_pos.getCenter())
                .text("and move them below itself");
        scene.idle(40);
        scene.effects().indicateSuccess(retriever_pos);
        scene.world().createItemEntity(retriever_pos.below().getCenter(), Vec3.ZERO, Items.REDSTONE.getDefaultInstance());
        scene.idle(60);

        scene.addKeyframe();
        scene.overlay().showText(80)
                .pointAt(retriever_pos.getCenter())
                .text("Right clicking will open the Retriever, letting items flow through it automatically");
        scene.idle(100);
        scene.overlay().showControls(retriever_pos.getCenter(), Pointing.DOWN, 40)
                        .rightClick();
        scene.idle(20);
        scene.world().modifyBlock(retriever_pos, state -> state.setValue(RetrieverBlock.OPEN, true), false);
        scene.world().createItemEntity(retriever_pos.below().getCenter(), Vec3.ZERO, Items.REDSTONE.getDefaultInstance());
        scene.idle(3);
        scene.world().createItemEntity(retriever_pos.below().getCenter(), Vec3.ZERO, Items.REDSTONE.getDefaultInstance());
        scene.idle(3);
        scene.world().createItemEntity(retriever_pos.below().getCenter(), Vec3.ZERO, Items.REDSTONE.getDefaultInstance());
        scene.idle(3);
        scene.world().createItemEntity(retriever_pos.below().getCenter(), Vec3.ZERO, Items.REDSTONE.getDefaultInstance());
        scene.idle(3);
        scene.world().modifyBlock(retriever_pos, state -> state.setValue(RetrieverBlock.OPEN, false), false);
        scene.idle(40);
        scene.addKeyframe();
        scene.world().showSection(util.select().position(retriever_pos.below()), Direction.NORTH);
        scene.world().modifyEntities(ItemEntity.class, Entity::kill);
        scene.overlay().showText(80)
                .pointAt(retriever_pos.below().getCenter())
                .text("The Retriever will also transfer to an inventory below itself if there is one");
        scene.idle(100);

        scene.addKeyframe();
        scene.world().showSection(util.select().fromTo(0, 1, 0, 2, 5, 1), Direction.DOWN);
        scene.overlay().showText(80)
                .text("By default, the Retriever will move any item, but with a Storage Modem, an Item Filter can be applied to the Retriever");
        scene.idle(100);
        scene.overlay().showText(60)
                .pointAt(modem_pos.getCenter())
                .text("Right click on the Storage Modem to access its menu");
        scene.overlay().showControls(modem_pos.getCenter(), Pointing.DOWN, 60)
                        .rightClick();
        scene.idle(80);
        scene.overlay().showText(80)
                .pointAt(modem_pos.getCenter())
                .text("You can create Item Filters with items in your inventory or by dragging items from JEI");
        scene.idle(100);
        scene.overlay().showText(80)
                .text("Once you've set up some Item Filters, connect the Storage Modem to the Retriever and press \"Sync\" inside the Storage Modem's menu");
        scene.idle(100);
        scene.effects().indicateRedstone(modem_pos.south());
        scene.idle(20);
        scene.effects().indicateSuccess(retriever_pos);
        scene.idle(20);
        scene.overlay().showText(80)
                .text("This will transmit the Item Filters to all connected Retrievers");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.overlay().showText(80)
                .text("");
        scene.idle(100);
        scene.overlay().showText(80)
                .text("The Retriever also has commands to retrieve only a set number of matching items, to count matching items in the above inventory, and more. Check its datasheet for details");
        scene.idle(60);
        scene.overlay().showControls(retriever_pos.getCenter(), Pointing.DOWN, 60)
                .rightClick().withItem(ModItems.DATASHEET.toStack());
        scene.idle(80);
    }

    public static void dataCellStorageModem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("datacell_item_filters", "Copying Item Filters with a Data Cell");
        scene.showBasePlate();
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.overlay().showText(80)
                .text("The Data Cell can copy Item Filters between Storage Modems");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showControls(util.grid().at(3, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(ModItems.DATA_CELL.toStack());
        scene.idle(50);
        scene.overlay().showControls(util.grid().at(1, 1, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.idle(20);
        scene.effects().indicateSuccess(util.grid().at(1, 1, 2));
    }

    public static void dataCellRetriever(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("datacell_retriever", "Setting a Retriever's Item Filters with a Data Cell");
        scene.showBasePlate();
        scene.world().showSection(util.select().column(2, 2), Direction.DOWN);
        scene.world().showSection(util.select().column(2, 0), Direction.DOWN);
        scene.overlay().showText(80)
                .text("The Data Cell can copy Item Filters into a Retriever");
        scene.idle(100);
        scene.addKeyframe();
        scene.overlay().showControls(util.grid().at(2, 2, 0).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(ModItems.DATA_CELL.toStack());
        scene.idle(50);
        scene.overlay().showControls(util.grid().at(2, 2, 2).getCenter(), Pointing.DOWN, 30)
                .rightClick().withItem(loadedDataCell());
        scene.idle(20);
        scene.effects().indicateSuccess(util.grid().at(2, 2, 2));
        scene.addKeyframe();
        scene.overlay().showText(80)
                .text("This has the same effect as the Storage Modem's \"Sync\" button, but forgoes the need to connect the two blocks");
        scene.idle(100);
    }
}
