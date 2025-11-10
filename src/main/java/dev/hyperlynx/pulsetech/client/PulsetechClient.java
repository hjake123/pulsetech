package dev.hyperlynx.pulsetech.client;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.client.renderer.NumberBlockRenderer;
import dev.hyperlynx.pulsetech.client.renderer.PatternBlockRenderer;
import dev.hyperlynx.pulsetech.client.gui.ConsoleScreen;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = Pulsetech.MODID, dist = Dist.CLIENT)
public class PulsetechClient {

    public PulsetechClient(ModContainer container) {
        IEventBus bus = container.getEventBus();
        if(bus == null) {
            throw new IllegalStateException("Mod constructor needs event bus");
        }
        bus.addListener(this::registerEntityRenderers);
    }


    protected void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PATTERN_DETECTOR.get(), PatternBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PATTERN_EMITTER.get(), PatternBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.NUMBER_EMITTER.get(), NumberBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.NUMBER_MONITOR.get(), NumberBlockRenderer::new);
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
}
