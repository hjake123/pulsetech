package dev.hyperlynx.pulsetech.feature.storage;

import dev.hyperlynx.pulsetech.registration.ModBlocks;
import dev.hyperlynx.pulsetech.registration.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class StorageModemMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private Consumer<ItemStack> quickStackRoutine;

    /// Network constructor to read the BlockPos from the network.
    public StorageModemMenu(int containerId, Inventory player_inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, player_inventory, ContainerLevelAccess.create(player_inventory.player.level(), buf.readBlockPos()));
    }

    public StorageModemMenu(int containerId, Inventory player_inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.STORAGE_MODEM.value(), containerId);
        this.access = access;

        // Copied from BrewingStandMenu.java
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(player_inventory, j + i * 9 + 9, 8 + j * 18, 113 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(player_inventory, k, 8 + k * 18, 171));
        }
    }

    public void setQuickStackRoutine(Consumer<ItemStack> routine) {
        quickStackRoutine = routine;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot quickMovedSlot = this.slots.get(index);
        if(quickStackRoutine != null && quickMovedSlot.hasItem()) {
            quickStackRoutine.accept(quickMovedSlot.getItem().copyWithCount(1));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(access, player, ModBlocks.STORAGE_MODEM.get());
    }

    public BlockPos getPos() {
        return access.evaluate((level, pos) -> pos).orElseThrow();
    }

}
