package dev.hyperlynx.pulsetech.feature.orb;

import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerTextInfo;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModEntityTypes;
import dev.hyperlynx.pulsetech.util.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class OrbBlockEntity extends ProtocolBlockEntity implements ScannerLinkable {
    private @Nullable UUID orb_uuid = null;
    private BlockPos origin = getBlockPos();

    public OrbBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.ORB.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        origin = NbtUtils.readBlockPos(tag, "Origin").orElseGet(this::getBlockPos);
        if(tag.contains("Orb")) {
            orb_uuid = tag.getUUID("Orb");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Origin", NbtUtils.writeBlockPos(origin));
        if(orb_uuid != null) {
            tag.putUUID("Orb", orb_uuid);
        }
    }

    @Override
    public boolean setLinkedOrigin(BlockPos scanner_position) {
        assert level != null;
        if(level.isLoaded(scanner_position) && level.getBlockState(scanner_position).is(ModBlocks.SCANNER)) {
            this.origin = scanner_position;
            return true;
        }
        return false;
    }

    public @Nullable Orb getOrb() {
        if(level == null || level.isClientSide || orb_uuid == null) {
            return null;
        }
        Entity fetched = ((ServerLevel) level).getEntity(orb_uuid);
        if(!(fetched instanceof Orb orb_entity)) {
            Pulsetech.LOGGER.error("Entity {} should have been an orb! Unlinking...", orb_uuid);
            orb_uuid = null;
            return null;
        }
        return orb_entity;
    }

    public void spawnOrb() {
        assert level != null;
        if(level instanceof ServerLevel slevel && orb_uuid != null && slevel.getEntity(orb_uuid) != null) {
            Objects.requireNonNull(slevel.getEntity(orb_uuid)).kill();
        }
        Orb orb = ModEntityTypes.ORB.get().create(level);
        orb.setPos(getBlockPos().above().getCenter());
        this.orb_uuid = orb.getUUID();
        level.addFreshEntity(orb);
        ParticleScribe.drawParticleBox(level, DustParticleOptions.REDSTONE, orb.getBoundingBox().inflate(0.5F), 10);
    }

    public BlockPos getOrigin() {
        return origin;
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        Orb orb = getOrb();
        StringBuilder orb_status_builder = new StringBuilder();
        if(orb != null) {
            orb_status_builder.append(orb.blockPosition().subtract(origin).toShortString());
            if(orb.grabbing()) {
                orb_status_builder.append("\n").append(Component.translatable("debugger.pulsetech.orb_grabbing").getString()).append(orb.getGrabbed().getName().getString());
            }
            if(orb.penDown()) {
                orb_status_builder.append("\n").append(Component.translatable("debugger.pulsetech.orb_pen_down").getString());
            }
            if(orb.isProjectile()) {
                orb_status_builder.append("\n").append(Component.translatable("debugger.pulsetech.orb_projectile").getString());
            }
        } else {
            orb_status_builder.append(Component.translatable("debugger.pulsetech.no_orb").getString());
        }
        return super.getDebuggerInfoManifest().append(new DebuggerInfoManifest.Entry(
                Component.translatable("debugger.pulsetech.orb_data").getString(),
                DebuggerInfoTypes.TEXT.value(),
                () -> new DebuggerTextInfo(orb_status_builder.toString())
        ));
    }
}
