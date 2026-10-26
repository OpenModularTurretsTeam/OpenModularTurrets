package omtteam.openmodularturrets.platform;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Furnace fuel values of the running loader (vanilla and modded fuels).
 */
public interface Fuel {
    /**
     * Burn time of one item of this stack in ticks, as in a furnace; 0 if empty or no fuel, never negative. The level
     * holds the fuel values on 1.21.11 (they depend on its data and enabled features); older versions ignore it.
     */
    int burnTime(Level level, ItemStack stack);

    /**
     * What one burnt item of this stack leaves behind, as in a furnace (lava bucket → bucket); {@link ItemStack#EMPTY}
     * if nothing. Call it before removing the item; the stack is not changed.
     */
    ItemStack remainder(ItemStack stack);
}
