package omtteam.openmodularturrets.init;


import net.minecraft.item.Item;
import omtteam.openmodularturrets.items.*;

import static omtteam.omlib.util.ReflectionInitHelper.registerItems;


public class ModItems {
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item usableMetaItem;

    public static void init() {
        intermediateProductTiered = new IntermediateProductTiered();
        intermediateProductRegular = new IntermediateProductRegular();
        addonMetaItem = new AddonMetaItem();
        upgradeMetaItem = new UpgradeMetaItem();
        ammoMetaItem = new AmmoMetaItem();
        usableMetaItem = new UsableMetaItem();
        registerItems(ModItems.class);
    }
}
