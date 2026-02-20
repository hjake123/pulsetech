package dev.hyperlynx.pulsetech.client.orb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class OrbRenderer extends EntityRenderer<Orb> {
    private static final ResourceLocation TEXTURE_LOCATION = Pulsetech.location("textures/entity/orb.png");
    private final OrbModel model;

    public OrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new OrbModel(context.bakeLayer(OrbModel.LAYER_LOCATION));
    }

    @Override
    public void render(Orb orb, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight) {
        Vec3 render_pos = orb.position();
        if(orb.hasNextDestination()) {
            Vec3 next_step = orb.getStep();
            Vec3 render_offset = next_step.scale(partialTick);
            render_pos = orb.position().add(render_offset);
        }

        renderOrbModel(orb, stack, source, packedLight);
        if(!Minecraft.getInstance().isPaused()) {
            if(orb.level().random.nextFloat() < 0.05F) {
                Minecraft.getInstance().level.addParticle(DustParticleOptions.REDSTONE, render_pos.x(), render_pos.y() + 0.15, render_pos.z(), 0, 0, 0);
            }
            if(orb.isPenDown()) {
                Minecraft.getInstance().level.addParticle(ParticleTypes.END_ROD, render_pos.x(), render_pos.y() + 0.15, render_pos.z(), 0, 0, 0);
            }
            if(orb.isProjectile()) {
                Minecraft.getInstance().level.addParticle(ParticleTypes.FLAME, render_pos.x(), render_pos.y() + 0.40, render_pos.z(), 0, 0, 0);
                Minecraft.getInstance().level.addParticle(ParticleTypes.SMOKE, render_pos.x(), render_pos.y() + 0.20, render_pos.z(), 0, 0, 0);
            }
        }
    }
    private void renderOrbModel(Orb orb, PoseStack stack, MultiBufferSource source, int packedLight) {
        stack.pushPose();
        stack.translate(0, -1.2F, 0);
        this.model.setupAnim(orb, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = source.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Orb orb) {
        return TEXTURE_LOCATION;
    }
}
