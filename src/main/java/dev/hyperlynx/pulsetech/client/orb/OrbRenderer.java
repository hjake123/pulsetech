package dev.hyperlynx.pulsetech.client.orb;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class OrbRenderer extends EntityRenderer<Orb> {
    public OrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Orb orb, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight) {
        Vec3 render_pos = orb.position();
        if(orb.hasNextDestination()) {
            Vec3 next_step = orb.getStep();
            render_pos = orb.position().add(next_step.scale(partialTick));
        }

        Minecraft.getInstance().level.addParticle(ParticleTypes.END_ROD, render_pos.x(), render_pos.y(), render_pos.z(), 0, 0, 0);
        if(orb.penDown()) {
            Minecraft.getInstance().level.addParticle(ParticleTypes.GLOW, render_pos.x(), render_pos.y(), render_pos.z(), 0, 0, 0);
        }
        if(orb.isProjectile()) {
            Minecraft.getInstance().level.addParticle(ParticleTypes.FLAME, render_pos.x(), render_pos.y(), render_pos.z(), 0, 0, 0);
            Minecraft.getInstance().level.addParticle(ParticleTypes.SMOKE, render_pos.x(), render_pos.y(), render_pos.z(), 0, 0, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Orb orb) {
        return Pulsetech.location("error");
    }
}
