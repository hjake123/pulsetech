package dev.hyperlynx.pulsetech.feature.orb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Orb extends Entity {
    private static final float SPEED = 0.06F;
    private static final float HITBOX_SIZE = 1.5F;
    private static final int DROP_ITEM_DELAY = 6;

    Queue<Destination> destinations = new ArrayDeque<>();
    BlockPos next_destination = null;
    private int drop_item_timer = 0; // Used to space out the dropped items in pen mode.

    public static final EntityDataAccessor<Boolean> PEN_DOWN = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.BOOLEAN);

    public Orb(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }

    private @Nullable Entity getGrabbed() {
        if(getPassengers().isEmpty()) {
            return null;
        }
        return getPassengers().getFirst();
    }

    public boolean grabbing()
    {
        return !getPassengers().isEmpty();
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PEN_DOWN, false);
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
                if(penDown()) {
                    if(getGrabbed() instanceof ItemEntity item_entity) {
                        ItemStack stack = item_entity.getItem();
                        if(stack.getItem() instanceof BlockItem block_item) {
                            BlockPos pos = BlockPos.containing(position());
                            if(level().getBlockState(pos).canBeReplaced()) {
                                BlockState state = block_item.getBlock().getStateForPlacement(new BlockPlaceContext(level(), null, InteractionHand.MAIN_HAND, stack, BlockHitResult.miss(position(), getDirection(), pos)));
                                if(state != null) {
                                    level().setBlock(pos, state, Block.UPDATE_ALL);
                                    stack.shrink(1);
                                }
                            }
                        } else {
                            if(drop_item_timer < 1) {
                                drop_item_timer = DROP_ITEM_DELAY;
                                ItemEntity drop = new ItemEntity(level(), position().x, position().y, position().z, stack.copyWithCount(1));
                                drop.setDeltaMovement(Vec3.ZERO);
                                drop.setPickUpDelay(20);
                                level().addFreshEntity(drop);
                                stack.shrink(1);
                            } else {
                                drop_item_timer--;
                            }
                        }
                        if(stack.isEmpty()) {
                            item_entity.kill();
                        }
                    }
                }
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
        if(grabbing()) {
            if(getGrabbed() != null) {
                getGrabbed().dismountTo(position().x, position().y, position().z);
            }
        } else {
            AABB hitbox = AABB.ofSize(position(), HITBOX_SIZE, HITBOX_SIZE, HITBOX_SIZE);
            List<Entity> in_range = level().getEntities(this, hitbox, entity -> !entity.is(this));
            if(in_range.isEmpty()) {
                return;
            }
            in_range.getFirst().startRiding(this);
        }
    }

    public void togglePen() {
        entityData.set(PEN_DOWN, !entityData.get(PEN_DOWN));
    }

    public boolean penDown() {
        return entityData.get(PEN_DOWN);
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
