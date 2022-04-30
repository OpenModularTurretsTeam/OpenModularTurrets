package omtteam.openmodularturrets.api.lists;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
@SuppressWarnings("unused")
public class MobBlockList extends MobList {
    private static MobBlockList instance = null;
    private final List<ResourceLocation> list = new ArrayList<>();

    public static MobBlockList getInstance() {
        if (instance == null) {
            instance = new MobBlockList();
        }
        return instance;
    }

    @Override
    public String getType() {
        return "MobBlock";
    }

    @Override
    protected List<ResourceLocation> getList() {
        return list;
    }
}
