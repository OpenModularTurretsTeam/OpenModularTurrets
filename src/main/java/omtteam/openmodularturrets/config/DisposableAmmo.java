package omtteam.openmodularturrets.config;

import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import omtteam.openmodularturrets.reference.Reference;

/**
 * Created by Keridos on 16/11/17.
 * The items the disposable turret may fire and their damage bonus (config {@code stringAmmoAllowList}; 3.x
 * {@code api.lists.AmmoList}). An entry with count 2 matches the item with any metadata; {@code ore:} entries match every
 * item with that ore dictionary name (looked up when asked, so items other mods register later count too).
 */
public final class DisposableAmmo {
    private static final Map<ItemStack, Integer> map = new HashMap<>();
    private static final Map<String, Integer> oreMap = new HashMap<>();

    private DisposableAmmo() {
    }

    public static int getDamage(ItemStack itemStack) { // Returns 0 if not valid ammo.
        for (Map.Entry<ItemStack, Integer> entry : map.entrySet()) {
            if (entry.getKey().isItemEqual(itemStack) || (entry.getKey().getCount() == 2 && itemStack.getItem() == entry.getKey().getItem())) {
                return entry.getValue();
            }
        }
        if (!oreMap.isEmpty() && !itemStack.isEmpty()) {
            for (int id : OreDictionary.getOreIDs(itemStack)) {
                Integer damage = oreMap.get(OreDictionary.getOreName(id));
                if (damage != null) return damage;
            }
        }
        return 0;
    }

    public static void addOre(String oreName, int damage) {
        if (oreMap.containsKey(oreName)) {
            Reference.LOGGER.warn("Tried to add duplicate ore to ammo list: " + oreName);
        } else {
            oreMap.put(oreName, damage);
        }
    }

    public static void add(ItemStack itemStack, int damage) {
        if (getDamage(itemStack) > 0) {
            Reference.LOGGER.warn("Tried to add duplicate item to ammo list: " + itemStack.getDisplayName());
        } else {
            map.put(itemStack, damage);
        }
    }

    public static void clear() {
        map.clear();
        oreMap.clear();
    }
}
