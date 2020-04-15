package omtteam.openmodularturrets.api.lists;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import omtteam.omlib.util.EntityUtil;
import omtteam.openmodularturrets.OpenModularTurrets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
@SuppressWarnings("unused")
public class MobBlacklist {
    private static final List<String> list = new ArrayList<>();

    public static boolean contains(String mobIn) {
        for (String mob : list) {
            if (mob.equalsIgnoreCase(mobIn)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(EntityLivingBase mobIn) {
        return contains(EntityList.getEntityString(mobIn));
    }

    public static void add(String mobIn) {
        if (contains(mobIn) || EntityUtil.findClassById(mobIn) == null) {
            OpenModularTurrets.getLogger().warn("Tried to add duplicate mob to mob list: " + mobIn);
        } else {
            list.add(mobIn);
        }
    }

    public static boolean remove(String mobIn) {
        for (String mob : list) {
            if (mob.equalsIgnoreCase(mobIn)) {
                list.remove(mobIn);
                return true;
            }
        }
        return false;
    }

    public static void clear() {
        list.clear();
    }
}
