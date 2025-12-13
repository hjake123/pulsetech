package dev.hyperlynx.pulsetech.client.blocktag;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.scope.ScopeBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ScopeBlockRenderer implements BlockEntityRenderer<ScopeBlockEntity> {
    final EntityRenderDispatcher renderer;

    public ScopeBlockRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getEntityRenderer();
    }

    /// Render the graph on the front.
    /// For the last seven bits in the buffer, there should be a different drawing depending on whether it is on or off, and whether it matches the prior state
    /// ON, OFF, RISING, and FALLING have different appearances.
    @Override
    public void render(ScopeBlockEntity scope, float partial_tick, PoseStack stack, MultiBufferSource buffers, int a, int b) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png")));
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(180));
        stack.translate(-0.5, -0.5, 0.501);
        Sequence pattern = scope.getPattern();
        for (int i = pattern.length() - 14; i < pattern.length(); i++) {
            if (i < 0) {
                continue;
            }
            if (pattern.get(i)) {
                drawDisplayBox(consumer, stack, 0xFFFF0000, i, 4, i + 1, 5);
            } else {
                drawDisplayBox(consumer, stack, 0xFFAA0000, i, 1, i + 1, 2);
            }
        }

        stack.popPose();
    }

    private void drawDisplayBox(VertexConsumer consumer, PoseStack stack, int color, int display_x1, int display_y1, int display_x2, int display_y2) {
        drawDisplayBoxRaw(consumer, stack, color,display_x1 + 1, display_y1 + 3, display_x2 + 1, display_y2 + 3);
    }

    private void drawDisplayBoxRaw(VertexConsumer consumer, PoseStack stack, int color, int display_x1, int display_y1, int display_x2, int display_y2) {
        addVertex(consumer, stack, display_x1 / 16.0F, display_y1 / 16.0F, 0, color, LightTexture.FULL_BRIGHT);
        addVertex(consumer, stack, display_x2 / 16.0F, display_y1 / 16.0F, 0, color, LightTexture.FULL_BRIGHT);
        addVertex(consumer, stack, display_x2 / 16.0F, display_y2 / 16.0F, 0, color, LightTexture.FULL_BRIGHT);
        addVertex(consumer, stack, display_x1 / 16.0F, display_y2 / 16.0F, 0, color, LightTexture.FULL_BRIGHT);
    }

    private void addVertex(VertexConsumer consumer, PoseStack stack, float x, float y, float z, int color, int packedLight) {
        consumer.addVertex(stack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(stack.last(), 0, 1, 0);
    }
}
