package omtteam.openmodularturrets.turret;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import omtteam.omlib.core.energy.EnergyAccess;
import omtteam.omlib.platform.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * The items the energy drain turret takes energy from (issue 324, user decision): a player's armour, then the item in
 * the main hand, then the offhand item. Other inventory slots are left alone, and mobs are never drained.
 */
public final class DrainableItems {
    /** The four armour slots follow the 36 slots of the main inventory, as on every Minecraft version. */
    private static final int ARMOR_SLOTS = 4;

    private DrainableItems() {
    }

    /** The energy of those items, in drain order; items without energy are left out. */
    public static List<EnergyAccess> of(Player player) {
        Inventory inventory = player.getInventory();
        List<EnergyAccess> items = new ArrayList<>();
        for (int i = 0; i < ARMOR_SLOTS; i++) add(items, inventory, Inventory.INVENTORY_SIZE + i);
        add(items, inventory, inventory.selected);
        add(items, inventory, Inventory.SLOT_OFFHAND);
        return items;
    }

    /** Whether at least one of those items has energy that can be extracted. */
    public static boolean any(Player player) {
        for (EnergyAccess item : of(player)) {
            if (item.extract(1, true) > 0) return true;
        }
        return false;
    }

    private static void add(List<EnergyAccess> items, Inventory inventory, int slot) {
        Platform.energy().itemEnergy(inventory, slot).ifPresent(items::add);
    }
}
