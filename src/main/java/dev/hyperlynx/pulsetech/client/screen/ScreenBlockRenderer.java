package dev.hyperlynx.pulsetech.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.feature.scope.ScopeBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenBlockEntity;
import dev.hyperlynx.pulsetech.feature.screen.ScreenData;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ScreenBlockRenderer implements BlockEntityRenderer<ScreenBlockEntity> {
    final EntityRenderDispatcher renderer;

    public ScreenBlockRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getEntityRenderer();
    }

    /// Render the graph on the front
    @Override
    public void render(ScreenBlockEntity screen, float partial_tick, PoseStack stack, MultiBufferSource buffers, int a, int b) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png")));
        stack.pushPose();
        adjustForFacing(stack, screen.getBlockState().getValue(PulseBlock.FACING));
        ScreenData data = screen.getScreenData();
        drawDisplayBox(consumer, stack, 0xFF000000 | data.bg_color().hex(), 0, 0, 14, 14);
        stack.translate(0, 0, 0.005);
        for(ScreenData.Pixel pixel : data.fg()) {
            drawDisplayBox(consumer, stack, pixel.color().hex(), pixel.x(), pixel.y(), pixel.x() + 1, pixel.y()+1);
        }
        stack.popPose();
    }

    private void adjustForFacing(PoseStack stack, Direction facing) {
        switch (facing) {
            case NORTH -> {
                stack.translate(0.5, 0.5, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.translate(-0.5, -0.5, 0.480);
            }
            case EAST -> {
                stack.translate(0.5, 0.5, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(90));
                stack.translate(-0.5, -0.5, 0.480);
            }
            case WEST -> {
                stack.translate(0.5, 0.5, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(270));
                stack.translate(-0.5, -0.5, 0.480);
            }
            default -> { // SOUTH
                stack.translate(0, 0, 1.001);
            }
        }
    }

    private void drawDisplayBox(VertexConsumer consumer, PoseStack stack, int color, int display_x1, int display_y1, int display_x2, int display_y2) {
        drawDisplayBoxRaw(consumer, stack, color, display_x1 + 2, display_y1 + 2, display_x2 + 2, display_y2 + 2);
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
