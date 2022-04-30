package omtteam.openmodularturrets.api.lists;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import omtteam.omlib.util.EntityUtil;
import omtteam.openmodularturrets.OpenModularTurrets;

import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
@SuppressWarnings("unused")
public abstract class MobList {

    public boolean contains(ResourceLocation mobIn) {
        for (ResourceLocation mob : getList()) {
            if (mob.equals(mobIn)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(EntityLivingBase mobIn) {
        return contains(EntityList.getKey(mobIn));
    }

    public boolean add(ResourceLocation mobIn) {
        if (contains(mobIn) || EntityUtil.findClassById(mobIn) == null) {
            OpenModularTurrets.getLogger().warn("Failed to add mob to " + getType() + "list: " + mobIn);
            return false;
        } else {
            getList().add(mobIn);
            return true;
        }
    }

    public boolean remove(ResourceLocation mobIn) {
        for (ResourceLocation mob : getList()) {
            if (mob.equals(mobIn)) {
                getList().remove(mobIn);
                return true;
            }
        }
        return false;
    }

    public void clear() {
        getList().clear();
    }

    public abstract String getType();

    protected abstract List<ResourceLocation> getList();
}
