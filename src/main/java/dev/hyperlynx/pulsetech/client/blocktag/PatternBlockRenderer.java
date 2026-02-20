package dev.hyperlynx.pulsetech.client.blocktag;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.pulsetech.core.PatternHolder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PatternBlockRenderer <T extends BlockEntity & PatternHolder> implements BlockEntityRenderer<T> {
    private final EntityRenderDispatcher dispatcher;

    public PatternBlockRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(T pattern_be, float v, PoseStack stack, MultiBufferSource buffers, int i, int i1) {
        stack.pushPose();
        stack.translate(0.5, 0.75, 0.5);
        if(!pattern_be.getPattern().isEmpty()) {
            HyperNameTagRenderer.renderNameTag(dispatcher, Component.literal(pattern_be.getPattern().toString()),
                    stack, buffers, LightTexture.FULL_BRIGHT);
        }
        stack.popPose();
    }
}
