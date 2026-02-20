package dev.hyperlynx.pulsetech.client.blocktag;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.pulsetech.feature.number.NumberKnower;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NumberBlockRenderer<T extends BlockEntity & NumberKnower> implements BlockEntityRenderer<T> {
    private final EntityRenderDispatcher dispatcher;

    public NumberBlockRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(T be, float v, PoseStack stack, MultiBufferSource buffers, int i, int i1) {
        stack.pushPose();
        stack.translate(0.5, 0.75, 0.5);
        HyperNameTagRenderer.renderNameTag(dispatcher, Component.literal("" + be.getNumber()),
                    stack, buffers, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }
}
