package omtteam.openmodularturrets.turret;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import omtteam.omlib.core.energy.EnergyAccess;
import omtteam.omlib.power.ItemEnergy;

import java.util.ArrayList;
import java.util.List;

/**
 * The items the energy drain turret takes energy from (issue 324, user decision): a player's armour, then the item in
 * the main hand, then the offhand item. Other inventory slots are left alone, and mobs are never drained.
 */
public final class DrainableItems {
    /** As an inventory, the player's 36 main slots are followed by the 4 armour slots and the offhand slot. */
    private static final int MAIN_SLOTS = 36;
    private static final int ARMOR_SLOTS = 4;
    private static final int OFFHAND_SLOT = MAIN_SLOTS + ARMOR_SLOTS;

    private DrainableItems() {
    }

    /** The energy of those items, in drain order; items without energy are left out. */
    public static List<EnergyAccess> of(EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;
        List<EnergyAccess> items = new ArrayList<>();
        for (int i = 0; i < ARMOR_SLOTS; i++) add(items, inventory, MAIN_SLOTS + i);
        add(items, inventory, inventory.currentItem);
        add(items, inventory, OFFHAND_SLOT);
        return items;
    }

    /** Whether at least one of those items has energy that can be extracted. */
    public static boolean any(EntityPlayer player) {
        for (EnergyAccess item : of(player)) {
            if (item.extract(1, true) > 0) return true;
        }
        return false;
    }

    private static void add(List<EnergyAccess> items, InventoryPlayer inventory, int slot) {
        ItemEnergy.of(inventory, slot).ifPresent(items::add);
    }
}
