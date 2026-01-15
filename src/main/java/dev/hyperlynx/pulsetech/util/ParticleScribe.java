package dev.hyperlynx.pulsetech.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class ParticleScribe {
    public static void drawParticle(Level level, ParticleOptions opt, double x, double y, double z) {
        if (level.isClientSide()) {
            level.addParticle(opt, x, y, z, 0, 0, 0);
        } else {
            ((ServerLevel) level).sendParticles(opt, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void drawParticle(Level level, ParticleOptions opt, double x, double y, double z, float odds, double xspeed, double yspeed, double zspeed) {
        if(level.random.nextFloat() < odds){
            if (level.isClientSide()) {
                level.addParticle(opt, x, y, z, xspeed, yspeed, zspeed);
            } else {
                ((ServerLevel) level).sendParticles(opt, x, y, z, 1, xspeed, yspeed, zspeed, 0.0);
            }
        }
    }

    public static void drawParticleBox(Level level, ParticleOptions opt, AABB aabb, int frequency) {
        double x, y, z;
        for(int i = 0; i < frequency; i++){
            x = level.random.nextDouble() * (aabb.maxX - aabb.minX) + aabb.minX;
            y = level.random.nextDouble() * (aabb.maxY - aabb.minY) + aabb.minY;
            z = level.random.nextDouble() * (aabb.maxZ - aabb.minZ) + aabb.minZ;
            drawParticle(level, opt, x, y, z);
        }
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, BlockPos a, BlockPos b, int frequency, double noise){
        drawParticleLine(level, opt, a.getX()+0.5, a.getY()+0.5,a.getZ()+0.5,
                b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5, frequency, noise);
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, Vec3 a, Vec3 b, int frequency, double noise){
        drawParticleLine(level, opt, a.x, a.y,a.z, b.x, b.y, b.z, frequency, noise);
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency, double noise) {
        for (int i = 0; i < frequency; i++) {
            double u = level.random.nextDouble();
            double x = (1 - u) * x1 + u * x2;
            double y = (1 - u) * y1 + u * y2;
            double z = (1 - u) * z1 + u * z2;

            x += (level.random.nextFloat() - 0.5) * noise;
            y += (level.random.nextFloat() - 0.5) * noise;
            z += (level.random.nextFloat() - 0.5) * noise;

            drawParticle(level, opt, x, y, z);
        }
    }

    public static void drawParticleFrame(Level level, ParticleOptions opt, AABB box, int frequency, double noise) {
        int edge_frequency = frequency/12 + 1;

        // Bottom square
        drawParticleLine(level, opt, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, frequency, noise);
        drawParticleLine(level, opt, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, frequency, noise);
        drawParticleLine(level, opt, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, frequency, noise);
        drawParticleLine(level, opt, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, frequency, noise);

        // Top square
        drawParticleLine(level, opt, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, frequency, noise);
        drawParticleLine(level, opt, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, frequency, noise);
        drawParticleLine(level, opt, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, frequency, noise);
        drawParticleLine(level, opt, box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ, frequency, noise);

        // Vertical edges
        drawParticleLine(level, opt, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, frequency, noise);
        drawParticleLine(level, opt, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, frequency, noise);
        drawParticleLine(level, opt, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, frequency, noise);
        drawParticleLine(level, opt, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, frequency, noise);
    }

    public static void drawParticleFrame(Level level, ParticleOptions opt, BlockPos blockPos, int frequency, float noise) {
        drawParticleFrame(level, opt, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(blockPos)), frequency, noise);
    }

    public static void drawParticleZigZag(Level level, ParticleOptions opt, BlockPos a, BlockPos b, int frequency, int segments, double noise){
        drawParticleZigZag(level, opt, a.getX()+0.5, a.getY()+0.5, a.getZ()+0.5, b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5, frequency, segments, noise);
    }

    public static void drawParticleZigZag(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency, int segments, double noise){
        double prev_x = x1;
        double prev_y = y1;
        double prev_z = z1;

        // For each line segment:
        // - Find a minimum and maximum length ('progress')
        // - Choose an actual length with these as bounds
        // - Adjust x y and z by the chosen progress
        // - Deflect x y and z by a random amount
        for(int i = 0; i < segments; i++){
            double next_x;
            double next_y;
            double next_z;

            if(i == segments-1){
                next_x = x2;
                next_y = y2;
                next_z = z2;
            }else{
                double min_progress = 1.0/(segments+3);
                double max_progress = segments==3 ? 1.0 : 1.0/(segments-3);
                double actual_progress = ThreadLocalRandom.current().nextDouble(min_progress, max_progress);

                double x_dist = Math.abs(x2 - prev_x) * actual_progress;
                double y_dist = Math.abs(y2 - prev_y) * actual_progress;
                double z_dist = Math.abs(z2 - prev_z) * actual_progress;

                if (x2 > 0)
                    next_x = x2 > prev_x ? prev_x + x_dist : prev_x - x_dist;
                else
                    next_x = x2 < prev_x ? prev_x - x_dist : prev_x + x_dist;

                if (y2 > 0)
                    next_y = y2 > prev_y ? prev_y + y_dist : prev_y - y_dist;
                else
                    next_y = y2 < prev_y ? prev_y - y_dist : prev_y + y_dist;

                if (z2 > 0)
                    next_z = z2 > prev_z ? prev_z + z_dist : prev_z - z_dist;
                else
                    next_z = z2 < prev_z ? prev_z - z_dist : prev_z + z_dist;

                next_x += (level.random.nextFloat()-0.5) * noise;
                next_y += (level.random.nextFloat()-0.5) * noise;
                next_z += (level.random.nextFloat()-0.5) * noise;
            }
            drawParticleLine(level, opt, prev_x, prev_y, prev_z, next_x, next_y, next_z, frequency, 0);

            prev_x = next_x;
            prev_y = next_y;
            prev_z = next_z;
        }
    }

    public static void drawParticleRing(Level level, ParticleOptions opt, BlockPos pos, double height, double radius, int frequency){
        drawExactParticleRing(level, opt, Vec3.atBottomCenterOf(pos).add(0, height, 0), radius, frequency);
    }

    public static void drawExactParticleRing(Level level, ParticleOptions opt, Vec3 pos, double radius, int frequency){
        for(int i = 0; i < frequency; i++){
            int deflection_angle = level.random.nextInt(1, 360);
            drawDeflectedParticle(level, opt, pos, 0, radius, deflection_angle);
        }
    }

    public static void drawDeflectedParticle(Level level, ParticleOptions opt, Vec3 pos, double height, double radius, int deflection_angle) {
        double x = Math.cos(Math.toRadians(deflection_angle)) * radius + pos.x;
        double z = Math.sin(Math.toRadians(deflection_angle)) * radius + pos.z;
        drawParticle(level, opt, x, pos.y + height, z);
    }

    public static void drawParticleSphere(Level level, ParticleOptions opt, BlockPos pos, double height, double radius, int frequency){
        double center_x = pos.getX() + 0.5;
        double center_z = pos.getZ() + 0.5;

        drawExactParticleSphere(level, opt, new Vec3(center_x, pos.getY(), center_z), height, radius, frequency);
    }

    public static void drawExactParticleSphere(Level level, ParticleOptions opt, Vec3 pos, double height, double radius, int frequency) {
        for(int i = 0; i < frequency; i++){
            double x = level.random.nextGaussian();
            double y = level.random.nextGaussian();
            double z = level.random.nextGaussian();
            double normalizer = 1 / Math.sqrt(x * x + y * y + z * z);

            x = x * normalizer * radius;
            y = y * normalizer * radius;
            z = z * normalizer * radius;

            drawParticle(level, opt, pos.x + x, pos.y + height + y, pos.z + z);
        }
    }
}
