package dev.hyperlynx.pulsetech.feature.orb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.print.attribute.standard.Destination;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class Orb extends Entity {
    private static final float SPEED = 0.12F;
    private static final float HITBOX_SIZE = 1.5F;
    private static final int DROP_ITEM_DELAY = 6;

    Queue<Destination> destinations = new ArrayDeque<>();
    private int drop_item_timer = 0; // Used to space out the dropped items in pen mode.

    public static final EntityDataAccessor<Boolean> PEN_DOWN = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> PROJECTILE = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> NEXT_DESTINATION = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> HAS_DESTINATION = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.BOOLEAN);

    public Orb(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PEN_DOWN, false);
        builder.define(PROJECTILE, false);
        builder.define(NEXT_DESTINATION, BlockPos.ZERO);
        builder.define(HAS_DESTINATION, false);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(this.isInvulnerableTo(source)) {
            return false;
        }
        kill();
        return true;
    }

    public @Nullable Entity getGrabbed() {
        if(getPassengers().isEmpty()) {
            return null;
        }
        return getPassengers().getFirst();
    }
    public boolean grabbing()
    {
        return !getPassengers().isEmpty();
    }

    public Vec3 getStep() {
        Vec3 destination_pos = nextDestination().getCenter();
        Vec3 displacement = destination_pos.subtract(this.position());
        return displacement.normalize().multiply(SPEED, SPEED, SPEED);
    }

    @Override
    public void tick() {
        super.tick();
        if(!hasNextDestination()) {
            if(!destinations.isEmpty()) {
                setNextDestination(destinations.poll().getPosition(blockPosition()));
            }
        }

        if(hasNextDestination()) {
            if(position().closerThan(Objects.requireNonNull(nextDestination()).getCenter(), SPEED + 0.01)) {
                clearNextDestination();
            } else {
                this.move(MoverType.SELF, getStep());
                if(penDown()) {
                    // Run pen down effects
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
        if(isProjectile()) {
            // Act as projectile
            if(level().getBlockCollisions(this, this.getBoundingBox()).iterator().hasNext()) {
                burst();
            }

            List<Entity> collided_entities = level().getEntities(this, this.getBoundingBox(), Objects::nonNull);
            if(!collided_entities.isEmpty()) {
                Entity victim = collided_entities.getFirst();
                victim.hurt(damageSources().magic(), 4.0F);
                burst();
            }
        }
    }

    public void burst() {
        level().explode(this, getX(), getY(), getZ(), 0.5F, Level.ExplosionInteraction.NONE);
        kill();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("Destinations")) {
            destinations = new ArrayDeque<>(Destination.CODEC.listOf().decode(NbtOps.INSTANCE, tag.get("Destinations")).getOrThrow().getFirst());
        }
        if(tag.contains("NextDestination")) {
            setNextDestination(NbtUtils.readBlockPos(tag, "NextDestination").orElse(null));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if(!destinations.isEmpty()) {
            tag.put("Destinations", Destination.CODEC.listOf().encodeStart(NbtOps.INSTANCE, destinations.stream().toList()).getOrThrow());
        }
        if(hasNextDestination()) {
            tag.put("NextDestination", NbtUtils.writeBlockPos(Objects.requireNonNull(nextDestination())));
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

    public void toggleProjectile() {
        entityData.set(PROJECTILE, !entityData.get(PROJECTILE));
    }

    public boolean isProjectile() {
        return entityData.get(PROJECTILE);
    }

    public @Nullable BlockPos nextDestination() {
        if(!hasNextDestination()) {
            return null;
        }
        return entityData.get(NEXT_DESTINATION);
    }

    public boolean hasNextDestination() {
        return entityData.get(HAS_DESTINATION);
    }

    public void setNextDestination(BlockPos pos) {
        entityData.set(NEXT_DESTINATION, pos);
        entityData.set(HAS_DESTINATION, true);
    }

    public void clearNextDestination() {
        entityData.set(HAS_DESTINATION, false);
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
