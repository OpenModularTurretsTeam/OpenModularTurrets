package omtteam.openmodularturrets.api.lists;

import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.OpenModularTurrets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
public class AmmoList {
    private static final List<ItemStack> list = new ArrayList<>();

    public static boolean contains(ItemStack itemStack) {
        for (ItemStack stack : list) {
            if (stack.isItemEqual(itemStack) || (stack.getCount() == 2 && itemStack.getItem() == stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static void add(ItemStack itemStack) {
        if (contains(itemStack)) {
            OpenModularTurrets.getLogger().warn("Tried to add duplicate item to ammo list: " + itemStack.getDisplayName());
        } else {
            list.add(itemStack);
        }
    }

    @SuppressWarnings("unused")
    public static boolean remove(ItemStack itemStack) {
        for (ItemStack stack : list) {
            if (stack.isItemEqual(itemStack)) {
                list.remove(stack);
                return true;
            }
        }
        return false;
    }

    public static void clear() {
        list.clear();
    }
}
