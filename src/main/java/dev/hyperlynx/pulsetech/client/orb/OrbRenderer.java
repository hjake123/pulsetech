package dev.hyperlynx.pulsetech.client.orb;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;

public class OrbRenderer extends EntityRenderer<Orb> {
    public OrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Orb orb, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight) {
        Minecraft.getInstance().level.addParticle(ParticleTypes.END_ROD, orb.getX(), orb.getY(), orb.getZ(), 0, 0, 0);
        if(orb.penDown()) {
            Minecraft.getInstance().level.addParticle(ParticleTypes.SMOKE, orb.getX(), orb.getY(), orb.getZ(), 0, 0, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Orb orb) {
        return Pulsetech.location("error");
    }
}
