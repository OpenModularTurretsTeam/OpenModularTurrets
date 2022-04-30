package omtteam.openmodularturrets.api.lists;

import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.OpenModularTurrets;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
public class AmmoList {
    private static final Map<ItemStack, Integer> map = new HashMap<>();

    public static int getDamage(ItemStack itemStack) { // Returns 0 if not valid ammo.
        for (Map.Entry<ItemStack, Integer> entry : map.entrySet()) {
            if (entry.getKey().isItemEqual(itemStack) || (entry.getKey().getCount() == 2 && itemStack.getItem() == entry.getKey().getItem())) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static void add(ItemStack itemStack, int damage) {
        if (getDamage(itemStack) > 0) {
            OpenModularTurrets.getLogger().warn("Tried to add duplicate item to ammo list: " + itemStack.getDisplayName());
        } else {
            map.put(itemStack, damage);
        }
    }

    @SuppressWarnings("unused")
    public static boolean remove(ItemStack itemStack) {
        for (Map.Entry<ItemStack, Integer> entry : map.entrySet()) {
            if (entry.getKey().isItemEqual(itemStack)) {
                map.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }

    public static void clear() {
        map.clear();
    }
}
