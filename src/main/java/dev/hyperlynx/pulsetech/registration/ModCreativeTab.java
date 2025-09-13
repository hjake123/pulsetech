package dev.hyperlynx.pulsetech.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hyperlynx.pulsetech.Pulsetech.MODID;

public class ModCreativeTab {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "pulsetech" namespace
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "pulsetech:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = TABS.register("tab",
            () -> CreativeModeTab.builder().title(Component.translatable("pulsetech.tab"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.EXAMPLE_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) ->
                            ModItems.ITEMS.getEntries().forEach(holder ->
                                    output.accept(holder.get())
                            )
                    ).build());
}
