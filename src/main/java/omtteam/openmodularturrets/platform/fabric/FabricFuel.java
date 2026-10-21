package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.platform.Fuel;

public final class FabricFuel implements Fuel {
    @Override
    public int burnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Integer ticks = FuelRegistry.INSTANCE.get(stack.getItem());
        return ticks == null ? 0 : Math.max(0, ticks);
    }

    @Override
    public ItemStack remainder(ItemStack stack) {
        // FabricItem#getRecipeRemainder, the stack-aware remainder hook of Fabric API
        return stack.isEmpty() ? ItemStack.EMPTY : stack.getItem().getRecipeRemainder(stack);
    }
}
