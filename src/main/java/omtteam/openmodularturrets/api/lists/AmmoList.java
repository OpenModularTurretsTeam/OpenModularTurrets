package omtteam.openmodularturrets.api.lists;

import net.minecraft.item.ItemStack;

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
            if (stack.isItemEqual(itemStack) || (stack.stackSize == 2 && itemStack.getItem() == stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static boolean add(ItemStack itemStack) {
        if (contains(itemStack)) {
            return false;
        } else {
            list.add(itemStack);
            return true;
        }
    }

    public static boolean remove(ItemStack itemStack) {
        for (ItemStack stack : list) {
            if (stack.isItemEqual(itemStack)) {
                list.remove(stack);
                return true;
            }
        }
        return false;
    }
}
