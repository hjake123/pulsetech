package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
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
        return InteractionResult.SUCCESS;
    }
}
