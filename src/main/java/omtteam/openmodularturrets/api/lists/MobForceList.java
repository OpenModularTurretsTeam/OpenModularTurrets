package omtteam.openmodularturrets.api.lists;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
@SuppressWarnings("unused")
public class MobForceList extends MobList {
    private static MobForceList instance = null;
    private final List<ResourceLocation> list = new ArrayList<>();

    public static MobForceList getInstance() {
        if (instance == null) {
            instance = new MobForceList();
        }
        return instance;
    }

    @Override
    public String getType() {
        return "MobForce";
    }

    @Override
    protected List<ResourceLocation> getList() {
        return list;
    }
}
