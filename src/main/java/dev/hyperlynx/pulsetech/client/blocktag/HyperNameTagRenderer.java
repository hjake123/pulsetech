package dev.hyperlynx.pulsetech.client.blocktag;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

// Contents adapted from EntityRenderer
public class HyperNameTagRenderer {
    protected static void renderNameTag(EntityRenderDispatcher dispatcher, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float f = Minecraft.getInstance().options.getBackgroundOpacity(0.35F);
        int j = (int) (f * 255.0F) << 24;
        Font font = Minecraft.getInstance().font;
        float f1 = (float) (-font.width(displayName) / 2);
        //font.drawInBatch(displayName, f1, (float) 0, 553648127, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, j, packedLight);
        font.drawInBatch(displayName, f1, (float) 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }
}
