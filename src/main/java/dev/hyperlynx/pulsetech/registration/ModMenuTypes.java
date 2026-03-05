package dev.hyperlynx.pulsetech.registration;

import dev.hyperlynx.pulsetech.feature.storage.StorageModemBlock;
import dev.hyperlynx.pulsetech.feature.storage.StorageModemMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StorageModemMenu>> STORAGE_MODEM = TYPES.register("storage_modem", () ->
            IMenuTypeExtension.create(StorageModemMenu::new));
}
