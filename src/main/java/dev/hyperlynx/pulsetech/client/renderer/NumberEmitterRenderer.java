package dev.hyperlynx.pulsetech.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.pulsetech.block.entity.NumberEmitterBlockEntity;
import dev.hyperlynx.pulsetech.block.entity.PatternBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;

public class NumberEmitterRenderer implements BlockEntityRenderer<NumberEmitterBlockEntity> {
    EntityRenderDispatcher dispatcher;

    public NumberEmitterRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(NumberEmitterBlockEntity be, float v, PoseStack stack, MultiBufferSource buffers, int i, int i1) {
        stack.pushPose();
        stack.translate(0.5, 0.75, 0.5);
        HyperNameTagRenderer.renderNameTag(dispatcher, Component.literal("" + be.getNumber()),
                    stack, buffers, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }
}
