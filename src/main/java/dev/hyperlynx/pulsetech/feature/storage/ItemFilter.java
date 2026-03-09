package dev.hyperlynx.pulsetech.feature.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// A single filter for a particular item. Used by the storage blocks to scan inventories for matching item stacks.
public record ItemFilter(ItemStack item, boolean match_data) {
    public static final Codec<ItemFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("item").forGetter(ItemFilter::itemToCodec),
            Codec.BOOL.fieldOf("match_data").forGetter(ItemFilter::match_data)
    ).apply(instance, ItemFilter::fromCodec));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static ItemFilter fromCodec(Optional<ItemStack> potential_item, Boolean match_data) {
        return new ItemFilter(potential_item.orElse(ItemStack.EMPTY), match_data);
    }

    private Optional<ItemStack> itemToCodec() {
        if(item.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    public boolean matches(ItemStack stack) {
        if(item.isEmpty()) {
            return true;
        }
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

    public Component getFilterLabel() {
        return item().isEmpty() ? Component.translatable("pulsetech.any_item") :
                match_data() ? item.getHoverName() : item.getItem().getName(item.getItem().getDefaultInstance());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemFilter(ItemStack other_item, boolean other_match_data))) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(this.item(), other_item) && this.match_data() == other_match_data;
    }
}
