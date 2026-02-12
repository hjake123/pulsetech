package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.debugger.DebuggerScreen;
import dev.hyperlynx.pulsetech.client.number.NumberChooseScreen;
import dev.hyperlynx.pulsetech.client.orb.OrbModel;
import dev.hyperlynx.pulsetech.client.orb.OrbRenderer;
import dev.hyperlynx.pulsetech.client.scope.ScopeBlockRenderer;
import dev.hyperlynx.pulsetech.client.datasheet.DatasheetScreen;
import dev.hyperlynx.pulsetech.client.pattern.SequenceChooseScreen;
import dev.hyperlynx.pulsetech.client.blocktag.NumberBlockRenderer;
import dev.hyperlynx.pulsetech.client.blocktag.PatternBlockRenderer;
import dev.hyperlynx.pulsetech.client.console.ConsoleScreen;
import dev.hyperlynx.pulsetech.client.screen.ScreenBlockRenderer;
import dev.hyperlynx.pulsetech.core.PatternHolder;
import dev.hyperlynx.pulsetech.feature.datacell.DataCellItem;
import dev.hyperlynx.pulsetech.feature.datasheet.Datasheet;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerByteInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerPosInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerSequenceInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerTextInfo;
import dev.hyperlynx.pulsetech.feature.number.block.NumberEmitterBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenData;
import dev.hyperlynx.pulsetech.ponder.PulsetechPonderPlugin;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Pulsetech.MODID, dist = Dist.CLIENT)
public class PulsetechClient {

    public PulsetechClient(ModContainer container) {
        IEventBus bus = container.getEventBus();
        if(bus == null) {
            throw new IllegalStateException("Mod constructor needs event bus");
        }
        bus.addListener(this::onClientSetup);
        bus.addListener(this::registerEntityRenderers);
        bus.addListener(this::registerLayerDefinitions);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void openDebuggerScreen(DebuggerInfoManifest manifest) {
        Minecraft.getInstance().setScreen(new DebuggerScreen(manifest));
    }

    public static void acceptDebuggerSequenceInfo(DebuggerSequenceInfo info) {
        if(Minecraft.getInstance().screen instanceof DebuggerScreen screen) {
            screen.acceptInfo(info.sequence());
        }
    }

    public static void acceptDebuggerByteInfo(DebuggerByteInfo info) {
        if(Minecraft.getInstance().screen instanceof DebuggerScreen screen) {
            screen.acceptInfo(info.number());
        }    }

    public static void acceptDebuggerTextInfo(DebuggerTextInfo info) {
        if(Minecraft.getInstance().screen instanceof DebuggerScreen screen) {
            screen.acceptInfo(info.text());
        }
    }

    public static void acceptDebuggerPosInfo(DebuggerPosInfo info) {
        if(Minecraft.getInstance().screen instanceof DebuggerScreen screen) {
            screen.acceptInfo(info.pos());
        }
    }

    protected void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.DATA_CELL.get(),
                    Pulsetech.location("loaded"),
                    (stack, level, player, seed) -> DataCellItem.getLoadedProperty(stack)
            );
            PonderIndex.addPlugin(new PulsetechPonderPlugin());
        });
    }

    protected void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PATTERN_DETECTOR.get(), PatternBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PATTERN_EMITTER.get(), PatternBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.NUMBER_EMITTER.get(), NumberBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.NUMBER_MONITOR.get(), NumberBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SCOPE.get(), ScopeBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SCREEN.get(), ScreenBlockRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ORB.get(), OrbRenderer::new);
    }

    protected void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OrbModel.LAYER_LOCATION, OrbModel::createBodyLayer);
    }

    protected static void openConsoleScreen(BlockPos pos, String prior_lines) {
        Minecraft.getInstance().setScreen(new ConsoleScreen(pos, prior_lines));
    }


    protected static void acceptConsoleLine(BlockPos pos, String line) {
        Screen current_screen = Minecraft.getInstance().screen;
        if(current_screen instanceof ConsoleScreen console) {
            if(console.getPos().equals(pos)) {
                console.addReadoutLine(line);
            }
        }
    }

    protected static void setPriorConsoleLines(BlockPos pos, String lines) {
        Screen current_screen = Minecraft.getInstance().screen;
        if(current_screen instanceof ConsoleScreen console) {
            if(console.getPos().equals(pos)) {
                console.setPriorLines(lines);
            }
        }
    }

    public static void openSequenceScreen(BlockPos pos) {
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(pos) instanceof PatternHolder bearer) {
            Minecraft.getInstance().setScreen(new SequenceChooseScreen(pos, bearer));
        }
    }

    public static void openNumberChooseScreen(BlockPos pos) {
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(pos) instanceof NumberEmitterBlockEntity emitter) {
            Minecraft.getInstance().setScreen(new NumberChooseScreen(pos, emitter));
        }
    }

    public static void openDatasheetScreen(Datasheet datasheet) {
        Minecraft.getInstance().setScreen(new DatasheetScreen(datasheet));
    }

    public static void updateScreenBlock(ScreenData screenData, BlockPos pos) {
        if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof ScreenBlockEntity screen) {
            screen.setScreenData(screenData);
        }
    }
}
