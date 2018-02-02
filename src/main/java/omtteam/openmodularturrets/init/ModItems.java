package omtteam.openmodularturrets.init;


import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.openmodularturrets.items.*;

import java.util.ArrayList;
import java.util.List;

import static omtteam.omlib.util.InitHelper.registerItem;


public class ModItems {
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item usableMetaItem;
    public static final List<Item> subblocks = new ArrayList<>();

    public static void init(IForgeRegistry<Item> registry) {
        intermediateProductTiered = registerItem(new IntermediateProductTiered(), registry);
        intermediateProductRegular = registerItem(new IntermediateProductRegular(), registry);
        addonMetaItem = registerItem(new AddonMetaItem(), registry);
        upgradeMetaItem = registerItem(new UpgradeMetaItem(), registry);
        ammoMetaItem = registerItem(new AmmoMetaItem(), registry);
        usableMetaItem = registerItem(new UsableMetaItem(), registry);

        for (Item item: subblocks) {
            registry.register(item);
        }
    }
}
