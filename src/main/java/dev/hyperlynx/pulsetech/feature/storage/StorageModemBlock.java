package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import dev.hyperlynx.pulsetech.registration.ModItems;
import dev.hyperlynx.pulsetech.registration.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class StorageModemBlock extends PulseBlock {
    public static final BooleanProperty SYNCING = BooleanProperty.create("syncing");

    public StorageModemBlock(Properties properties, SideIO io) {
        super(properties, io, false);
        registerDefaultState(defaultBlockState().setValue(SYNCING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SYNCING);
    }

    public static boolean isSyncing(BlockState state) {
        return state.getValue(SYNCING);
    }

    public static void setSyncing(Level level, BlockPos pos, BlockState state, boolean syncing) {
        level.setBlock(pos, state.setValue(SYNCING, syncing), Block.UPDATE_ALL_IMMEDIATE);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return pulseCodec(StorageModemBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new StorageModemBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) {
           return InteractionResult.SUCCESS;
        }
        if(!(level.getBlockEntity(pos) instanceof StorageModemBlockEntity modem)) {
            return InteractionResult.SUCCESS;
        }
        openMenu(level, player, modem);
        return InteractionResult.SUCCESS;
    }

    public static void openMenu(Level level, Player player, StorageModemBlockEntity modem) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("menu.title.pulsetech.storage_modem");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return new StorageModemMenu(id, inventory, ContainerLevelAccess.create(level, modem.getBlockPos()));
            }
        }, buf -> buf.writeBlockPos(modem.getBlockPos()));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(ModItems.DATASHEET)) {
            return ItemInteractionResult.FAIL;
        }
        if(level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if(level.getBlockEntity(pos) instanceof StorageModemBlockEntity modem && stack.is(ModItems.DATA_CELL)) {
            level.playSound(null, pos, ModSounds.BEEP.value(), SoundSource.PLAYERS, 1.0F, 1.0F + level.random.nextFloat() * 0.05F);
            if(!stack.has(ModComponentTypes.ITEM_FILTERS)) {
                stack.set(ModComponentTypes.ITEM_FILTERS, modem.getFilters());
                player.displayClientMessage(Component.translatable("pulsetech.copied_filters"), true);
                return ItemInteractionResult.FAIL;
            }

            List<ItemFilter> stored_filters = Objects.requireNonNull(stack.get(ModComponentTypes.ITEM_FILTERS));
            if(modem.getFilters().equals(stored_filters)) {
                player.displayClientMessage(Component.translatable("pulsetech.filters_already_match"), true);
                return ItemInteractionResult.FAIL;
            }

            if(modem.getFilters().size() == 1 && modem.getFilters().getFirst().item().isEmpty()) {
                // This is an empty filter list, so don't bother copying it to the data cell.
                player.displayClientMessage(Component.translatable("pulsetech.pasted_filters"), true);
            } else {
                stack.set(ModComponentTypes.ITEM_FILTERS, modem.getFilters());
                player.displayClientMessage(Component.translatable("pulsetech.swapped_filters"), true);
            }
            modem.setFilters(stored_filters);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
