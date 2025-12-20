package dev.hyperlynx.pulsetech.feature.orb;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.scanner.ScannerLinkable;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class OrbBlockEntity extends ProtocolBlockEntity implements ScannerLinkable {
    private @Nullable UUID orb = null;
    private BlockPos origin = getBlockPos();

    public OrbBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.ORB.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        origin = NbtUtils.readBlockPos(tag, "Origin").orElseGet(this::getBlockPos);
        if(tag.contains("Orb")) {
            orb = tag.getUUID("Orb");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Origin", NbtUtils.writeBlockPos(origin));
        if(orb != null) {
            tag.putUUID("Orb", orb);
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

    public void spawnOrb() {
        assert level != null;
        if(level instanceof ServerLevel slevel && slevel.getEntity(orb) != null) {
            slevel.getEntity(orb).kill();
        }
        Orb orb = ModEntityTypes.ORB.get().create(level);
        orb.setPos(getBlockPos().above().getCenter());
        this.orb = orb.getUUID();
        level.addFreshEntity(orb);
    }
}
