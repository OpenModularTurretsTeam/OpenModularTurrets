package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.platform.Fuel;

public final class NeoForgeFuel implements Fuel {
    @Override
    public int burnTime(Level level, ItemStack stack) {
        // includes NeoForge's furnace_fuels data map and items that override their burn time
        //? if >=1.21.11 {
        /*return stack.isEmpty() ? 0 : Math.max(0, stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()));
        *///?} else {
        return stack.isEmpty() ? 0 : Math.max(0, stack.getBurnTime(RecipeType.SMELTING));
        //?}
    }

    @Override
    public ItemStack remainder(ItemStack stack) {
        //? if >=1.21.11 {
        /*return stack.isEmpty() ? ItemStack.EMPTY : stack.getCraftingRemainder();
        *///?} else {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.getCraftingRemainingItem();
        //?}
    }
}
