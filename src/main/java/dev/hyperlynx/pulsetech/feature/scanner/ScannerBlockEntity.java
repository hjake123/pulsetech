package dev.hyperlynx.pulsetech.feature.scanner;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.orb.Orb;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ScannerBlockEntity extends ProtocolBlockEntity {
    public static final int RANGE = 16;

    public ScannerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.SCANNER.get(), pos, blockState);
    }

    public void setMode(int index) {
        for(ScannerBlock.Mode mode : ScannerBlock.Mode.values()) {
            if(mode.index == index) {
                assert level != null;
                level.setBlock(getBlockPos(), getBlockState().setValue(ScannerBlock.MODE, mode), Block.UPDATE_ALL);
                break;
            }
        }
    }

    private List<Entity> scan() {
        AABB scan_area = new AABB(getBlockPos()).inflate(RANGE);
        assert level != null;
        switch(getBlockState().getValue(ScannerBlock.MODE)) {
            case ScannerBlock.Mode.ANY -> {
                return level.getEntities((Entity) null, scan_area, entity -> true);
            }
            case ScannerBlock.Mode.MONSTER -> {
                return level.getEntities((Entity) null, scan_area, entity -> !entity.getType().getCategory().isFriendly());
            }
            case ScannerBlock.Mode.ANIMAL  -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof Animal);
            }
            case ScannerBlock.Mode.ADULT  -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof AgeableMob mob && !mob.isBaby());
            }
            case ScannerBlock.Mode.CHILD -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof AgeableMob mob && mob.isBaby());
            }
            case ScannerBlock.Mode.ITEM -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof ItemEntity);
            }
            case ScannerBlock.Mode.OBJECT -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity.getType().getCategory().isFriendly() && !(entity instanceof AgeableMob) && !(entity instanceof ItemEntity) &&!(entity instanceof Player) && !(entity instanceof Orb));
            }
            case ScannerBlock.Mode.PLAYER -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof Player);
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    public boolean testAnyNearby() {
        return scan().isEmpty();
    }

    public byte countNearby() {
        int count = scan().size();
        if(count > 127) {
            return 127;
        }
        return (byte) count;
    }

    public BlockPos findNearest() {
        var nearby = scan();
        double dist = Double.POSITIVE_INFINITY;
        Entity nearest = null;
        for (Entity entity : nearby) {
            double entity_dist = entity.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
            if (entity_dist < dist) {
                dist = entity_dist;
                nearest = entity;
            }
        }
        if(nearest == null) {
            return BlockPos.ZERO;
        }
        return new BlockPos(nearest.getBlockX() - getBlockPos().getX(), nearest.getBlockY() - getBlockPos().getY(), nearest.getBlockZ() - getBlockPos().getZ());
    }
}
