package omtteam.openmodularturrets.init;


import net.minecraft.item.Item;
import omtteam.openmodularturrets.items.*;

import static omtteam.omlib.util.InitHelper.registerItem;


public class ModItems {
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item usableMetaItem;

    public static void init() {
        intermediateProductTiered = registerItem(new IntermediateProductTiered());
        intermediateProductRegular = registerItem(new IntermediateProductRegular());
        addonMetaItem = registerItem(new AddonMetaItem());
        upgradeMetaItem = registerItem(new UpgradeMetaItem());
        ammoMetaItem = registerItem(new AmmoMetaItem());
        usableMetaItem = registerItem(new UsableMetaItem());
    }
}
