package dev.hyperlynx.pulsetech.integration.jei;

import dev.hyperlynx.pulsetech.client.storage.StorageModemScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StorageModemGhostItemHandler implements IGhostIngredientHandler<StorageModemScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(StorageModemScreen gui, @NotNull ITypedIngredient<I> ingredient, boolean doStart) {
        if(!doStart) {
            return List.of();
        }
        return gui.getFilterSlotRectangles().stream().<Target<I>>map(rect -> new FilterListTarget<>(rect, gui)).toList();
    }

    @Override
    public void onComplete() {}

    private record FilterListTarget<I>(Rect2i area, StorageModemScreen screen) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(I ing) {
            if (!(ing instanceof ItemStack stack)) {
                return;
            }
            if(stack.isEmpty()) {
                return;
            }
            screen.onQuickStack(stack);
        }
    }
}
