package omtteam.openmodularturrets.api.lists;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 16/11/17.
 * This Class
 */
@SuppressWarnings("unused")
public class NeutralList extends MobList {
    private static NeutralList instance = null;
    private final List<ResourceLocation> list = new ArrayList<>();

    public static NeutralList getInstance() {
        if (instance == null) {
            instance = new NeutralList();
        }
        return instance;
    }

    @Override
    public String getType() {
        return "NeutralForce";
    }

    @Override
    protected List<ResourceLocation> getList() {
        return list;
    }
}
