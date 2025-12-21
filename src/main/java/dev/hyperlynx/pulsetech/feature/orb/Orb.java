package dev.hyperlynx.pulsetech.feature.orb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Orb extends Entity {
    private static final float SPEED = 0.06F;
    private static final float HITBOX_SIZE = 1.0F;

    Queue<Destination> destinations = new ArrayDeque<>();
    BlockPos next_destination = null;
    public boolean grabbing = false;
    private @Nullable Entity grabbed = null;

    public Orb(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        if(next_destination == null) {
            if(!destinations.isEmpty()) {
                next_destination = destinations.poll().getPosition(blockPosition());
            }
        }

        if(next_destination != null) {
            Vec3 destination_pos = next_destination.getCenter();
            if(position().closerThan(destination_pos, SPEED + 0.01)) {
                next_destination = null;
            } else {
                Vec3 displacement = destination_pos.subtract(this.position());
                Vec3 step = displacement.normalize().multiply(SPEED, SPEED, SPEED);
                this.move(MoverType.SELF, step);
            }
        }

        if(grabbed != null) {
            if(grabbed.isRemoved()) {
                grabbed = null;
                grabbing = false;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("Destinations")) {
            destinations = new ArrayDeque<>(Destination.CODEC.listOf().decode(NbtOps.INSTANCE, tag.get("Destinations")).getOrThrow().getFirst());
        }
        if(tag.contains("NextDestination")) {
            next_destination = NbtUtils.readBlockPos(tag, "NextDestination").orElse(null);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if(!destinations.isEmpty()) {
            tag.put("Destinations", Destination.CODEC.listOf().encodeStart(NbtOps.INSTANCE, destinations.stream().toList()).getOrThrow());
        }
        if(next_destination != null) {
            tag.put("NextDestination", NbtUtils.writeBlockPos(next_destination));
        }
    }

    public void addDestination(int x, int y, int z, boolean relative) {
        destinations.add(new Destination(new BlockPos(x, y, z), relative));
    }

    public void toggleGrab() {
        if(grabbing) {
            if(grabbed != null) {
                grabbed.dismountTo(position().x, position().y, position().z);
                grabbed = null;
            }
            grabbing = false;
        } else {
            AABB hitbox = AABB.ofSize(position(), HITBOX_SIZE, HITBOX_SIZE, HITBOX_SIZE);
            List<Entity> in_range = level().getEntities(this, hitbox, entity -> !entity.is(this));
            if(in_range.isEmpty()) {
                return;
            }
            grabbed = in_range.getFirst();
            grabbed.startRiding(this);
            grabbing = true;
        }
    }

    private record Destination(BlockPos pos, boolean relative) {
        private static final Codec<Destination> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Destination::pos),
                Codec.BOOL.fieldOf("rel").forGetter(Destination::relative)
        ).apply(instance, Destination::new));

        public BlockPos getPosition(BlockPos current_position) {
            if(relative) {
                return current_position.offset(pos);
            }
            return pos;
        }
    }
}
