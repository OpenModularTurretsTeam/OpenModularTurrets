package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import omtteam.openmodularturrets.platform.Fuel;

public final class NeoForgeFuel implements Fuel {
    @Override
    public int burnTime(ItemStack stack) {
        // includes NeoForge's furnace_fuels data map and items that override their burn time
        return stack.isEmpty() ? 0 : Math.max(0, stack.getBurnTime(RecipeType.SMELTING));
    }

    @Override
    public ItemStack remainder(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.getCraftingRemainingItem();
    }
}
