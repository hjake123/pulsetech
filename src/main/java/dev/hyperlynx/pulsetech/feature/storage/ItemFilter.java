package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/// A single filter for a particular item. Used by the storage blocks to scan inventories for matching item stacks.
public record ItemFilter(ItemStack item, boolean match_data) {
    public static final Codec<ItemFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(ItemFilter::item),
            Codec.BOOL.fieldOf("match_data").forGetter(ItemFilter::match_data)
    ).apply(instance, ItemFilter::new));

    public boolean matches(ItemStack stack) {
        if(!ItemStack.isSameItem(stack, item)) {
            return false;
        }
        if(!match_data) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(stack, item);
    }

    @Override
    public @NotNull String toString() {
        if(!match_data) {
            return item.getDisplayName().getString();
        }
        return item.getDisplayName().getString() + Component.translatable("pulsetech.with").getString() + item.getComponentsPatch().size() + Component.translatable("pulsetech.components").getString();
    }
}
