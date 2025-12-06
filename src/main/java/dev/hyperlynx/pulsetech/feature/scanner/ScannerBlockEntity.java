package dev.hyperlynx.pulsetech.feature.scanner;

import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ScannerBlockEntity extends ProtocolBlockEntity {
    private int mode = 0;

    public static final int RANGE = 16;

    public static final int MODE_ANY = 0;
    public static final int MODE_MONSTER = 1;
    public static final int MODE_ANIMAL = 2;
    public static final int MODE_ADULT = 3;
    public static final int MODE_CHILD = 4;
    public static final int MODE_ITEM = 5;
    public static final int MODE_OBJECT = 6;
    public static final int MAX_MODE = 6;

    public ScannerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.SCANNER.get(), pos, blockState);
    }

    public void setMode(int mode) {
        if(mode >= 0 && mode < MAX_MODE) {
            this.mode = mode;
            setChanged();
        }
    }

    private List<Entity> scan() {
        AABB scan_area = new AABB(getBlockPos()).inflate(RANGE);
        assert level != null;
        switch(mode) {
            case(MODE_ANY) -> {
                return level.getEntities((Entity) null, scan_area, entity -> true);
            }
            case(MODE_MONSTER) -> {
                return level.getEntities((Entity) null, scan_area, entity -> !entity.getType().getCategory().isFriendly());
            }
            case(MODE_ANIMAL) -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof Animal);
            }
            case(MODE_ADULT) -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof AgeableMob mob && !mob.isBaby());
            }
            case(MODE_CHILD) -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof AgeableMob mob && mob.isBaby());
            }
            case(MODE_ITEM) -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity instanceof ItemEntity);
            }
            case(MODE_OBJECT) -> {
                return level.getEntities((Entity) null, scan_area, entity -> entity.getType().getCategory().isFriendly() && !(entity instanceof AgeableMob) && !(entity instanceof ItemEntity));
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    public byte countNearby() {
        int count = scan().size();
        if(count > 127) {
            return 127;
        }
        return (byte) count;
    }
}
