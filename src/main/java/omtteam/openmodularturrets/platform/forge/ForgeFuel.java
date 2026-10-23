package omtteam.openmodularturrets.platform.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import omtteam.openmodularturrets.platform.Fuel;

public final class ForgeFuel implements Fuel {
    @Override
    public int burnTime(ItemStack stack) {
        // includes items that override their burn time and Forge's FurnaceFuelBurnTimeEvent
        return stack.isEmpty() ? 0 : Math.max(0, ForgeHooks.getBurnTime(stack, RecipeType.SMELTING));
    }

    @Override
    public ItemStack remainder(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.getCraftingRemainingItem();
    }
}
