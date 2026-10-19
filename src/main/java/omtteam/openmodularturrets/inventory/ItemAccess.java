package omtteam.openmodularturrets.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Slots of a container as other blocks see them (hoppers, pipes), free of loaders. The loader adapters in
 * {@code platform/<loader>} expose it; the container's own {@link Container#canPlaceItem} always applies as well.
 *
 * @param firstSlot  first exposed slot of the container
 * @param slotCount  number of exposed slots from {@code firstSlot} on
 * @param canInsert  which items other blocks may put in
 * @param canExtract which items other blocks may take out
 */
public record ItemAccess(Container container, int firstSlot, int slotCount,
                         Predicate<ItemStack> canInsert, Predicate<ItemStack> canExtract) {
    /**
     * All slots; anything the container accepts goes in, nothing comes out.
     */
    public static ItemAccess insertOnly(Container container) {
        return new ItemAccess(container, 0, container.getContainerSize(), stack -> true, stack -> false);
    }
}
